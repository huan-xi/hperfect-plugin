package com.gitee.hperfect.yapi.parse.parser;

import com.gitee.hperfect.yapi.model.ApiModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.util.List;

/**
 * api模型解析器
 *
 * @author huanxi
 * @version 1.0
 * @date 2021/2/4 11:15 上午
 */
public interface ApiModelParser {

    /**
     * 从类方法中解析api模型
     *
     * @param methods
     * @param pointMethod
     * @param catRoute
     * @param project
     * @return
     */
    List<ApiModel> parseApiModel(PsiMethod[] methods, String pointMethod, String catRoute, Project project);

    /**
     * 从方法中解析apiName名称
     * 1. swagger ApiOperation 注解
     * 2. 方法注释
     *
     * @param method
     * @return
     */
    String parseApiName(PsiMethod method);
}
