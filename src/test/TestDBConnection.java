package test;

import dao.BaseDao;
import java.sql.Connection;

public class TestDBConnection {
    public static void main(String[] args) {
        System.out.println("开始测试数据库连接...");
        try {
            Connection conn = BaseDao.getConnection();
            System.out.println("数据库连接成功！");
            conn.close();
        } catch (Exception e) {
            System.out.println("数据库连接失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}