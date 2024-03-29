package com.gitee.hperfect.yapi.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 一个api
 *
 * @author huanxi
 * @version 1.0
 * @date 2021/2/2 4:23 下午
 */
@Data
@ToString
public class ApiModel {
    private String name;

    private String path;

    private List<ApiParamModelNode> formParams;
    private ApiParamModelNode bodyParams;
    /**
     * get,post,del,put
     */
    private String method;
    /**
     * 描述
     */
    private String desc;

    /**
     * 返回值
     */
    private ApiParamModelNode returnType;
}
