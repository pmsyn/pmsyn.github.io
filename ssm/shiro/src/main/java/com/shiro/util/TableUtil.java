package com.shiro.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.shiro.pojo.TableField;
import com.shiro.service.SpringContextServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pms
 * @createtime 2018/7/12-16:05
 * @qq 718195578
 * @since 1.0
 */
public class TableUtil {
    private static final Logger log = LoggerFactory.getLogger(TableUtil.class);

    /**
     * @param map       前台传入map
     * @param owner     用户
     * @param tableName 表名
     * @return 存入数据库的map
     */
    public static Map<String, Object> getTableValue(Map<String, Object> map, String owner, String tableName) {
        List<TableField> tableColumn = tableField(owner, tableName);
        Map<String, Object> tableValues = new HashMap<>();

        for (TableField table : tableColumn) {
            String key = table.getColumnName();
            Object val = MapUtility.getMapObjectValue(map, key);
            if (val != null) tableValues.put(key, val);
        }
        return tableValues;
    }

    /**
     * 获取table字段
     *
     * @param owner     用户
     * @param tableName 表名
     * @return table字段
     */
    public static List<TableField> tableField(String owner, String tableName) {
        DruidDataSource dataSource = SpringContextServiceImpl.getBean("dataSource");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TableField> fieldList = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("select * from " + tableName + " where rownum < 1");
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                TableField field = new TableField();
                String columnName = rsmd.getColumnName(i);
                String columnType = rsmd.getColumnTypeName(i);
                int dataLength = rsmd.getColumnDisplaySize(i);
                int nullable = rsmd.isNullable(i);
                field.setColumnName(columnName);
                field.setDataType(columnType);
                field.setDataLength(dataLength);
                field.setNullable(nullable);
                fieldList.add(field);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            DbConnectUtil.closeConnection(conn, ps, rs);
        }
        return fieldList;
    }
}
