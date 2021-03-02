package com.gitee.hperfect.yapi.parse.parser.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
            //解析字段类型
            for (PsiField field : psiClass.getAllFields()) {
                if (field.getModifierList() != null && field.getModifierList().hasModifierProperty("final")) {
                    continue;
                }

                String filedTypeName = field.getType().getCanonicalText();
                System.out.printf("开始解析对象%s,属性:%s,类型:%s\n", psiClass.getName(), field.getName(), filedTypeName);
                //是否是基本类型
                GenericType filedGenericType = GenericType.parse(filedTypeName);
                if (StrUtil.isNotBlank(filedGenericType.getGenericClass()) && YapiTypeUtils.genericList.contains(filedGenericType.getGenericClass())) {
                    //替换泛型,多泛型未处理
                    GenericType filedTypeReplaced = new GenericType(filedGenericType.getClassName(), genericType.getGenericClass());
                    apiParamModelNode.getParamModelList().add(parseObjectType(filedTypeReplaced, project, apiParamModelNode));
                    //该字段解析完成
                    continue;
                }
                //如果是(当前类)泛型
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
                        System.out.println("开始解析数组属性");
                        apiParamModelNode.getParamModelList().add(parseFiledArray(field, genericClass, project));
                        continue;
                    }
                }
                apiParamModelNode.getParamModelList().add(parseObjectOrNormal(field, filedTypeName, project));
            }
            //对象是数组
        } else {
            System.out.printf("未找到class:%s\n", genericType.getClassName());
        }
        return apiParamModelNode;
    }

    public ApiParamModelNode parseObjectOrNormal(PsiField field, String filedTypeName, Project project) {
        ApiParamModelNode apiParamModelNode = parseModelNormalType(field, filedTypeName);
        if (YapiTypeUtils.isNormalType(filedTypeName)) {
            return apiParamModelNode;
        } else {
            return parseObjectType(GenericType.parse(filedTypeName), project, apiParamModelNode);
        }
    }

    /**
     * 解析数组类型
     *
     * @param genericClass
     * @param project
     * @return
     */
    private ApiParamModelNode parseFiledArray(PsiField field, String genericClass, Project project) {
        ApiParamModelNode paramModelNode = parseModelNormalType(field, "array");
        genericClass = GenericType.parse(genericClass).getGenericClass();
        if (StrUtil.isBlank(genericClass) || "?".equals(genericClass)) {
            //未知泛型处理
            genericClass = "java.lang.Object";
        }
        paramModelNode.setParamModelList(CollUtil.toList(parseObjectOrNormal(field, genericClass, project)));
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
     * @param field 数组时为null
     * @return
     */
    private static ApiParamModelNode parseModelNormalType(PsiField field, String typeName) {
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
