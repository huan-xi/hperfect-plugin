package com.gitee.hperfect.utils;

import cn.hutool.core.collection.ListUtil;
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

    public static final Map<String, Object> NORMAL_TYPES_PACKAGES = new HashMap<>();

    public static final Map<String, Object> COLLECT_TYPES = new HashMap<>();

    public static final Map<String, Object> COLLECT_TYPES_Packages = new HashMap<>();

    public static final Map<String, String> JAVA2_JSON_TYPES = new HashMap<>();
    /**
     * 泛型列表
     */
    public static final List<String> GENERIC_LIST = new ArrayList<>();
    public static final List<String> ARRAY_TYPE_LIST = ListUtil.toList("java.util.List", "java.lang.Iterable", "java.util.ArrayList");
    public static final List<String> MAP_TYPE_LIST = ListUtil.toList("java.util.Map");

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
        NORMAL_TYPES.put("Map", null);



        NORMAL_TYPES.put("Date", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
        NORMAL_TYPES.put("BigDecimal", 0.111111);
        NORMAL_TYPES.put("LocalDate", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        NORMAL_TYPES.put("LocalTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        NORMAL_TYPES.put("LocalDateTime", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
        NORMAL_TYPES.put("Timestamp", new Timestamp(System.currentTimeMillis()));
        NORMAL_TYPES.put("Object", null);
        COLLECT_TYPES.put("HashMap", "HashMap");
        COLLECT_TYPES.put("Map", "Map");

        COLLECT_TYPES.put("LinkedHashMap", "LinkedHashMap");

        GENERIC_LIST.add("T");
        GENERIC_LIST.add("E");
        GENERIC_LIST.add("A");
        GENERIC_LIST.add("B");
        GENERIC_LIST.add("K");
        GENERIC_LIST.add("V");

        JAVA2_JSON_TYPES.put("int", "number");
        JAVA2_JSON_TYPES.put("byte", "number");
        JAVA2_JSON_TYPES.put("short", "number");
        JAVA2_JSON_TYPES.put("long", "number");
        JAVA2_JSON_TYPES.put("float", "number");
        JAVA2_JSON_TYPES.put("double", "number");
        JAVA2_JSON_TYPES.put("Byte", "number");
        JAVA2_JSON_TYPES.put("Short", "number");
        JAVA2_JSON_TYPES.put("Integer", "number");
        JAVA2_JSON_TYPES.put("Long", "number");
        JAVA2_JSON_TYPES.put("Float", "number");
        JAVA2_JSON_TYPES.put("Double", "number");
        JAVA2_JSON_TYPES.put("BigDecimal", "number");
        JAVA2_JSON_TYPES.put("Timestamp", "number");
        JAVA2_JSON_TYPES.put("char", "string");
        JAVA2_JSON_TYPES.put("String", "string");
        JAVA2_JSON_TYPES.put("Date", "string");
        JAVA2_JSON_TYPES.put("LocalDate", "string");
        JAVA2_JSON_TYPES.put("LocalDateTime", "string");
        JAVA2_JSON_TYPES.put("Boolean", "boolean");
        JAVA2_JSON_TYPES.put("array", "array");
        JAVA2_JSON_TYPES.put("enum", "string");
    }

    static {
        NORMAL_TYPES_PACKAGES.put("int", 1);
        NORMAL_TYPES_PACKAGES.put("boolean", true);
        NORMAL_TYPES_PACKAGES.put("byte", 1);
        NORMAL_TYPES_PACKAGES.put("short", 1);
        NORMAL_TYPES_PACKAGES.put("long", 1L);
        NORMAL_TYPES_PACKAGES.put("float", 1.0F);
        NORMAL_TYPES_PACKAGES.put("double", 1.0D);
        NORMAL_TYPES_PACKAGES.put("char", 'a');
        NORMAL_TYPES_PACKAGES.put("java.lang.Boolean", false);
        NORMAL_TYPES_PACKAGES.put("java.lang.Byte", 0);
        NORMAL_TYPES_PACKAGES.put("java.lang.Short", (short) 0);
        NORMAL_TYPES_PACKAGES.put("java.lang.Integer", 1);
        NORMAL_TYPES_PACKAGES.put("java.lang.Long", 1L);
        NORMAL_TYPES_PACKAGES.put("java.lang.Float", 1L);
        NORMAL_TYPES_PACKAGES.put("java.lang.Double", 1.0D);
        NORMAL_TYPES_PACKAGES.put("java.sql.Timestamp", new Timestamp(System.currentTimeMillis()));
        NORMAL_TYPES_PACKAGES.put("java.util.Date", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
        NORMAL_TYPES_PACKAGES.put("java.lang.String", "String");
        NORMAL_TYPES_PACKAGES.put("java.math.BigDecimal", 1);
        NORMAL_TYPES_PACKAGES.put("java.time.LocalDate", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        NORMAL_TYPES_PACKAGES.put("java.time.LocalTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        NORMAL_TYPES_PACKAGES.put("java.time.LocalDateTime", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));

        COLLECT_TYPES_Packages.put("java.util.LinkedHashMap", "LinkedHashMap");
        COLLECT_TYPES_Packages.put("java.util.HashMap", "HashMap");
        COLLECT_TYPES_Packages.put("java.util.Map", "Map");
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
            if (JAVA2_JSON_TYPES.containsKey(type)) {
                return JAVA2_JSON_TYPES.get(type);
            }
        }

        return "object";
    }

    public static boolean isArrayType(String className) {
        for (String typeName : ARRAY_TYPE_LIST) {
            if (typeName.contains(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMapType(String className) {
        for (String typeName : MAP_TYPE_LIST) {
            if (typeName.contains(className)) {
                return true;
            }
        }
        return false;
    }
}
