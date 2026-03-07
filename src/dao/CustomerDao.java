package dao;
import javax.swing.*;
import java.sql.*;
/**
 * 客户DAO层：处理客户登录、存款、取款、转账、改密码等数据库操作
 */
public class CustomerDao {
    // 1.客户登录验证
    // @param username 用户名
    // @param password 密码
    // @return 验证通过返回用户ID，失败返回-1
    public int login(String username, String password) {
        String sql = "SELECT user_id FROM dbo.Users WHERE username = ? AND password = ?";
        try (Connection conn = BaseDao.getConnection();// 获取数据库连接
             PreparedStatement ps = conn.prepareStatement(sql)) {// 预编译SQL语句
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery(); // 执行查询，返回结果集
            return rs.next() ? rs.getInt("user_id") : -1;
            // 若结果集有数据，返回用户ID；否则返回-1
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    // 2.校验客户密码（用于敏感操作验证）
    // @param userId 客户ID
    // @param password 待校验密码
    // @return 校验通过返回true，失败返回false
    public boolean verifyPassword(int userId, String password) {
        String sql = "SELECT user_id FROM dbo.Users WHERE user_id = ? AND password = ?";
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, password);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // 3.存款操作（事务连接，更新余额+插入交易记录）
    // @param userId 存款客户ID
    // @param amount 存款金额
    // @param conn 事务连接（确保操作原子性）
    // @return 操作成功返回true，失败返回false
    // @throws SQLException SQL执行异常
    public boolean deposit(int userId, double amount, Connection conn) throws SQLException {
        // 更新用户余额
        String updateSql = "UPDATE dbo.Users SET balance = balance + ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            if (ps.executeUpdate() <= 0) return false;
        }
        // 插入存款交易记录
        String insertSql = "INSERT INTO dbo.Transactions (user_id, [type], amount, trans_time) VALUES (?, ?, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, userId);
            ps.setString(2, "存款");
            ps.setDouble(3, amount);
            return ps.executeUpdate() > 0;
        }
    }

    // 4.取款操作（事务连接，校验余额+更新余额+插入交易记录）
    // @param userId 取款客户ID
    // @param amount 取款金额
    // @param conn 事务连接（确保操作原子性）
    // @return 操作成功返回true，失败返回false
    // @throws SQLException SQL执行异常
    public boolean withdraw(int userId, double amount, Connection conn) throws SQLException {
        // 校验余额充足
        String checkSql = "SELECT balance FROM dbo.Users WHERE user_id = ? AND balance >= ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, userId);
            ps.setDouble(2, amount);
            if (!ps.executeQuery().next()) return false;
        }

        // 更新用户余额
        String updateSql = "UPDATE dbo.Users SET balance = balance - ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            if (ps.executeUpdate() <= 0) return false;
        }

        // 插入取款交易记录
        String insertSql = "INSERT INTO dbo.Transactions (user_id, [type], amount, trans_time) VALUES (?, ?, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, userId);
            ps.setString(2, "取款");
            ps.setDouble(3, amount);
            return ps.executeUpdate() > 0;
        }
    }
    // 5.转账操作（事务连接，校验余额+更新双方余额+插入两条交易记录）
    // @param fromUserId 转出客户ID
    // @param toUserId 转入客户ID
    // @param amount 转账金额
    // @param conn 事务连接（确保操作原子性）
    // @return 操作成功返回true，失败返回false
    // @throws SQLException SQL执行异常
    public boolean transfer(int fromUserId, int toUserId, double amount, Connection conn) throws SQLException {
        // 校验转出用户余额充足
        String checkFromSql = "SELECT balance FROM dbo.Users WHERE user_id = ? AND balance >= ?";
        try (PreparedStatement ps = conn.prepareStatement(checkFromSql)) {
            ps.setInt(1, fromUserId);
            ps.setDouble(2, amount);
            if (!ps.executeQuery().next()) return false;
        }
        // 校验转入用户存在
        String checkToSql = "SELECT user_id FROM dbo.Users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkToSql)) {
            ps.setInt(1, toUserId);
            if (!ps.executeQuery().next()) return false;
        }
        // 转出用户扣钱
        String updateFromSql = "UPDATE dbo.Users SET balance = balance - ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateFromSql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, fromUserId);
            if (ps.executeUpdate() <= 0) return false;
        }
        // 转入用户加钱
        String updateToSql = "UPDATE dbo.Users SET balance = balance + ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateToSql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, toUserId);
            if (ps.executeUpdate() <= 0) return false;
        }
        // 插入转出交易记录
        String insertFromSql = "INSERT INTO dbo.Transactions (user_id, [type], amount, trans_time) VALUES (?, ?, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(insertFromSql)) {
            ps.setInt(1, fromUserId);
            ps.setString(2, "转账（转出）");
            ps.setDouble(3, amount);
            ps.executeUpdate();
        }

        // 插入转入交易记录
        String insertToSql = "INSERT INTO dbo.Transactions (user_id, [type], amount, trans_time) VALUES (?, ?, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(insertToSql)) {
            ps.setInt(1, toUserId);
            ps.setString(2, "转账（转入）");
            ps.setDouble(3, amount);
            return ps.executeUpdate() > 0;
        }
    }
    // 6.验证客户手机号（修改密码前校验）
    // @param userId 客户ID
    // @param phone 待校验手机号
    // @return 校验通过返回true，失败返回false
    public boolean verifyPhone(int userId, String phone) {
        String sql = "SELECT user_id FROM dbo.Users WHERE user_id = ? AND phone = ?";
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, phone);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // 7.修改客户密码
    // @param userId 客户ID
    // @param newPassword 新密码
    // @return 修改成功返回true，失败返回false
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE dbo.Users SET password = ? WHERE user_id = ?";
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // 8.通过用户名查询用户ID（转账时使用）
    // @param username 用户名
    // @return 存在返回用户ID，不存在返回-1
    public int getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM dbo.Users WHERE username = ?";
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("user_id") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
   // 9.根据客户ID查询余额
    // @param userId 客户ID
    // @return 成功返回余额，用户不存在/异常返回-1
    public double getBalance(int userId) {
        String sql = "SELECT balance FROM dbo.Users WHERE user_id = ?";
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("balance") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}