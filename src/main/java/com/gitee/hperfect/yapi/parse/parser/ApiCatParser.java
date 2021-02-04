package com.gitee.hperfect.yapi.parse.parser;

import com.gitee.hperfect.yapi.model.ApiCat;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

/**
 * api 分类解析
 *
 * @author huanxi
 * @version 1.0
 * @date 2021/2/4 10:46 上午
 */
public interface ApiCatParser {
    /**
     * 解析api分类
     *
     * @param psiClass
     * @param project
     * @param selectedMethod 指定方法
     * @return
     */
    ApiCat parse(PsiClass psiClass, Project project, PsiMethod selectedMethod);



    /**
     * 解析分类名称和描述,解析优先级
     * 1. swagger Api 注解
     * 2. 类注释
     *
     * @param psiClass
     * @return
     */
    ApiCat parseCatNameDesc(PsiClass psiClass);

    /**
     * 解析一级分类路由
     *
     * @param psiClass
     * @return
     */
    String parseCatRoute(PsiClass psiClass);
}
