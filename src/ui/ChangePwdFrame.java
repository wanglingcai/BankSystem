package ui;
import service.CustomerService;
import javax.swing.*;
import java.awt.*;
import ui.UIUtils;
// 修改密码窗口
public class ChangePwdFrame extends JFrame {
    private final int userId; // 当前客户ID
    private JTextField phoneField; // 绑定手机号输入框
    private JPasswordField newPwdField; // 新密码输入框
    private JPasswordField confirmPwdField; // 确认新密码输入框
    // 构造方法：初始化界面，接收当前客户ID
    public ChangePwdFrame(int userId) {
        this.userId = userId;
        // 界面配置：标题、大小、关闭方式、居中、禁止缩放
        setTitle("修改密码 - 手机号验证");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        // 创建面板（网格布局：4行2列），设置边距
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        // 绑定手机号输入项
        panel.add(new JLabel("绑定手机号：", JLabel.RIGHT));
        phoneField = new JTextField(15);
        phoneField.setToolTipText("输入账户绑定的手机号");
        panel.add(phoneField);
        // 新密码输入项
        panel.add(new JLabel("新密码：", JLabel.RIGHT));
        newPwdField = new JPasswordField(15);
        newPwdField.setToolTipText("不少于6位");
        panel.add(newPwdField);
        // 确认新密码输入项
        panel.add(new JLabel("确认新密码：", JLabel.RIGHT));
        confirmPwdField = new JPasswordField(15);
        panel.add(confirmPwdField);
        // 确认修改按钮：触发修改密码逻辑
        JButton confirmBtn = UIUtils.createStyledButton("确认修改");
        confirmBtn.addActionListener(e -> doChangePwd());
        panel.add(confirmBtn);
        // 取消按钮：关闭当前窗口
        JButton cancelBtn = UIUtils.createStyledButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        panel.add(cancelBtn);
        add(panel);
    }
    // 执行修改密码逻辑：校验输入，调用服务层完成密码修改
    private void doChangePwd() {
        String phone = phoneField.getText().trim();
        String newPwd = new String(newPwdField.getPassword()).trim();
        String confirmPwd = new String(confirmPwdField.getPassword()).trim();
        // 空值校验（所有字段必填）
        if (phone.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 调用服务层执行修改，成功则关闭窗口
        CustomerService service = new CustomerService();
        boolean success = service.changePassword(userId, phone, newPwd, confirmPwd);
        if (success) {
            dispose();
        } else {
            // 失败则清空密码输入框
            newPwdField.setText("");
            confirmPwdField.setText("");
        }
    }
}