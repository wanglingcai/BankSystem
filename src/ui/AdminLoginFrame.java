package ui;

import service.AdminService;
import javax.swing.*;
import java.awt.*;
import ui.UIUtils;
// 管理员登录界面
public class AdminLoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField pwdField;

    public AdminLoginFrame() {
        setTitle("管理员登录");
        setSize(500, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);// 关闭时仅隐藏窗口
        setLocationRelativeTo(null);
        setLayout(null);

        // 背景面板
        ImageBackgroundPanel backgroundPanel = new ImageBackgroundPanel("images/admin_login_bg.png");
        backgroundPanel.setBounds(0, 0, 500, 380);
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        // 半透明白色面板，用于放置输入框和按钮
        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(new Color(255, 255, 255, 200));
        formPanel.setBounds(90, 80, 320, 200);
        backgroundPanel.add(formPanel);

        // 用户名
        JLabel usernameLabel = new JLabel("用户名：");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        usernameLabel.setBounds(40, 40, 80, 25);
        formPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        usernameField.setBounds(120, 40, 160, 28);
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(usernameField);

        // 密码
        JLabel pwdLabel = new JLabel("密  码：");
        pwdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        pwdLabel.setBounds(40, 85, 80, 25);
        formPanel.add(pwdLabel);

        pwdField = new JPasswordField();
        pwdField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        pwdField.setBounds(120, 85, 160, 28);
        pwdField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(pwdField);

        // 登录按钮
        JButton loginBtn = UIUtils.createStyledButton("登录");
        loginBtn.setBounds(60, 140, 90, 35);
        loginBtn.addActionListener(e -> doLogin());
        formPanel.add(loginBtn);

        // 取消按钮
        JButton cancelBtn = UIUtils.createStyledButton("取消");
        cancelBtn.setBounds(170, 140, 90, 35);
        cancelBtn.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
        });
        formPanel.add(cancelBtn);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();// 去除首尾空格
        String password = new String(pwdField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AdminService service = new AdminService();
        if (service.login(username, password)) {
            JOptionPane.showMessageDialog(this, "登录成功");
            new AdminMainFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
            pwdField.setText("");// 清空密码输入框
        }
    }
}
