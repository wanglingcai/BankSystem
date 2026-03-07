package ui;
//转账操作界面
import service.CustomerService;
import javax.swing.*;
import java.awt.*;
import ui.UIUtils;
// 转账操作窗口
public class TransferFrame extends JFrame {
    private final int fromUserId; // 转出用户ID
    private JTextField toUsernameField; // 转入用户名输入框
    private JTextField amountField; // 转账金额输入框
    private JPasswordField passwordField; // 密码输入框
    // 构造方法：初始化界面，接收转出用户ID
    public TransferFrame(int fromUserId) {
        this.fromUserId = fromUserId;
        setTitle("转账操作");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        // 创建面板，设置边距
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        // 转入用户名输入项
        panel.add(new JLabel("转入用户名：", JLabel.RIGHT));
        toUsernameField = new JTextField(15);
        panel.add(toUsernameField);
        // 转账金额输入项
        panel.add(new JLabel("转账金额（元）：", JLabel.RIGHT));
        amountField = new JTextField(15);
        amountField.setToolTipText("请输入正数");
        panel.add(amountField);
        // 密码输入项
        panel.add(new JLabel("验证密码：", JLabel.RIGHT));
        passwordField = new JPasswordField(15);
        panel.add(passwordField);
        // 确认转账按钮：触发转账逻辑
        JButton confirmBtn = UIUtils.createStyledButton("确认转账");
        confirmBtn.addActionListener(e -> doTransfer());
        panel.add(confirmBtn);
        // 取消按钮：关闭当前窗口
        JButton cancelBtn = UIUtils.createStyledButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        panel.add(cancelBtn);
        add(panel);
    }

    // 执行转账逻辑：校验输入，调用服务层完成转账
    private void doTransfer() {
        String toUsername = toUsernameField.getText().trim();
        String amountStr = amountField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (toUsername.isEmpty() || amountStr.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
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
        CustomerService service = new CustomerService();
        boolean success = service.transfer(fromUserId, toUsername, amount, password);
        if (success) {
            toUsernameField.setText("");
            amountField.setText("");
            passwordField.setText("");
        }
    }
}