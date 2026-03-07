package ui;

import service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import ui.UIUtils;

// 管理员主界面 —— 支持查询、修改、删除、新增交易记录
public class AdminMainFrame extends JFrame {
    private DefaultTableModel tableModel;// 交易记录表格模型
    private JTable transactionTable;// 交易记录表格
    private final AdminService adminService;// 管理员服务类，用于处理业务逻辑

    public AdminMainFrame() {
        this.adminService = new AdminService();
        setTitle("管理员 - 交易记录管理");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        // 背景面板
        ImageBackgroundPanel backgroundPanel = new ImageBackgroundPanel("images/admin_login_bg.png");
        backgroundPanel.setLayout(new BorderLayout());

        // ============ 查询区 ============
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));//流式布局
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("用户ID："));
        JTextField userIdField = new JTextField(5);
        searchPanel.add(userIdField);
        searchPanel.add(new JLabel("交易类型："));
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"", "存款", "取款", "转账(转入)", "转账(转出)"});
        searchPanel.add(typeCombo);
        //时间
        searchPanel.add(new JLabel("交易日期："));
        JTextField dateField = new JTextField(10);
        searchPanel.add(dateField);
        searchPanel.add(new JLabel("(YYYY-MM-DD)"));
        JButton searchBtn = UIUtils.createStyledButton("查询");
        searchBtn.addActionListener(e -> {
            String userId = userIdField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String date = dateField.getText().trim(); // 获取日期输入
            // 日期格式验证（简单验证YYYY-MM-DD格式）
            if (!date.isEmpty() && !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "请输入正确的日期格式：YYYY-MM-DD", "格式错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                List<String[]> data = adminService.searchTransactions(userId, type, date);
                refreshTransactionTable(data);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "查询失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        searchPanel.add(searchBtn);

        // ============ 表格 ============
        String[] columnNames = {"交易ID", "转出方ID", "转出方用户名", "转入方ID", "转入方用户名", "类型", "金额", "时间"};
        tableModel = new DefaultTableModel(columnNames, 0);// 交易记录表格模型
        transactionTable = new JTable(tableModel);// 交易记录表格
        JScrollPane scrollPane = new JScrollPane(transactionTable);// 交易记录表格滚动面板

        // ============ 底部按钮区 ============
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);// 底部按钮区透明
        JButton addBtn = UIUtils.createStyledButton("新增交易记录");
        addBtn.addActionListener(e -> addTransaction());
        buttonPanel.add(addBtn);
        JButton editBtn = UIUtils.createStyledButton("修改选中记录");
        editBtn.addActionListener(e -> editTransaction());
        buttonPanel.add(editBtn);
        JButton deleteBtn = UIUtils.createStyledButton("删除选中记录");
        deleteBtn.addActionListener(e -> deleteTransaction());
        buttonPanel.add(deleteBtn);
        JButton backBtn = UIUtils.createStyledButton("返回");
        backBtn.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
        });
        buttonPanel.add(backBtn);

        backgroundPanel.add(searchPanel, BorderLayout.NORTH);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(backgroundPanel);

        loadAllTransactions();
    }

    // 加载所有交易记录
    private void loadAllTransactions() {
        try {
            List<String[]> data = adminService.getAllTransactions();
            refreshTransactionTable(data);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载交易记录失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 刷新交易记录表格
    private void refreshTransactionTable(List<String[]> data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (String[] row : data) {
                tableModel.addRow(row);
            }
        }
    }

    // 删除选中交易记录
    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int transId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "确认删除交易记录 ID=" + transId + "？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        if (adminService.deleteTransaction(transId)) {
            tableModel.removeRow(selectedRow);// 从表格模型中删除选中行
            JOptionPane.showMessageDialog(this, "删除成功！");
        } else {
            JOptionPane.showMessageDialog(this, "删除失败：记录不存在或数据库错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 修改选中交易记录
    private void editTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一条记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int transId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String type = tableModel.getValueAt(selectedRow, 5).toString();
        String amountStr = tableModel.getValueAt(selectedRow, 6).toString();

        // 【修复1】从表格中读取原有的转入方ID，避免更新时丢失
        Object targetIdObj = tableModel.getValueAt(selectedRow, 3);
        Integer targetUserId = null;
        if (targetIdObj != null && !targetIdObj.toString().trim().isEmpty()) {
            targetUserId = Integer.parseInt(targetIdObj.toString().trim());
        }

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"", "存款", "取款", "转账(转入)", "转账(转出)"});
        // 设置默认选中当前记录的交易类型
        typeCombo.setSelectedItem(type);
        JTextField amountField = new JTextField(amountStr);
        Object[] message = {
                "交易类型：", typeCombo,
                "金额（元）：", amountField
        };
        // 显示修改交易记录对话框
        int option = JOptionPane.showConfirmDialog(this, message, "修改交易记录", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newType = (String)typeCombo.getSelectedItem();
            String newAmountStr = amountField.getText().trim();
            try {
                double newAmount = Double.parseDouble(newAmountStr);
                // 【修复1】把原来的 null 替换成我们刚刚读取的 targetUserId
                boolean success = adminService.updateTransaction(transId, newType, newAmount, targetUserId);
                if (success) {
                    tableModel.setValueAt(newType, selectedRow, 5);
                    tableModel.setValueAt(newAmountStr, selectedRow, 6);
                    JOptionPane.showMessageDialog(this, "修改成功！");
                } else {
                    JOptionPane.showMessageDialog(this, "修改失败：数据库更新异常", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "金额必须为数字！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 新增交易记录
    private void addTransaction() {
        JTextField fromIdField = new JTextField();
        JTextField toIdField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField amountField = new JTextField();

        Object[] message = {
                "转出方ID：", fromIdField,
                "转入方ID（非转账可为空）：", toIdField, // 提示管理员这里可以不填
                "类型（存款/取款/转账(转入)/转账(转出)）：", typeField,
                "金额（元）：", amountField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "新增交易记录", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int fromId = Integer.parseInt(fromIdField.getText().trim());

                // 【修复2】对转入方ID进行判空处理，为空时赋予 null
                String toIdStr = toIdField.getText().trim();
                Integer toId = null;
                if (!toIdStr.isEmpty()) {
                    toId = Integer.parseInt(toIdStr);
                }

                String type = typeField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());

                boolean success = adminService.insertTransaction(fromId, type, amount, toId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "新增成功！");
                    loadAllTransactions(); // 重新加载表格以展示最新数据
                } else {
                    JOptionPane.showMessageDialog(this, "新增失败，请检查输入或数据库状态", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID 和金额必须为有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}