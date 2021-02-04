package com.gitee.hperfect.yapi.model;

import lombok.Data;

import java.util.List;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/4 10:58 上午
 */
@Data
public class ApiCat {
    private String catName;

    private String catDesc;


    private List<ApiModel> apiModelList;
}
