package com.giant.watsonapp.utils;

import com.giant.watsonapp.App;
import com.giant.watsonapp.Const;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Jorble on 2017/7/26.
 */

public final class JdbcUtil {

    /**
     * 建立数据库连接
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException{

        /**
         * 装载驱动
         */
        // 引用代码此处需要修改，address为数据IP，Port为端口号，
        // DBName为数据库名称，UserName为数据库登录账户，Password为数据库登录密码
//        String url="jdbc:mysql://192.168.100.55:3306/watson_app";
//        String userName="root";
//        String password="";
//        String driver="com.mysql.jdbc.Driver";

        String url= App.getCacheString(Const.MYSQL_URL_KEY,Const.MYSQL_URL_DEFAULT);
        String userName=App.getCacheString(Const.MYSQL_USER_KEY,Const.MYSQL_USER_DEFAULT);
        String password=App.getCacheString(Const.MYSQL_PWD_KEY,Const.MYSQL_PWD_DEFAULT);
        String driver=App.getCacheString(Const.MYSQL_DRIVER_KEY,Const.MYSQL_DRIVER_DEFAULT);

        L.i("url="+url);
        L.i("userName="+userName);
        L.i("password="+password);

        try {
            Class.forName(driver);
            L.i("装载驱动成功");
        } catch (ClassNotFoundException e) {
            L.e("装载驱动失败:"+e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, userName, password);
            if(conn!=null) {
                L.i("建立数据库连接成功");
            }
        } catch (SQLException e) {
            L.e("建立数据库连接失败:"+e.getMessage());
            throw new SQLException(e);
        }
        return conn;
    }

    /**
     * 释放连接
     * @param conn
     */
    private static void freeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 释放statement
     * @param statement
     */
    private static void freeStatement(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 释放resultset
     * @param rs
     */
    private static void freeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     *
     * @param conn
     * @param statement
     * @param rs
     */
    public static void free(Connection conn, Statement statement, ResultSet rs) {
        if (rs != null) {
            freeResultSet(rs);
        }
        if (statement != null) {
            freeStatement(statement);
        }
        if (conn != null) {
            freeConnection(conn);
        }
    }
}
