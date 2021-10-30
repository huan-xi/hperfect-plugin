package com.gitee.hperfect.yapi.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 树形
 *
 * @author huanxi
 * @version 1.0
 * @date 2021/2/2 4:26 下午
 */
@Data
public class ApiParamModelNode {
    /**
     * 参数字段名(默认为参数名),只有基本数据类型才需要
     */
    private String name;

    /**
     * 参数类型(最终为string)
     */
    private String type;

    private List<ApiParamModelNode> paramModelList = new ArrayList<>();
    /**
     * 详情
     */
    private String desc;
    /**
     * 是否未必须值
     */
    private boolean required;
    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 示例
     */
    private String example;
}
