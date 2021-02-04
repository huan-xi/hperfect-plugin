package com.gitee.hperfect.yapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.jsonProtocol.JsonField;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/3 12:57 下午
 */
@Data
public class CatDto {
    private Integer index;
    @SerializedName("_id")
    private Integer id;
    private String name;
    @SerializedName("project_id")
    private Integer projectId;

    private String desc;
    private Integer uid;
    @SerializedName("add_time")
    private Integer addTime;
    @SerializedName("up_time")
    private Integer upTime;

}
