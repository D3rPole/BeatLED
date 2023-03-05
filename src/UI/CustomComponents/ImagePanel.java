package UI.CustomComponents;

import Utils.Debug;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {
    Image image;
    boolean imageLoaded;
    String imgPath;
    public ImagePanel(String path) throws IOException {
        imgPath = path;
        File imgFile = new File(imgPath);
        if(!imgFile.exists()) {
            imgPath = getClass().getResource("/Assets/Image.png").getPath().replace("%20", " ");
        }
    }

    public void loadImage(){
        try {
            image = ImageIO.read(new File(imgPath)).getScaledInstance(64,64, Image.SCALE_SMOOTH);
        } catch (IOException ex) {
            Debug.log(ex);
        }
        imageLoaded = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
