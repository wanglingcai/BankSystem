package ui;
import javax.swing.*;
import java.awt.*;
// 带图片背景的自定义面板
public class ImageBackgroundPanel extends JPanel {
    private Image backgroundImage; // 背景图片对象
    // 构造方法：接收图片路径，初始化背景图片
    public ImageBackgroundPanel(String imagePath) {
        try {
            backgroundImage = new ImageIcon(imagePath).getImage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "背景图片加载失败！请检查路径：" + imagePath);
            e.printStackTrace();
        }
    }
    // 重写绘制方法：将背景图片绘制到面板上
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(
                    backgroundImage,
                    0, 0,          // 图片左上角坐标
                    getWidth(),    // 图片宽度=面板宽度
                    getHeight(),   // 图片高度=面板高度
                    this
            );
        }
    }
}