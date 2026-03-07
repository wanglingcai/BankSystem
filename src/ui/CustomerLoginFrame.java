package ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import service.CustomerService;
import javax.swing.*;
import java.awt.*;
import ui.UIUtils;
/**
 * 客户登录界面（优化视觉布局，与管理员登录风格统一）
 */
public class CustomerLoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public CustomerLoginFrame() {
        setTitle("客户登录 - 银行管理系统");
        setSize(500, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // 背景面板
        ImageBackgroundPanel backgroundPanel = new ImageBackgroundPanel("images/customer_login_bg.png");
        backgroundPanel.setBounds(0, 0, 500, 380);
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        // 半透明白色表单面板（柔和视觉）
        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(new Color(255, 255, 255, 210)); // 半透明白色
        formPanel.setBounds(90, 80, 320, 230);
        backgroundPanel.add(formPanel);

        // ===== 用户名 =====
        JLabel usernameLabel = new JLabel("用户名：");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        usernameLabel.setBounds(40, 40, 80, 25);
        formPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        usernameField.setBounds(120, 40, 160, 28);
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(usernameField);

        // ===== 密码 =====
        JLabel pwdLabel = new JLabel("密  码：");
        pwdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        pwdLabel.setBounds(40, 85, 80, 25);
        formPanel.add(pwdLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        passwordField.setBounds(120, 85, 160, 28);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(passwordField);

        // ===== 登录按钮 =====
        JButton loginBtn = UIUtils.createStyledButton("登录");
        loginBtn.setBounds(50, 135, 90, 35);
        loginBtn.addActionListener(e -> doLogin());
        formPanel.add(loginBtn);

        // ===== 取消按钮 =====
        JButton cancelBtn = UIUtils.createStyledButton("取消");
        cancelBtn.setBounds(170, 135, 90, 35);
        cancelBtn.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
        });
        formPanel.add(cancelBtn);

// ===== 注册按钮 =====
        JButton registerBtn = UIUtils.createStyledButton("新用户注册");
        registerBtn.setBounds(85, 185, 150, 35);
        registerBtn.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });
        formPanel.add(registerBtn);
    }

    // 登录验证逻辑
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名或密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CustomerService service = new CustomerService();
        int userId = service.login(username, password);
        if (userId != -1) {
            JOptionPane.showMessageDialog(this, "登录成功！欢迎使用银行服务", "成功", JOptionPane.INFORMATION_MESSAGE);
            new CustomerMainFrame(userId).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}
