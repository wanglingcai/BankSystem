package ui;
import javax.swing.*;
import java.awt.*;
public class UIUtils {
    // 创建具有统一样式的按钮
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(new Color(100, 170, 240));//背景浅蓝色
        button.setForeground(Color.WHITE);//字体白色
        button.setBorder(BorderFactory.createEmptyBorder());// 移除按钮默认边框，创建无边框效果
        button.setFocusPainted(false);//取消获取焦点时的边框
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));//设置鼠标悬停时的光标为手型
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(80, 150, 230)); // 鼠标进入时，背景色变为稍深的蓝色
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(100, 170, 240)); // 鼠标离开时，恢复为原始浅蓝色
            }
        });
        return button;
    }
}