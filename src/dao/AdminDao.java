package dao;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// 管理员 DAO
public class AdminDao {
    // 查询所有交易记录（包含转账、存款、取款）
    public List<String[]> getAllTransactions() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM dbo.TransactionView " +
                "ORDER BY trans_time DESC";
        try (Connection conn = BaseDao.getConnection();
             // 预编译 SQL 语句(防止sql注入攻击,提高执行效率)
             PreparedStatement ps = conn.prepareStatement(sql);
             // 执行查询并获取结果集
             ResultSet rs = ps.executeQuery()) {
            // 遍历结果集，将每一行数据转换为字符串数组
            while (rs.next()) {
                String[] row = new String[]{
                        rs.getString("trans_id"),
                        rs.getString("from_user_id"),
                        rs.getString("from_username"),
                        rs.getString("to_user_id"),
                        rs.getString("to_username"),
                        rs.getString("type"),
                        rs.getString("amount"),
                        rs.getString("trans_time")
                };
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "加载交易记录失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }
    //条件查询
    public List<String[]> queryTransactionsByCondition(String userId, String type, String transDate) {
        List<String[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM dbo.TransactionView WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();
        if (userId != null && !userId.isEmpty()) {
            sql.append("AND from_user_id = ? ");
            try {
                params.add(Integer.parseInt(userId));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "用户ID必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
                return list;
            }
        }
        if (type != null && !type.isEmpty()) {
            sql.append("AND [type] = ? ");
            params.add(type);
        }
        if (transDate != null && !transDate.isEmpty()) {
            sql.append("AND CONVERT(VARCHAR(10), trans_time, 120) = ? ");
            params.add(transDate);
        }
        sql.append("ORDER BY trans_time DESC");
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));//第几个?,三选一
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] row = new String[]{
                            rs.getString("trans_id"),
                            rs.getString("from_user_id"),
                            rs.getString("from_username"),
                            rs.getString("to_user_id"),
                            rs.getString("to_username"),
                            rs.getString("type"),
                            rs.getString("amount"),
                            rs.getString("trans_time")
                    };
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询交易记录失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }
    //新增交易记录
    public boolean insertTransaction(int userId, String type, double amount, Integer targetUserId) {
        String sql = "INSERT INTO dbo.Transactions (user_id, [type], amount, target_user_id, trans_time) " +
                "VALUES (?, ?, ?, ?, GETDATE())";
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            if (targetUserId != null) ps.setInt(4, targetUserId);
            else ps.setNull(4, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "新增交易记录失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    //修改数据
    public boolean updateTransaction(int transId, String newType, double newAmount, Integer targetUserId) {
        String sql = "UPDATE dbo.Transactions SET [type] = ?, amount = ?, target_user_id = ? WHERE trans_id = ?";
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newType);
            ps.setDouble(2, newAmount);
            if (targetUserId != null) ps.setInt(3, targetUserId);
            else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, transId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "修改交易记录失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    //删除交易记录
    public boolean deleteTransaction(int transId) {
        String sql = "DELETE FROM dbo.Transactions WHERE trans_id = ?";
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "删除交易记录失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    //管理员登录验证
    public boolean verifyAdmin(String username, String password) {
        String sql = "SELECT admin_id FROM dbo.Admins WHERE username = ? AND password = ?";
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "管理员登录验证失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}