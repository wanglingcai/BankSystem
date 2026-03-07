package service;
import dao.CustomerDao;
import dao.BaseDao;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
//新增
import java.sql.PreparedStatement;
// 客户业务逻辑层
// 负责处理：登录、存款、取款、转账、修改密码、查询余额等操作
public class CustomerService {
    private final CustomerDao customerDao = new CustomerDao();
    // 1. 客户登录
    // @param username 用户名
    // @param password 密码
    // @return 存在返回用户ID，不存在返回-1
    public int login(String username, String password) {
        return customerDao.login(username, password);
    }
    // 2. 存款业务
    // @param userId 客户ID
    // @param amount 存款金额
    // @param password 密码
    // @return 成功返回true，失败返回false
    public boolean deposit(int userId, double amount, String password) {
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "存款金额必须大于0！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!customerDao.verifyPassword(userId, password)) {
            JOptionPane.showMessageDialog(null, "密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try (Connection conn = BaseDao.getConnection()) {
            conn.setAutoCommit(false);// 手动提交事务
            boolean success = customerDao.deposit(userId, amount, conn);
            if (success) {
                conn.commit();
                JOptionPane.showMessageDialog(null, "存款成功！当前余额已更新", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                conn.rollback();//回滚事务撤销所有在事务中执行的数据库操作，恢复到事务开始前的状态
                JOptionPane.showMessageDialog(null, "存款失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();// 打印异常信息到控制台
            JOptionPane.showMessageDialog(null, "存款异常！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    // 3. 取款业务
    // @param userId 客户ID
    // @param amount 取款金额
    // @param password 密码
    // @return 成功返回true，失败返回false
    public boolean withdraw(int userId, double amount, String password) {
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "取款金额必须大于0！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!customerDao.verifyPassword(userId, password)) {
            JOptionPane.showMessageDialog(null, "密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try (Connection conn = BaseDao.getConnection()) {
            conn.setAutoCommit(false);
            boolean success = customerDao.withdraw(userId, amount, conn);
            if (success) {
                conn.commit();
                JOptionPane.showMessageDialog(null, "取款成功！当前余额已更新", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "取款失败（余额不足或账户异常）！", "错误", JOptionPane.ERROR_MESSAGE);
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "取款异常！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    // 4. 转账业务
    // @param fromUserId 转出客户ID
    // @param toUsername 转入用户名
    // @param amount 转账金额
    // @param password 转出客户密码
    // @return 成功返回true，失败返回false
    public boolean transfer(int fromUserId, String toUsername, double amount, String password) {
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "转账金额必须大于0！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!customerDao.verifyPassword(fromUserId, password)) {
            JOptionPane.showMessageDialog(null, "密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int toUserId = customerDao.getUserIdByUsername(toUsername);
        if (toUserId == -1) {
            JOptionPane.showMessageDialog(null, "转入用户名不存在！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (toUserId == fromUserId) {
            JOptionPane.showMessageDialog(null, "不能向自己转账！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try (Connection conn = BaseDao.getConnection()) {
            conn.setAutoCommit(false);
            boolean success = customerDao.transfer(fromUserId, toUserId, amount, conn);
            if (success) {
                conn.commit();
                JOptionPane.showMessageDialog(null, "转账成功！对方已收到款项", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "转账失败（余额不足或账户异常）！", "错误", JOptionPane.ERROR_MESSAGE);
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "转账异常！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    // 5. 修改密码
    // @param userId 客户ID
    // @param phone 手机号
    // @param newPwd 新密码
    // @param confirmPwd 确认密码
    // @return 成功返回true，失败返回false
    public boolean changePassword(int userId, String phone, String newPwd, String confirmPwd) {
        if (!newPwd.equals(confirmPwd)) {
            JOptionPane.showMessageDialog(null, "两次密码输入不一致！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (newPwd.length() < 6) {
            JOptionPane.showMessageDialog(null, "密码长度不能少于6位！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!customerDao.verifyPhone(userId, phone)) {
            JOptionPane.showMessageDialog(null, "手机号验证失败（与账户绑定手机号不一致）！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean success = customerDao.updatePassword(userId, newPwd);
        if (success) {
            JOptionPane.showMessageDialog(null, "密码修改成功！下次登录请使用新密码", "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "密码修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
        return success;
    }
    // 6. 查询余额
    // @param userId 客户ID
    // @return 客户余额
    public double getBalance(int userId) {
        return customerDao.getBalance(userId);
    }
    //注册
    public boolean register(String username, String password, String phone) {
        // 添加基本验证
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "用户名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(null, "密码长度不能少于6位！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (phone == null || !phone.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(null, "请输入有效的11位手机号！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try (Connection conn = BaseDao.getConnection()) {
            String sql = "INSERT INTO dbo.Users(Username, Password, Phone) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, phone);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "注册成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "注册失败（可能用户名已存在）！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
