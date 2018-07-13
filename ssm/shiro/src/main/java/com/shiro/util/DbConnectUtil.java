package com.shiro.util;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author pms
 */
public class DbConnectUtil {
    private static Connection cn = null;
    private static Properties property = new Properties();

    public Connection getLocalConnection() {
        if (cn == null) {
            try {
                DruidDataSource ds = new DruidDataSource();
                property.load(this.getClass().getResourceAsStream("/jdbc.properties"));
                ds.configFromPropety(property);
                cn = ds.getConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cn;

    }

    public Connection getJshConnection() {
        if (cn == null) {
            try {
                property.load(this.getClass().getResourceAsStream("/db.properties"));
                String driver = property.getProperty("jshoracledriver");
                String url = property.getProperty("jshoracleurl");
                String username = property.getProperty("jshoracleuser");
                String password = property.getProperty("jshoraclepassword");
                Class.forName(driver);
                cn = DriverManager.getConnection(url, username, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cn;

    }

    public static void closeConnection(Connection cn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            if (cn != null)
                cn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

