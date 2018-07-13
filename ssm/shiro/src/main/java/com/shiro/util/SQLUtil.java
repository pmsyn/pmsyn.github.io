package com.shiro.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author pms
 * @createtime 2018/7/10-16:26
 * @qq 718195578
 * @since 1.0
 */
public class SQLUtil {

    /**
     * 获取插入sql
     *
     * @param tableName 表名
     * @param map       表字段map
     * @param dateName  需要转成时间的字段
     * @return insertSql
     */
    public static String getInsertSql(String tableName, Map<String, Object> map, String... dateName) {
        Map<String, Object> finalMap = removeMapEmptyField(map);
        String[] key = mapKeyToStrArray(finalMap);
        List<String> listKey = convertInsertColVal(key, dateName);
        String[] keyParam = listArrayToStrArray(listKey);
        return new SQL() {{
            INSERT_INTO(tableName);
            INTO_COLUMNS(arrayToDelimitedString(key, ","));
            INTO_VALUES(arrayToDelimitedString(keyParam, ","));
        }}.toString();
    }

    /**
     * 获取更新sql
     *
     * @param tableName    表名称
     * @param map          表字段map
     * @param conditionSql 更新条件
     * @param dateName     需要转成时间的字段
     * @return updateSQL
     */
    public static String getUpdateSql(String tableName, Map<String, Object> map, String conditionSql, String...
            dateName) {
        Map<String, Object> finalMap = removeMapEmptyField(map);
        String[] key = mapKeyToStrArray(finalMap);
        List<String> listKey = convertUpdateColVal(key, dateName);
        String[] keyParam = listArrayToStrArray(listKey);
        return new SQL() {{
            UPDATE(tableName);
            SET(arrayToDelimitedString(keyParam, ","));
            WHERE(conditionSql);
        }}.toString();
    }

    /**
     * 删除map中空值
     *
     * @param map 原map
     * @return map
     */
    private static Map<String, Object> removeMapEmptyField(Map<String, Object> map) {
        Map<String, Object> newMap = new HashMap<>();
        Set<String> set = map.keySet();
        for (String k : set) {
            Object v = map.get(k);
            if (!StringUtils.isEmpty(v)) {
                newMap.put(k, v);
            }
        }
        return newMap;
    }

    /**
     * 将Map key 转换成字符串数组
     *
     * @param map 待转换map
     * @return string[]
     */
    private static String[] mapKeyToStrArray(Map<String, Object> map) {
        Set<String> keySet = map.keySet();
        return keySet.toArray(new String[keySet.size()]);
    }

    /**
     * 将List<String> 转换成string数组
     *
     * @param values
     * @return
     */
    private static String[] listArrayToStrArray(List<String> values) {
        return values.toArray(new String[values.size()]);
    }

    /**
     * mybatis insert sql转换
     *
     * @param keys
     * @param dateName 时间格式
     * @return
     */
    private static List<String> convertInsertColVal(String[] keys, String... dateName) {
        List<String> list = new ArrayList<>();
        for (String key : keys) {
            if (ArrayUtils.contains(dateName, key)) {
                list.add("to_date(#{" + key + "},'yyyy-mm-dd Hi24:mi:ss')");
            } else {
                list.add("#{" + key + "}");
            }
        }
        return list;
    }

    /**
     * mybatis update sql 转换
     *
     * @param keys
     * @param dateName 时间格式
     * @return
     */
    private static List<String> convertUpdateColVal(String[] keys, String... dateName) {
        List<String> list = new ArrayList<>();
        for (String key : keys) {
            if (ArrayUtils.contains(dateName, key)) {
                list.add(key + "=to_date(#{" + key + "},'yyyy-mm-dd Hi24:mi:ss')");
            } else {
                list.add(key + "=#{" + key + "}");
            }
        }
        return list;
    }

    /**
     * 字符串数组转换成指定字符分割的字符串
     *
     * @param str   字符串数组
     * @param delim 分隔符
     * @return
     */
    private static String arrayToDelimitedString(String[] str, String delim) {
        return StringUtils.arrayToDelimitedString(str, delim);
    }
}
