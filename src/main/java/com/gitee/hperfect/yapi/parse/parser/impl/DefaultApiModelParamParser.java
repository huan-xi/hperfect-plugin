package com.gitee.hperfect.yapi.parse.parser.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.gitee.hperfect.settings.AppSettingsState;
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
        } else if (isArrayType(typeName)) {
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
            comment = Strings.isNullOrEmpty(comment) ? comment : "-" + comment;
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
        apiParamModel.setName(parseName(psiParameter, requestParam, apiParam));
        //解析描述(注释,swagger)
        apiParamModel.setDesc(parseParamDesc(method, apiParam, psiParameter));
        //解析是否必填(param,swagger)
        //默认值
        return apiParamModel;
    }


    /**
     * 解析对象类型
     *
     * @param genericType
     * @param project
     * @return
     */
    public ApiParamModelNode parseObjectType(GenericType genericType, Project project, ApiParamModelNode apiParamModelNode) {
        if (apiParamModelNode == null) {
            apiParamModelNode = new ApiParamModelNode();
            apiParamModelNode.setType("Object");
        }
        System.out.printf("查找对象%s\n", genericType.getClassName());
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(genericType.getClassName(), GlobalSearchScope.allScope(project));
        if (psiClass != null) {
            System.out.printf("找到对象,开始解析对象%s\n", psiClass.getName());
            //如果是枚举
            if (psiClass.isEnum()) {
                System.out.println("开始解析枚举属性");
                apiParamModelNode.setType("enum");
                apiParamModelNode.setDesc(parseEnumDesc(apiParamModelNode.getDesc(), psiClass));
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
            if (StrUtil.isNotBlank(apiParam)) {
                if (apiParam.startsWith("!")) {
                    excludeFieldList.addAll(StrUtil.split(apiParam, ','));
                } else {
                    pointList.addAll(StrUtil.split(apiParam.substring(1), '1'));
                }
            }
            if (isArrayType(genericType.getClassName())) {
                //todo 字段没有确定
                //对象是数组->解析泛型类型
                ApiParamModelNode arrayTypeNode = parseObjectType(GenericType.parse(genericType.getGenericClass()), project, null);
                //添加成数组
                return arrayTypeNode;

            } else {
                //解析字段类型
                for (PsiField field : psiClass.getAllFields()) {
                    if (field.getModifierList() != null && field.getModifierList().hasModifierProperty("final")) {
                        continue;
                    }
                    if (CollUtil.isNotEmpty(pointList)) {
                        //指定优先
                        if (!pointList.contains(field.getName())) {
                            continue;
                        }
                    } else if (CollUtil.isNotEmpty(excludeFieldList)) {
                        //排除
                        if (excludeFieldList.contains(field.getName())) {
                            continue;
                        }

                    }
                    String filedTypeName = field.getType().getCanonicalText();
                    System.out.printf("开始解析对象%s,属性:%s,类型:%s\n", psiClass.getName(), field.getName(), filedTypeName);
                    //是否是基本类型
                    GenericType filedGenericType = GenericType.parse(filedTypeName);
                    if (StrUtil.isNotBlank(filedGenericType.getGenericClass())) {
                        //替换泛型,多泛型未处理 List<T> 处理
                        if (YapiTypeUtils.genericList.contains(filedGenericType.getGenericClass())) {
                            filedGenericType.setGenericClass(genericType.getGenericClass());
                        }
                        if (isArrayType(filedGenericType.getClassName())) {
                            apiParamModelNode.getParamModelList().add(parseFiledArray(field, filedGenericType, project));
                            continue;
                        }
                        apiParamModelNode.getParamModelList().add(parseObjectType(filedGenericType, project, apiParamModelNode));
                        //该字段解析完成
                        continue;
                    }
                    //如果是(当前类)泛型 T t 处理
                    String genericClass = genericType.getGenericClass();
                    if (YapiTypeUtils.genericList.contains(filedTypeName)) {
                        if (StrUtil.isBlank(genericClass) || "?".equals(genericClass)) {
                            //未知泛型处理
                            filedTypeName = "java.lang.Object";
                        } else {
                            //按外层解析泛型处理
                            filedTypeName = genericType.getGenericClass();
                        }
                        if (isArrayType(genericClass)) {
                            System.out.printf("开始解析对象属性%s,数组属性\n", field.getName());
                            apiParamModelNode.getParamModelList().add(parseFiledArray(field, GenericType.parse(genericClass), project));
                            continue;
                        }
                    }
                    apiParamModelNode.getParamModelList().add(parseObjectOrNormal(field, GenericType.parse(filedTypeName), project));
                }
            }


        } else {
            System.out.printf("未找到class:%s\n", genericType.getClassName());
        }
        return apiParamModelNode;
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
        paramModelNode.setParamModelList(CollUtil.toList(parseObjectOrNormal(field, genericType, project)));
        return paramModelNode;
    }


    //todo 待完善
    private boolean isArrayType(String filedTypeName) {
        if (filedTypeName.contains("java.util.List")) {
            return true;
        }
        if (filedTypeName.contains("java.lang.Iterable")) {
            return true;
        }
        return false;
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
            //desc 获取
            PsiAnnotation apiModelProperty = field.getAnnotation(AnnotationCons.API_MODEL_PROPERTY);
            String desc = "";
            String notes = "";
            if (apiModelProperty != null) {
                desc = ParseUtils.getPsiAnnotationValue(apiModelProperty);
                notes = ParseUtils.getPsiAnnotationValueByName(apiModelProperty, "notes");
                apiParamModelNode.setDesc(desc);
            }
            if (StrUtil.isBlank(desc) && field.getDocComment() != null) {
                //注释中获取
                desc = ParseUtils.getJavaDoc(field.getDocComment().getText());
            }
            if (StrUtil.isNotBlank(notes)) {
                desc = desc + "(" + notes + ")";
            }
            apiParamModelNode.setDesc(desc);
        }
        //默认值,是否必填
        return apiParamModelNode;
    }
}
