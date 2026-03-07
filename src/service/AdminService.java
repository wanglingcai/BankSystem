package service;
import dao.AdminDao;
import java.util.List;
public class AdminService {

    // 管理员数据访问对象，用于数据库交互
    private final AdminDao adminDao = new AdminDao();
    // 获取所有交易记录
    public List<String[]> getAllTransactions() {
        return adminDao.getAllTransactions();
    }
    // 根据用户ID和交易类型查询交易记录
    public List<String[]> searchTransactions(String userId, String type,String transDate) {
        return adminDao.queryTransactionsByCondition(userId, type, transDate);
    }
    // 保留原有方法以兼容现有代码
    public List<String[]> searchTransactions(String userId, String type) {
        return searchTransactions(userId, type, null);
    }
    // 插入新交易记录
    public boolean insertTransaction(int userId, String type, double amount, Integer targetUserId) {
        return adminDao.insertTransaction(userId, type, amount, targetUserId);
    }
    // 更新交易记录
    public boolean updateTransaction(int transId, String newType, double newAmount, Integer targetUserId) {
        return adminDao.updateTransaction(transId, newType, newAmount, targetUserId);
    }
    // 删除交易记录
    public boolean deleteTransaction(int transId) {
        return adminDao.deleteTransaction(transId);
    }
    // 验证管理员登录
    public boolean verifyAdmin(String username, String password) {
        return adminDao.verifyAdmin(username, password);
    }
    // 管理员登录方法，调用 verifyAdmin 验证
    public boolean login(String username, String password) {
        return verifyAdmin(username, password);
    }
}
