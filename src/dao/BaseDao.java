package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// 数据库连接工具类：统一封装连接逻辑，供所有Dao类调用，简化维护
public class BaseDao {
    // 格式：jdbc:sqlserver://[地址]:[端口];databaseName=[库名];trustServerCertificate=true（跳过SSL验证）
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=BankDB;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456";

    // 静态方法：获取数据库连接
    // @return 数据库连接对象
    // @throws SQLException 连接失败时抛出（如配置错误、数据库未启动），由调用者处理
    public static Connection getConnection() throws SQLException {
        // 自动加载驱动（SQL Server 2008+无需手动注册），返回连接对象
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}