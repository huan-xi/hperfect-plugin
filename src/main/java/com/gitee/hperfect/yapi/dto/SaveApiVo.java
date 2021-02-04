package com.gitee.hperfect.yapi.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/4 9:54 上午
 */
@Data
public class SaveApiVo {
    @SerializedName("_id")
    private Integer id;
    @SerializedName("res_body")
    private String resBody;
}
