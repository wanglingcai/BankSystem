package ui;
//客户登录后主界面
import ui.UIUtils;
import javax.swing.*;
import java.awt.*;
import service.CustomerService;
// 客户主界面
public class CustomerMainFrame extends JFrame {
    private final int userId; // 当前登录客户ID
    // 构造方法：初始化界面，接收当前客户ID
    public CustomerMainFrame(int userId) {
        this.userId = userId;
        // 界面配置：标题、大小、关闭方式、居中
        setTitle("客户中心 - 银行管理系统");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // 创建背景面板，设置客户主界面背景图
        ImageBackgroundPanel backgroundPanel = new ImageBackgroundPanel("images/customer_main_bg.png");
        backgroundPanel.setLayout(new BorderLayout());
        // 创建功能按钮面板（网格布局：3行2列），设置边距
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 30, 30));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));
        btnPanel.setOpaque(false); // 面板透明
        //转账按钮：打开转账窗口
        JButton transferBtn = UIUtils.createStyledButton("转账");
        transferBtn.addActionListener(e -> new TransferFrame(userId).setVisible(true));
        btnPanel.add(transferBtn);
        //存款按钮
        JButton depositBtn = UIUtils.createStyledButton("存款");
        depositBtn.addActionListener(e -> new DepositFrame(userId).setVisible(true));
        btnPanel.add(depositBtn);
        // 取款按钮
        JButton withdrawBtn = UIUtils.createStyledButton("取款");
        withdrawBtn.addActionListener(e -> new WithdrawFrame(userId).setVisible(true));
        btnPanel.add(withdrawBtn);
        // 修改密码按钮
        JButton changePwdBtn = UIUtils.createStyledButton("修改密码");
        changePwdBtn.addActionListener(e -> new ChangePwdFrame(userId).setVisible(true));
        btnPanel.add(changePwdBtn);
        // 查询余额按钮
        JButton checkBalanceBtn = UIUtils.createStyledButton("查询余额");
        checkBalanceBtn.addActionListener(e -> checkBalance());
        btnPanel.add(checkBalanceBtn);
        // 返回按钮
        JButton backBtn = UIUtils.createStyledButton("返回");
        backBtn.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
        });
        btnPanel.add(backBtn);
        // 将按钮面板添加到背景面板（居中），并设置为窗口内容面板
        backgroundPanel.add(btnPanel, BorderLayout.CENTER);
        this.setContentPane(backgroundPanel);
    }
    // 查询余额逻辑：调用服务层获取余额并弹窗显示
    private void checkBalance() {
        CustomerService service = new CustomerService();
        double balance = service.getBalance(userId);
        if (balance >= 0) {
            JOptionPane.showMessageDialog(this,
                    String.format("您的当前余额为：%.2f 元", balance),
                    "余额查询成功",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "查询余额失败，请稍后重试",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}