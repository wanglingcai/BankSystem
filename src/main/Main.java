package main;

import ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * 程序主入口类
 * 作用：启动程序，初始化并显示登录选择界面（MainFrame）
 */
public class Main {
    public static void main(String[] args) {
        // Swing 组件需在事件调度线程（EDT）中创建，避免线程安全问题
        SwingUtilities.invokeLater(() -> {
            // 创建登录选择界面实例
            MainFrame mainFrame = new MainFrame();
            // 显式设置界面可见（必须调用，否则窗口不显示）
            mainFrame.setVisible(true);
        });
    }
}