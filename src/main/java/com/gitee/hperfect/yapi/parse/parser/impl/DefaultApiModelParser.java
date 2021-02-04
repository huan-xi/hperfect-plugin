package com.gitee.hperfect.yapi.parse.parser.impl;

import cn.hutool.core.util.StrUtil;
import com.gitee.hperfect.yapi.config.AnnotationCons;
import com.gitee.hperfect.yapi.model.ApiModel;
import com.gitee.hperfect.yapi.model.ApiParamModelNode;
import com.gitee.hperfect.yapi.parse.GenericType;
import com.gitee.hperfect.yapi.parse.ParseUtils;
import com.gitee.hperfect.yapi.parse.parser.ApiModelParser;
import com.gitee.hperfect.yapi.parse.parser.ApiModelParamParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.javadoc.PsiDocComment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/4 11:30 上午
 */
public class DefaultApiModelParser implements ApiModelParser {

    ApiModelParamParser apiModelPropertyParser;

    public DefaultApiModelParser() {
        this.apiModelPropertyParser = new DefaultApiModelParamParser();
    }

    @Override
    public List<ApiModel> parseApiModel(PsiMethod[] methods, String pointMethod, String catRoute, Project project) {
        List<ApiModel> apis = new ArrayList<>();
        for (PsiMethod method : methods) {
            //是否是指定的方法
            if (StrUtil.isNotBlank(pointMethod) && !method.getName().equals(pointMethod)) {
                continue;
            }
            if (!method.getModifierList().hasModifierProperty("private")) {
                //查找mapper注解
                for (PsiAnnotation annotation : method.getAnnotations()) {
                    if (annotation.getQualifiedName() != null && annotation.getQualifiedName().endsWith("Mapping")) {
                        ApiModel apiModel = new ApiModel();
                        //路径
                        apiModel.setPath(catRoute + ParseUtils.addWithBackslash(ParseUtils.getPsiAnnotationValue(annotation)));
                        //方法名
                        apiModel.setName(this.parseApiName(method));
                        //方法类型
                        apiModel.setMethod(this.parseMethod(annotation.getQualifiedName()));
                        //参数,get参数
                        apiModel.setFormParams(this.parseFormParams(method, project));
                        //post参数
                        apiModel.setBodyParams(this.parseBodyParams(method, project));
                        //返回值
                        apiModel.setReturnType(this.parseApiReturnType(method, project));
                        //方法描述
                        PsiDocComment docComment = method.getDocComment();
                        if (docComment != null) {
                            apiModel.setDesc(ParseUtils.getJavaDoc(docComment.getText()));
                        }
                        apis.add(apiModel);
                        break;
                    }
                }
            }
        }
        return apis;
    }

    /**
     * 解析返回值类型
     * @param method
     * @param project
     * @return
     */
    private ApiParamModelNode parseApiReturnType(PsiMethod method, Project project) {
        PsiType returnType = method.getReturnType();
        if (returnType != null) {
            return this.apiModelPropertyParser.parseObjectType(GenericType.parse(returnType.getCanonicalText()), project);
        }
        return null;
    }

    /**
     * 解析apiName ApiOperation
     *
     * @param method
     * @return
     */
    @Override
    public String parseApiName(PsiMethod method) {
        String methodName = "";
        PsiAnnotation annotation = method.getAnnotation(AnnotationCons.API_OPERATION);
        if (annotation != null) {
            methodName = ParseUtils.getPsiAnnotationValue(annotation);
        }
        if (StrUtil.isBlank(methodName)) {
            PsiDocComment docComment = method.getDocComment();
            if (docComment != null) {
                methodName = ParseUtils.getJavaDoc(docComment.getText());
            }
        }
        return methodName;
    }


    private ApiParamModelNode parseBodyParams(PsiMethod method, Project project) {
        ApiParamModelNode params = null;
        PsiParameter[] parameterList = method.getParameterList().getParameters();
        if (parameterList.length > 0) {
            for (PsiParameter psiParameter : parameterList) {
                //没有requestBody注解,否则为json参数
                PsiAnnotation requestBody = psiParameter.getAnnotation(AnnotationCons.REQUEST_BODY);
                if (requestBody != null) {
                    params = this.apiModelPropertyParser.parseMethodFiled(psiParameter, method, project);
                    break;
                }
            }
        }
        return params;
    }

    /**
     * 解析参数
     *
     * @param method
     * @return
     */
    private List<ApiParamModelNode> parseFormParams(PsiMethod method, Project project) {
        List<ApiParamModelNode> params = new ArrayList<>();
        PsiParameter[] parameterList = method.getParameterList().getParameters();
        if (parameterList.length > 0) {
            for (PsiParameter psiParameter : parameterList) {
                //没有requestBody注解,否则为json参数
                PsiAnnotation requestBody = psiParameter.getAnnotation(AnnotationCons.REQUEST_BODY);
                if (requestBody != null) {
                    continue;
                }

                ApiParamModelNode apiParamModel = this.apiModelPropertyParser.parseMethodFiled(psiParameter, method, project);

                if (apiParamModel != null) {
                    params.add(apiParamModel);
                }
            }
        }
        return params;
    }

    /**
     * 解析方法类型
     *
     * @param qualifiedName
     * @return
     */
    private String parseMethod(String qualifiedName) {
        List<String> split = StrUtil.split(qualifiedName, '.');
        String mapping = StrUtil.removeSuffix(split.get(split.size() - 1), "Mapping").toUpperCase();
        return "REQUEST".equals(mapping) ? "GET" : mapping;
    }
}
