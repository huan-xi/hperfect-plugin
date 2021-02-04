package com.gitee.hperfect.yapi.parse.parser;

import com.gitee.hperfect.yapi.model.ApiParamModelNode;
import com.gitee.hperfect.yapi.parse.GenericType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

/**
 * 模型属性解析
 *
 * @author huanxi
 * @version 1.0
 * @date 2021/2/4 10:42 上午
 */
public interface ApiModelParamParser {

    /**
     * 从方法上解析方法参数为参数模型
     * 分为:
     * 1. 普通类型
     * 2. 数组类型
     * 3. 枚举类型
     * 4. 对象类型
     *
     * @param psiParameter
     * @param method
     * @param project
     * @return
     */

    ApiParamModelNode parseMethodFiled(PsiParameter psiParameter, PsiMethod method, Project project);

    /**
     * 从类属性上解析字段
     *
     * @param genericType
     * @param project
     * @return
     */
    ApiParamModelNode parseObjectType(GenericType genericType, Project project);
}
