package com.gitee.hperfect.utils;

import cn.hutool.core.util.StrUtil;
import com.gitee.hperfect.yapi.parse.ParseUtils;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 基本类
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/30 9:58 AM
 */
public class YapiTypeUtils {

    @NonNls
    public static final Map<String, Object> NORMAL_TYPES = new HashMap<>();

    public static final Map<String, Object> normalTypesPackages = new HashMap<>();

    public static final Map<String, Object> collectTypes = new HashMap<>();

    public static final Map<String, Object> collectTypesPackages = new HashMap<>();

    public static final Map<String, String> java2JsonTypes = new HashMap<>();
    /**
     * 泛型列表
     */
    public static final List<String> genericList = new ArrayList<>();


    static {
        NORMAL_TYPES.put("int", 1);
        NORMAL_TYPES.put("boolean", false);
        NORMAL_TYPES.put("byte", 1);
        NORMAL_TYPES.put("short", 1);
        NORMAL_TYPES.put("long", 1L);
        NORMAL_TYPES.put("float", 1.0F);
        NORMAL_TYPES.put("double", 1.0D);
        NORMAL_TYPES.put("char", 'a');
        NORMAL_TYPES.put("Boolean", false);
        NORMAL_TYPES.put("Byte", 0);
        NORMAL_TYPES.put("Short", (short) 0);
        NORMAL_TYPES.put("Integer", 0);
        NORMAL_TYPES.put("Long", 0L);
        NORMAL_TYPES.put("Float", 0.0F);
        NORMAL_TYPES.put("Double", 0.0D);
        NORMAL_TYPES.put("String", "String");
        NORMAL_TYPES.put("Date", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
        NORMAL_TYPES.put("BigDecimal", 0.111111);
        NORMAL_TYPES.put("LocalDate", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        NORMAL_TYPES.put("LocalTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        NORMAL_TYPES.put("LocalDateTime", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
        NORMAL_TYPES.put("Timestamp", new Timestamp(System.currentTimeMillis()));
        NORMAL_TYPES.put("Object", null);
        collectTypes.put("HashMap", "HashMap");
        collectTypes.put("Map", "Map");

        collectTypes.put("LinkedHashMap", "LinkedHashMap");

        genericList.add("T");
        genericList.add("E");
        genericList.add("A");
        genericList.add("B");
        genericList.add("K");
        genericList.add("V");

        java2JsonTypes.put("int", "number");
        java2JsonTypes.put("byte", "number");
        java2JsonTypes.put("short", "number");
        java2JsonTypes.put("long", "number");
        java2JsonTypes.put("float", "number");
        java2JsonTypes.put("double", "number");
        java2JsonTypes.put("Byte", "number");
        java2JsonTypes.put("Short", "number");
        java2JsonTypes.put("Integer", "number");
        java2JsonTypes.put("Long", "number");
        java2JsonTypes.put("Float", "number");
        java2JsonTypes.put("Double", "number");
        java2JsonTypes.put("BigDecimal", "number");
        java2JsonTypes.put("Timestamp", "number");
        java2JsonTypes.put("char", "string");
        java2JsonTypes.put("String", "string");
        java2JsonTypes.put("Date", "string");
        java2JsonTypes.put("LocalDate", "string");
        java2JsonTypes.put("LocalDateTime", "string");
        java2JsonTypes.put("Boolean", "boolean");
        java2JsonTypes.put("array", "array");
        java2JsonTypes.put("enum", "string");
    }

    static {
        normalTypesPackages.put("int", 1);
        normalTypesPackages.put("boolean", true);
        normalTypesPackages.put("byte", 1);
        normalTypesPackages.put("short", 1);
        normalTypesPackages.put("long", 1L);
        normalTypesPackages.put("float", 1.0F);
        normalTypesPackages.put("double", 1.0D);
        normalTypesPackages.put("char", 'a');
        normalTypesPackages.put("java.lang.Boolean", false);
        normalTypesPackages.put("java.lang.Byte", 0);
        normalTypesPackages.put("java.lang.Short", (short) 0);
        normalTypesPackages.put("java.lang.Integer", 1);
        normalTypesPackages.put("java.lang.Long", 1L);
        normalTypesPackages.put("java.lang.Float", 1L);
        normalTypesPackages.put("java.lang.Double", 1.0D);
        normalTypesPackages.put("java.sql.Timestamp", new Timestamp(System.currentTimeMillis()));
        normalTypesPackages.put("java.util.Date", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
        normalTypesPackages.put("java.lang.String", "String");
        normalTypesPackages.put("java.math.BigDecimal", 1);
        normalTypesPackages.put("java.time.LocalDate", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        normalTypesPackages.put("java.time.LocalTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        normalTypesPackages.put("java.time.LocalDateTime", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));

        collectTypesPackages.put("java.util.LinkedHashMap", "LinkedHashMap");
        collectTypesPackages.put("java.util.HashMap", "HashMap");
        collectTypesPackages.put("java.util.Map", "Map");
    }


    public static boolean isNormalType(String typeName) {
        typeName = ParseUtils.removePackage(typeName);
        return NORMAL_TYPES.containsKey(typeName);
    }

    public static JsonObject formatMockType(String type) {
        return formatMockType(type, null);
    }

    /**
     * mock type
     *
     * @param type
     * @return
     */
    public static JsonObject formatMockType(String type, String exampleMock) {
        JsonObject mock = new JsonObject();

        //支持传入自定义mock
        if (StringUtils.isNotEmpty(exampleMock)) {
            mock.addProperty("mock", exampleMock);
            return mock;
        }
        if (type.equals("int")) {
            mock.addProperty("mock", "@integer");
        } else if (type.equals("boolean")) {
            mock.addProperty("mock", "@boolean");
        } else if (type.equals("byte")) {
            mock.addProperty("mock", "@byte");
        } else if (type.equals("short")) {
            mock.addProperty("mock", "@integer");
        } else if (type.equals("long")) {
            mock.addProperty("mock", "@integer");
        } else if (type.equals("float")) {
            mock.addProperty("mock", "@float");
        } else if (type.equals("double")) {
            mock.addProperty("mock", "@float");
        } else if (type.equals("char")) {
            mock.addProperty("mock", "@char");
        } else if (type.equals("Boolean")) {
            mock.addProperty("mock", "@boolean");
        } else if (type.equals("Byte")) {
            mock.addProperty("mock", "@byte");
        } else if (type.equals("Short")) {
            mock.addProperty("mock", "@integer");
        } else if (type.equals("Integer")) {
            mock.addProperty("mock", "@integer");
        } else if (type.equals("Long")) {
            mock.addProperty("mock", "@integer");
        } else if (type.equals("Float")) {
            mock.addProperty("mock", "@float");
        } else if (type.equals("Double")) {
            mock.addProperty("mock", "@float");
        } else if (type.equals("String")) {
            mock.addProperty("mock", "@string");
        } else if (type.equals("Date")) {
            mock.addProperty("mock", "@timestamp");
        } else if (type.equals("BigDecimal")) {
            mock.addProperty("mock", "@float");
        } else if (type.equals("LocalDate")) {
            mock.addProperty("mock", "@timestamp");
        } else if (type.equals("LocalTime")) {
            mock.addProperty("mock", "@timestamp");
        } else if (type.equals("LocalDateTime")) {
            mock.addProperty("mock", "@timestamp");
        } else if (type.equals("Timestamp")) {
            mock.addProperty("mock", "@timestamp");
        } else if (type.equals("java.lang.Boolean")) {
            mock.addProperty("mock", "@boolean");
        } else if (type.equals("java.lang.Byte")) {
            mock.addProperty("mock", "@byte");
        } else if (type.equals("java.lang.Short")) {
            mock.addProperty("mock", "@integer");
        } else if (type.equals("java.lang.Integer")) {
            mock.addProperty("mock", "@integer");
        } else if (type.equals("java.lang.Long")) {
            mock.addProperty("mock", "@integer");
        } else if (type.equals("java.lang.Float")) {
            mock.addProperty("mock", "@float");
        } else if (type.equals("java.lang.Double")) {
            mock.addProperty("mock", "@float");
        } else if (type.equals("java.sql.Timestamp")) {
            mock.addProperty("mock", "@timestamp");
        } else if (type.equals("java.util.Date")) {
            mock.addProperty("mock", "@timestamp");
        } else if (type.equals("java.lang.String")) {
            mock.addProperty("mock", "@string");
        } else if (type.equals("java.math.BigDecimal")) {
            mock.addProperty("mock", "@float");
        } else if (type.equals("java.time.LocalDate")) {
            mock.addProperty("mock", "@timestamp");
        } else if (type.equals("java.time.LocalTime")) {
            mock.addProperty("mock", "@timestamp");
        } else if (type.equals("java.time.LocalDateTime")) {
            mock.addProperty("mock", "@timestamp");
        } else {
            mock.addProperty("mock", "mock");
        }
        return mock;
    }

    public static String toYapiType(String type) {
        if (StrUtil.isNotBlank(type)) {
            if (type.contains(".")) {
                List<String> split = StrUtil.split(type, '.');
                type = split.get(split.size() - 1);
            }
            if (java2JsonTypes.containsKey(type)) {
                return java2JsonTypes.get(type);
            }
        }

        return "object";
    }
}
