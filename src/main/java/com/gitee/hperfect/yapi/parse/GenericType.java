package com.gitee.hperfect.yapi.parse;

import cn.hutool.core.util.StrUtil;
import com.gitee.hperfect.utils.YapiTypeUtils;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 泛型类型
 *
 * @author huanxi
 * @version 1.0
 * @date 2021/2/3 10:30 上午
 */
@Data
public class GenericType {
    private String className;
    private String genericClass;

    public GenericType(String className, String genericClass) {
        this.className = className;
        this.genericClass = genericClass;
    }

    public GenericType() {

    }

    public static GenericType parse(String str) {
        GenericType genericType = new GenericType();
        if (str.contains("<")) {
            List<String> split = StrUtil.split(str, '<', 2);
            genericType.setClassName(split.get(0));
            String s = split.get(1);
            genericType.setGenericClass(s.substring(0, s.length() - 1));
        } else {
            genericType.setClassName(str);
        }
        return genericType;
    }

    public boolean isArray() {
        return YapiTypeUtils.isArrayType(className);
    }
}
