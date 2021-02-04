package com.gitee.hperfect.yapi.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/3 4:08 下午
 */
@Data
public class YapiTypeDto {
    private String type;
    private Map<String, YapiTypeDto> properties;
    private String title;
    private String description;
    private YapiTypeDto items;
}
