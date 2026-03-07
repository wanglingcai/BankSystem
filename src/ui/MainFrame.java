package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ui.UIUtils;
// 银行管理系统主窗口
public class MainFrame extends JFrame {

    public MainFrame() {
        // 窗口基础配置：标题、大小、关闭方式、居中、禁止缩放
        setTitle("银行管理系统");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 关键：使用绝对布局，手动控制组件位置（标题+两个按钮）
        setLayout(null);

        // 1. 创建自定义背景面板（保留原有背景图）
        ImageBackgroundPanel backgroundPanel = new ImageBackgroundPanel("images/login_bg.png");
        backgroundPanel.setBounds(0, 0, 800, 600); // 背景面板占满整个窗口
        backgroundPanel.setLayout(null); // 背景面板也用绝对布局，方便放子组件

        // 2. 添加【标题】（中间偏上位置，微软雅黑加粗36号，白色）
        JLabel titleLabel = new JLabel("银行管理系统");
        Font titleFont = new Font("微软雅黑", Font.BOLD, 36);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // 文字水平居中
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);   // 文字垂直居中
        titleLabel.setBounds((800 - 300) / 2, 80, 300, 50); // 组件位置居中
        backgroundPanel.add(titleLabel);

        // 3. 管理员登录按钮（左中偏下位置，美化样式）
        JButton adminBtn = UIUtils.createStyledButton("管理员登录");
        // 按钮位置：左中偏下（x=150，y=350），大小150x40
        adminBtn.setBounds(150, 350, 150, 40);
        // 保留原有功能：点击打开管理员登录界面，关闭当前窗口
        adminBtn.addActionListener(e -> {
            new AdminLoginFrame().setVisible(true);
            dispose();
        });
        backgroundPanel.add(adminBtn);

        // 4. 客户登录按钮（右中偏下位置，美化样式）
        JButton customerBtn = UIUtils.createStyledButton("客户登录");
        // 按钮位置：右中偏下（x=500，y=350），大小150x40
        customerBtn.setBounds(500, 350, 150, 40);
        // 保留原有功能：点击打开客户登录界面，关闭当前窗口
        customerBtn.addActionListener(e -> {
            new CustomerLoginFrame().setVisible(true);
            dispose();
        });
        backgroundPanel.add(customerBtn);

        // 将背景面板设为窗口内容面板（确保背景和组件都显示）
        this.setContentPane(backgroundPanel);
    }

    // 主方法：启动程序，显示主窗口
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}