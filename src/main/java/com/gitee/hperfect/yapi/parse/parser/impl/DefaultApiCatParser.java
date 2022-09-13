package com.gitee.hperfect.yapi.parse.parser.impl;

import cn.hutool.core.util.StrUtil;
import com.gitee.hperfect.utils.MessageUtils;
import com.gitee.hperfect.yapi.config.AnnotationCons;
import com.gitee.hperfect.yapi.model.ApiCat;
import com.gitee.hperfect.yapi.parse.ParseUtils;
import com.gitee.hperfect.yapi.parse.parser.ApiCatParser;
import com.gitee.hperfect.yapi.parse.parser.ApiModelParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/4 11:23 上午
 */
public class DefaultApiCatParser implements ApiCatParser {

    ApiModelParser apiModelParser;

    public DefaultApiCatParser() {
        this.apiModelParser = new DefaultApiModelParser();
    }

    /**
     * 开始解析
     *
     * @param psiClass       选中的类
     * @param project        工程上下文
     * @param selectedMethod 选择的方法
     */
    @Override
    public ApiCat parse(PsiClass psiClass, Project project, PsiMethod selectedMethod) {
        String pointMethod = selectedMethod != null ? selectedMethod.getName() : null;
        //restController或Controller
        //解析分类和一级路由
        PsiAnnotation annotation = psiClass.getAnnotation(AnnotationCons.REST_CONTROLLER);
        if (annotation == null) {
            PsiAnnotation controller = psiClass.getAnnotation(AnnotationCons.CONTROLLER);
            if (controller == null) {
                //不是controller
                MessageUtils.error(project, "没有解析到controller");
                return null;
            }
        }
        String catRoute = this.parseCatRoute(psiClass);
        if (catRoute == null) {
            MessageUtils.error(project, "没有解析到路由");
            //没有解析到路由
            return null;
        }
        ApiCat apiCat = this.parseCatNameDesc(psiClass);
        PsiMethod[] methods = psiClass.getAllMethods();
        apiCat.setApiModelList(apiModelParser.parseApiModel(methods, pointMethod, catRoute, project));
        return apiCat;
    }


    @Override
    public ApiCat parseCatNameDesc(PsiClass psiClass) {
        ApiCat apiCat = new ApiCat();
        PsiAnnotation annotation = psiClass.getAnnotation(AnnotationCons.API);
        if (annotation != null) {
            apiCat.setCatName(ParseUtils.getPsiAnnotationValueByName(annotation, "tags"));
        }
        PsiDocComment docComment = psiClass.getDocComment();
        if (docComment != null) {
            apiCat.setCatDesc(ParseUtils.getJavaDoc(docComment.getText()));
        }
        if (StrUtil.isBlank(apiCat.getCatName()) && docComment != null) {
            //从注释中获取
            apiCat.setCatName(ParseUtils.getJavaDoc(docComment.getText()));
        }
        return apiCat;
    }


    @Override
    public String parseCatRoute(PsiClass psiClass) {
        for (PsiAnnotation annotation : psiClass.getAnnotations()) {
            if (annotation.getQualifiedName() != null && annotation.getQualifiedName().endsWith("Mapping")) {
                return ParseUtils.addWithBackslash(ParseUtils.getPsiAnnotationValue(annotation));
            }
        }
        return null;
    }
}
