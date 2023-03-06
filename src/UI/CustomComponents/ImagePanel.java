package UI.CustomComponents;

import Utils.Debug;
import Utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ImagePanel extends JPanel {
    Image image;
    boolean imageLoaded;

    boolean resource;
    String imgPath;
    public ImagePanel(String path) throws IOException {
        if(new File(path).exists()){
            imgPath = path;
        }else {
            resource = true;
            InputStream in = getClass().getResourceAsStream(path);
            if(in == null){
                imgPath = "/Assets/Image.png";
            }else{
                imgPath = path;
            }
        }
    }



    public void loadImage(){
        if(resource){
            image = Utils.loadImageFromResource(imgPath);
        }else {
            try {
                image = ImageIO.read(new File(imgPath)).getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            } catch (IOException ex) {
                Debug.log(ex);
            }
        }
        imageLoaded = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
