package ui;
import service.CustomerService;
import javax.swing.*;
import java.awt.*;
import ui.UIUtils;
//取款操作窗口
public class WithdrawFrame extends JFrame {
    private final int userId; // 当前登录客户ID
    private JTextField amountField; // 取款金额输入框
    private JPasswordField passwordField; // 密码输入框
    // 构造方法：初始化界面，接收当前客户ID
    public WithdrawFrame(int userId) {
        this.userId = userId;
        setTitle("取款操作");
        setSize(380, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        // 创建面板，设置边距
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        // 取款金额输入项
        panel.add(new JLabel("取款金额（元）：", JLabel.RIGHT));
        amountField = new JTextField(15);
        amountField.setToolTipText("请输入正数");
        panel.add(amountField);
        // 密码输入项
        panel.add(new JLabel("验证密码：", JLabel.RIGHT));
        passwordField = new JPasswordField(15);
        panel.add(passwordField);
        // 确认取款按钮：触发取款逻辑
        JButton confirmBtn =  UIUtils.createStyledButton("确认取款");
        confirmBtn.addActionListener(e -> doWithdraw());
        panel.add(confirmBtn);
        // 取消按钮：关闭当前窗口
        JButton cancelBtn =  UIUtils.createStyledButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        panel.add(cancelBtn);
        add(panel);
    }
    // 执行取款逻辑：校验输入，调用服务层完成取款
    private void doWithdraw() {
        String amountStr = amountField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        // 空值校验
        if (amountStr.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "金额和密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 金额格式校验（必须为数字）
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "金额必须是数字！", "错误", JOptionPane.ERROR_MESSAGE);
            amountField.setText("");
            return;
        }
        // 调用服务层执行取款，成功则清空输入框
        CustomerService service = new CustomerService();
        boolean success = service.withdraw(userId, amount, password);
        if (success) {
            amountField.setText("");
            passwordField.setText("");
        }
    }
}