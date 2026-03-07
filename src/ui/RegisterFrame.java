package ui;
import javax.swing.*;
import java.awt.*;
import service.CustomerService;
import ui.UIUtils;
// 新用户注册窗口
public class RegisterFrame extends JFrame {
    private JTextField usernameField;       // 用户名输入框
    private JPasswordField passwordField;  // 密码输入框
    private JPasswordField confirmPwdField;// 确认密码输入框
    private JTextField phoneField;         // 手机号输入框
    public RegisterFrame() {
        // 基本窗口设置
        setTitle("新用户注册 - 银行管理系统");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        // 用户名
        panel.add(new JLabel("用户名：", JLabel.RIGHT));
        usernameField = new JTextField(15);
        panel.add(usernameField);
        // 密码
        panel.add(new JLabel("密码：", JLabel.RIGHT));
        passwordField = new JPasswordField(15);
        panel.add(passwordField);
        // 确认密码
        panel.add(new JLabel("确认密码：", JLabel.RIGHT));
        confirmPwdField = new JPasswordField(15);
        panel.add(confirmPwdField);
        // 手机号
        panel.add(new JLabel("手机号：", JLabel.RIGHT));
        phoneField = new JTextField(15);
        panel.add(phoneField);
        // 按钮区
        JButton registerBtn = UIUtils.createStyledButton("确认注册");
        registerBtn.addActionListener(e -> doRegister());
        panel.add(registerBtn);
        JButton cancelBtn = UIUtils.createStyledButton("取消");
        cancelBtn.addActionListener(e -> {
            // ✅ 修改点：返回到客户登录界面
            dispose(); // 关闭注册窗口
            new CustomerLoginFrame().setVisible(true);
        });
        panel.add(cancelBtn);

        add(panel);
    }
    // 注册逻辑（用户名、密码、手机号校验）
    private void doRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPwd = new String(confirmPwdField.getPassword()).trim();
        String phone = phoneField.getText().trim();
        // 校验输入
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (username.length() < 3 || username.length() > 15) {
            JOptionPane.showMessageDialog(this, "用户名需3-15个字符！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPwd)) {
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "密码长度不能少于6位！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "手机号格式错误（需11位数字）！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 注册调用
        CustomerService service = new CustomerService();
        boolean success = service.register(username, password, phone);
        if (success) {
            JOptionPane.showMessageDialog(this, "注册成功！即将跳转到登录界面", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new CustomerLoginFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "注册失败（用户名可能已存在）", "错误", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            confirmPwdField.setText("");
        }
    }
}
