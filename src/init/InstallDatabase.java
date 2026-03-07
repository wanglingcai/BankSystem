//    package init;
//
//    import dao.BaseDao;
//
//    import java.sql.Connection;
//    import java.sql.Statement;
//
//    public class InstallDatabase {
//
//        public static void main(String[] args) {
//            try {
//                Connection conn = BaseDao.getConnection();
//                Statement stmt = conn.createStatement();
//                System.out.println("开始初始化数据库……");
//                // 1. 删除旧库（如果存在）
//                stmt.execute("IF DB_ID('BankDB') IS NOT NULL DROP DATABASE BankDB;");
//                // 2. 创建新库
//                stmt.execute("CREATE DATABASE BankDB;");
//                // 3. 切换到 BankDB
//                stmt.execute("USE BankDB;");
//                // 4. 创建 Users 表
//                stmt.execute(
//                        "CREATE TABLE dbo.Users (" +
//                                "  user_id INT IDENTITY(1,1) PRIMARY KEY," +
//                                "  username NVARCHAR(50) NOT NULL UNIQUE," +
//                                "  password NVARCHAR(50) NOT NULL," +
//                                "  balance DECIMAL(18,2) NOT NULL DEFAULT 0," +
//                                "  phone NVARCHAR(20)" +
//                                ");"
//                );
//                // 5. 创建 Admins 表
//                stmt.execute(
//                        "CREATE TABLE dbo.Admins (" +
//                                "  admin_id INT IDENTITY(1,1) PRIMARY KEY," +
//                                "  username NVARCHAR(50) NOT NULL UNIQUE," +
//                                "  password NVARCHAR(50) NOT NULL" +
//                                ");"
//                );
//                // 6. 创建 Transactions 表
//                stmt.execute(
//                        "CREATE TABLE dbo.Transactions (" +
//                                "  trans_id INT IDENTITY(1,1) PRIMARY KEY," +
//                                "  user_id INT NOT NULL," +
//                                "  type NVARCHAR(20) NOT NULL," +
//                                "  amount DECIMAL(18,2) NOT NULL," +
//                                "  trans_time DATETIME NOT NULL DEFAULT GETDATE()," +
//                                "  target_user_id INT NULL" +
//                                ");"
//                );
//                // 7. 外键约束
//                stmt.execute(
//                        "ALTER TABLE dbo.Transactions " +
//                                "ADD CONSTRAINT FK_Transactions_User " +
//                                "FOREIGN KEY (user_id) REFERENCES dbo.Users(user_id);"
//                );
//                stmt.execute(
//                        "ALTER TABLE dbo.Transactions " +
//                                "ADD CONSTRAINT FK_Transactions_TargetUser " +
//                                "FOREIGN KEY (target_user_id) REFERENCES dbo.Users(user_id);"
//                );
//                // 8. 创建视图：TransactionView（封装交易记录与用户名的JOIN）
//                stmt.execute(
//                        "CREATE VIEW dbo.TransactionView AS " +
//                                "SELECT t.trans_id, t.user_id AS from_user_id, u1.username AS from_username, " +
//                                "t.target_user_id AS to_user_id, u2.username AS to_username, " +
//                                "t.[type], t.amount, t.trans_time " +
//                                "FROM dbo.Transactions t " +
//                                "JOIN dbo.Users u1 ON t.user_id = u1.user_id " +
//                                "LEFT JOIN dbo.Users u2 ON t.target_user_id = u2.user_id;"
//                );
//                // 9. 插入管理员
//                stmt.execute(
//                        "INSERT INTO dbo.Admins (username, password) VALUES ('admin', '123456');"
//                );
//                // 10. 插入用户
//                stmt.execute(
//                        "INSERT INTO dbo.Users (username, password, balance, phone) VALUES " +
//                                "('zhangsan', '123456', 5000, '13800000000')," +
//                                "('lisi', '123456', 3000, '13900000000')," +
//                                "('wangwu', '123456', 10000, '13700000000');"
//                );
//                // 11. 插入交易记录
//                stmt.execute("INSERT INTO dbo.Transactions (user_id, type, amount) VALUES (1, '存款', 2000);");
//                stmt.execute("INSERT INTO dbo.Transactions (user_id, type, amount) VALUES (2, '取款', 500);");
//                stmt.execute("INSERT INTO dbo.Transactions (user_id, type, amount, target_user_id) VALUES (3, '转出', 800, 1);");
//                stmt.execute("INSERT INTO dbo.Transactions (user_id, type, amount, target_user_id) VALUES (1, '转入', 800, 3);");
//                System.out.println("数据库初始化成功！");
//                stmt.close();
//                conn.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("数据库初始化失败：" + e.getMessage());
//            }
//        }
//    }