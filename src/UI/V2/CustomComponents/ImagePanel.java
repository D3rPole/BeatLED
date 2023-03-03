package UI.V2.CustomComponents;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {
    Image image;
    public ImagePanel(String path) throws IOException {
        image = ImageIO.read(new File(path)).getScaledInstance(64,64, Image.SCALE_SMOOTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
