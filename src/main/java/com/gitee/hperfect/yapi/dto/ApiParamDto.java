package com.gitee.hperfect.yapi.dto;

import lombok.Data;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/3 3:32 下午
 */
@Data
public class ApiParamDto {
    private String desc;
    private String example;
    private String name;
    private String required = "0";
}
