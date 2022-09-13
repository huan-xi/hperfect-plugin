package com.gitee.hperfect.yapi.parse.parser.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.gitee.hperfect.settings.AppSettingsState;
import com.gitee.hperfect.utils.MessageUtils;
import com.gitee.hperfect.utils.YapiTypeUtils;
import com.gitee.hperfect.yapi.config.AnnotationCons;
import com.gitee.hperfect.yapi.model.ApiParamModelNode;
import com.gitee.hperfect.yapi.parse.GenericType;
import com.gitee.hperfect.yapi.parse.ParseUtils;
import com.gitee.hperfect.yapi.parse.parser.ApiModelParamParser;
import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/4 10:45 上午
 */
public class DefaultApiModelParamParser implements ApiModelParamParser {

    private static final List<String> IGNORE_PACKAGE = CollUtil.toList("org.springframework.web", "javax.servlet.http");

    @Override
    public ApiParamModelNode parseMethodFiled(PsiParameter psiParameter, PsiMethod method, Project project) {
        String typeName = psiParameter.getType().getCanonicalText();
        PsiType type = psiParameter.getType();
        //忽略一些自动注入的类型
        if (isIgnore(typeName)) {
            return null;
        }
        //map 类型
        if (YapiTypeUtils.isNormalType(typeName)) {
            //普通参数解析
            return parseNormalType(psiParameter, typeName, method);
        } else if (YapiTypeUtils.isArrayType(typeName)) {
            //解析泛型List<String>
            GenericType parse = GenericType.parse(typeName);
            ApiParamModelNode info = parseNormalType(psiParameter, typeName, method);
            ApiParamModelNode apiParamModelNode = parseObjectType(parse, project);
            String desc = info.getDesc();
            desc = String.format("%s(数组类型%s)", desc, ParseUtils.removePackage(parse.getGenericClass()));
            //解析给数组
            info.setDesc(desc);
            info.setParamModelList(apiParamModelNode.getParamModelList());
            return info;
        } else {
            if (type instanceof PsiClassReferenceType) {
                PsiClass psiClass = ((PsiClassReferenceType) type).resolve();
                if (psiClass != null && psiClass.isEnum()) {
                    ApiParamModelNode apiParamModelNode = parseNormalType(psiParameter, "enum", method);
                    apiParamModelNode.setDesc(parseEnumDesc(apiParamModelNode.getDesc(), psiClass));
                    return apiParamModelNode;
                }
            }
            //对象类型
            return parseObjectType(GenericType.parse(typeName), project);
        }
    }

    @Override
    public ApiParamModelNode parseObjectType(GenericType genericType, Project project) {
        return parseObjectType(genericType, project, null);
    }

    public String parseEnumDesc(String desc, PsiClass psiClass) {
        desc = desc == null ? "" : desc;
        //枚举类型
        PsiField[] fields = psiClass.getAllFields();
        List<PsiField> fieldList = Arrays.stream(fields).filter(f -> f instanceof PsiEnumConstant).collect(Collectors.toList());
        StringBuilder remarkBuilder = new StringBuilder();
        for (PsiField psiField : fieldList) {
            String comment = ParseUtils.getJavaDoc(psiField.getDocComment());
            comment = "" + (StrUtil.isBlank(comment) ? "暂无注释" : comment);
            remarkBuilder.append(psiField.getName()).append(comment);
            remarkBuilder.append(";");
        }
        return String.format("%s{枚举值:%s}", desc, remarkBuilder.toString());
    }

    public boolean isIgnore(String typeName) {
        for (String strType : IGNORE_PACKAGE) {
            if (typeName.contains(strType)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 解析参数描述
     *
     * @param method
     * @param apiParam
     * @param psiParameter
     * @return
     */
    public static String parseParamDesc(PsiMethod method, PsiAnnotation apiParam, PsiParameter psiParameter) {
        String desc = "";
        if (apiParam != null) {
            desc = ParseUtils.getPsiAnnotationValue(apiParam);
        }
        if (StrUtil.isBlank(desc)) {
            //从注释中获取参数描述
            desc = ParseUtils.getParamDesc(method, psiParameter.getName()) + "(" + psiParameter.getType().getPresentableText() + ")";
        }


        return desc;
    }

    /**
     * 解析参数名称
     *
     * @param parameter
     * @param param
     * @param requestParam
     * @return
     */
    public static String parseName(PsiParameter parameter, PsiAnnotation param, PsiAnnotation requestParam) {
        String name = "";
        if (requestParam != null) {
            //解析参数名称
            name = ParseUtils.getPsiAnnotationValue(requestParam);
        }
        if (StrUtil.isBlank(name)) {
            name = parameter.getName();
        }
        return name;
    }

    /**
     * 解析基本类型字段
     *
     * @param psiParameter
     * @param type
     * @param method
     * @return
     */
    public static ApiParamModelNode parseNormalType(PsiParameter psiParameter, String type, PsiMethod method) {
        ApiParamModelNode apiParamModel = new ApiParamModelNode();
        //api参数
        PsiAnnotation apiParam = psiParameter.getAnnotation(AnnotationCons.API_PARAM);
        PsiAnnotation requestParam = psiParameter.getAnnotation(AnnotationCons.REQUEST_PARAM);

        //api模型字段解析
        apiParamModel.setType(type);
        //解析名称
        apiParamModel.setName(parseName(psiParameter, requestParam, apiParam));
        //解析描述(注释,swagger)
        apiParamModel.setDesc(parseParamDesc(method, apiParam, psiParameter));
        if (requestParam != null) {
            //解析是否必填(param)
            String defaultValue = ParseUtils.getPsiAnnotationValueByName(requestParam, "defaultValue");
            apiParamModel.setDefaultValue(defaultValue);
        }
        //默认值
        return apiParamModel;
    }

    private static PsiClass mapClass;

    public static PsiClass getMapClass(Project project) {

        if (mapClass == null) {
            PsiClass clazz = JavaPsiFacade.getInstance(project).findClass("java.util.Map", GlobalSearchScope.allScope(project));
            if (clazz != null) {
                mapClass = clazz;
            }
        }

        return mapClass;
    }

    /**
     * 解析对象类型
     *
     * @param genericType
     * @param project
     * @param apiParamModelNode 用于递归传递(如果不为空将解析到当前node上)
     * @return
     */
    public ApiParamModelNode parseObjectType(GenericType genericType, Project project, ApiParamModelNode apiParamModelNode) {
        if (apiParamModelNode == null) {
            apiParamModelNode = new ApiParamModelNode();
            apiParamModelNode.setType("Object");
        }
        System.out.printf("查找对象%s\n", genericType.getClassName());
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(genericType.getClassName(), GlobalSearchScope.allScope(project));
        if (psiClass == null) {
            MessageUtils.info(project, "未找到class:" + genericType.getClassName());
            return null;
        }
        System.out.printf("找到对象,开始解析对象%s\n", psiClass.getName());
        //如果是枚举
        if (psiClass.isEnum()) {
            //开始解析枚举属性;
            apiParamModelNode.setType("enum");
            apiParamModelNode.setDesc(parseEnumDesc(apiParamModelNode.getDesc(), psiClass));
            return apiParamModelNode;
        }
        if (genericType.isArray()) {
            //泛型中的泛型 List<Result>
            //name 占位
            apiParamModelNode.setType("array");
            ApiParamModelNode arrayNode = parseObjectType(GenericType.parse(genericType.getGenericClass()), project);
            if (arrayNode == null) {
                arrayNode = new ApiParamModelNode();
            }
            arrayNode.setName(apiParamModelNode.getName());
            arrayNode.setType(genericType.getGenericClass());
            apiParamModelNode.getParamModelList()
                    .add(arrayNode);
            return apiParamModelNode;
        }
        //获取排除属性
        List<String> excludeFieldList = new ArrayList<>();
        List<String> pointList = new ArrayList<>();
        //配置排除
        AppSettingsState instance = AppSettingsState.getInstance(project);
        String excludeFields = instance.getExcludeFields();
        if (StrUtil.isNotBlank(excludeFields)) {
            excludeFieldList.addAll(StrUtil.split(excludeFields, ','));
        }
        //模型注解排除
        String apiParam = ParseUtils.getJavaDocTagValue(psiClass.getDocComment(), "apiParam");
        parseSetField(excludeFieldList, pointList, apiParam);
        //解析字段类型
        for (PsiField field : psiClass.getAllFields()) {
            if (field.getModifierList() != null && field.getModifierList().hasModifierProperty("final")) {
                continue;
            }
            if (isSkip(excludeFieldList, pointList, field)) {
                continue;
            }
            String filedTypeName = field.getType().getCanonicalText();
            System.out.printf("开始解析对象%s,属性:%s,类型:%s\n", psiClass.getName(), field.getName(), filedTypeName);
            //如果当前类型是泛型 替换成泛型真实类型
            if (YapiTypeUtils.GENERIC_LIST.contains(filedTypeName)) {
                GenericType genericTypeType = GenericType.parse(genericType.getGenericClass());
                ApiParamModelNode propNode = new ApiParamModelNode();
                propNode.setName(field.getName());
                propNode.setDesc(parsePropDesc(field));
                //解析参数到属性上
                parseObjectType(genericTypeType, project, propNode);

                apiParamModelNode
                        .getParamModelList()
                        .add(propNode);
                continue;
            }
            //map 类型
            if (YapiTypeUtils.isMapType(GenericType.parse(filedTypeName).getClassName())) {
                apiParamModelNode.getParamModelList()
                        .add(parseObjectOrNormal(field, GenericType.parse(filedTypeName), project));
                continue;
            }
            //是否是基本类型
            GenericType filedGenericType = GenericType.parse(filedTypeName);
            if (StrUtil.isNotBlank(filedGenericType.getGenericClass())) {
                //替换泛型,多泛型未处理 List<T> -> List<具体类>
                if (YapiTypeUtils.GENERIC_LIST.contains(filedGenericType.getGenericClass())) {
                    filedGenericType.setGenericClass(genericType.getGenericClass());
                }
                if (filedGenericType.isArray()) {
                    //数组属性名称和描述
                    ApiParamModelNode arrayNode = parseFiledArray(field, filedGenericType, project);
                    apiParamModelNode.getParamModelList().add(arrayNode);
                    continue;
                }
                ApiParamModelNode node = parseObjectType(filedGenericType, project, apiParamModelNode);
                if (node != null) {
                    apiParamModelNode.getParamModelList().add(node);
                }
                //该字段解析完成
                continue;
            }
            //如果是(当前类)泛型 T t 处理
            String genericClass = genericType.getGenericClass();
            if (genericType.isGeneric()) {
                if (StrUtil.isBlank(genericClass) || "?".equals(genericClass)) {
                    //未知泛型处理
                    filedTypeName = "java.lang.Object";
                } else {
                    //按外层解析泛型处理
                    filedTypeName = genericType.getGenericClass();
                }
                if (YapiTypeUtils.isArrayType(genericClass)) {
                    System.out.printf("开始解析对象属性%s,数组属性\n", field.getName());
                    ApiParamModelNode arrayNode = parseFiledArray(field, GenericType.parse(genericClass), project);
                    apiParamModelNode.getParamModelList().add(arrayNode);
                    continue;
                }
            }
            ApiParamModelNode propNode = parseObjectOrNormal(field, GenericType.parse(filedTypeName), project);
            apiParamModelNode.getParamModelList()
                    .add(propNode);
        }

        return apiParamModelNode;
    }

    private boolean isSkip(List<String> excludeFieldList, List<String> pointList, PsiField field) {
        if (CollUtil.isNotEmpty(pointList)) {
            //指定优先
            return !pointList.contains(field.getName());
        } else if (CollUtil.isNotEmpty(excludeFieldList)) {
            //排除
            return excludeFieldList.contains(field.getName());
        }
        return false;
    }

    /**
     * 解析过滤字段
     *
     * @param excludeFieldList
     * @param pointList
     * @param apiParam
     */
    private void parseSetField(List<String> excludeFieldList, List<String> pointList, String apiParam) {
        if (StrUtil.isNotBlank(apiParam)) {
            if (apiParam.startsWith("!")) {
                excludeFieldList.addAll(StrUtil.split(apiParam, ','));
            } else {
                pointList.addAll(StrUtil.split(apiParam.substring(1), '1'));
            }
        }
    }

    /**
     * 解析对象或基本类型
     *
     * @param field
     * @param genericType
     * @param project
     * @return
     */
    public ApiParamModelNode parseObjectOrNormal(PsiField field, GenericType genericType, Project project) {
        ApiParamModelNode apiParamModelNode = parseFieldNormalType(field, genericType.getClassName());
        if (YapiTypeUtils.isNormalType(genericType.getClassName())) {
            return apiParamModelNode;
        } else {
            return parseObjectType(genericType, project, apiParamModelNode);
        }
    }

    /**
     * 解析字段数组类型
     *
     * @param genericType List<ZZZ> 泛型类型
     * @param project
     * @return
     */
    private ApiParamModelNode parseFiledArray(PsiField field, GenericType genericType, Project project) {
        ApiParamModelNode paramModelNode = parseFieldNormalType(field, "array");
        String genericClass = genericType.getGenericClass();
        if (StrUtil.isBlank(genericClass) || "?".equals(genericClass)) {
            //未知泛型处理
            genericType.setClassName("java.lang.Object");
        }
        ApiParamModelNode apiParamModelNode = parseObjectOrNormal(field, GenericType.parse(genericClass), project);
        paramModelNode.setParamModelList(ListUtil.toList(apiParamModelNode));
        return paramModelNode;
    }

    /**
     * 解析对象属性desc
     *
     * @param field
     * @return
     */
    public static String parsePropDesc(PsiField field) {
        String desc = "";
        String notes = "";
        //desc 获取
        PsiAnnotation apiModelProperty = field.getAnnotation(AnnotationCons.API_MODEL_PROPERTY);
        //@ApiModelProperty 注解中获取
        if (apiModelProperty != null) {
            desc = ParseUtils.getPsiAnnotationValue(apiModelProperty);
            notes = ParseUtils.getPsiAnnotationValueByName(apiModelProperty, "notes");
        }
        if (StrUtil.isBlank(desc) && field.getDocComment() != null) {
            //注释中获取
            desc = ParseUtils.getJavaDoc(field.getDocComment().getText());
        }
        if (StrUtil.isNotBlank(notes)) {
            desc = desc + "(" + notes + ")";
        }
        return desc;
    }


    /**
     * 从字段中解析普通参数类型
     *
     * @param field    数组时为null
     * @param typeName
     * @return
     */
    private static ApiParamModelNode parseFieldNormalType(PsiField field, String typeName) {
        if (StrUtil.isBlank(typeName)) {
            typeName = field.getType().getCanonicalText();
        }
        ApiParamModelNode apiParamModelNode = new ApiParamModelNode();
        apiParamModelNode.setName(field != null ? field.getName() : null);
        apiParamModelNode.setType(typeName);
        if (field != null) {
            apiParamModelNode.setDesc(parsePropDesc(field));
        }
        //默认值,是否必填
        return apiParamModelNode;
    }

}
