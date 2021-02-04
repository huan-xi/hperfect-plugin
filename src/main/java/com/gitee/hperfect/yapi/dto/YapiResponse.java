package com.gitee.hperfect.yapi.dto;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/3 12:52 下午
 */
@Data
public class YapiResponse<T> {
    private String errcode;
    private String errmsg;
    private T data;

    public static boolean isSuccess(String resp) {
        return StrUtil.isNotBlank(resp) && resp.contains("成功");
    }
}
