package UI.V2.Preview;

import Lighting.Components.LEDstrip;

import javax.swing.*;
import java.awt.*;

class VisPanel extends JPanel {
    LEDstrip strip;
    VisPanel(LEDstrip strip){
        this.strip = strip;
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int height = this.getHeight() - 4;
        int width = this.getWidth() - 10;

        for (int i = 0; i < width; i++) {
            Lighting.Components.Color c = strip.getLEDColor((int) ((double)i / width * (strip.getLength() - 1)));
            Color color = new Color(c.r,c.g,c.b);
            g.setColor(color);
            g.fillRect(5 + i,0,1,height);
            g.setColor(Color.black);
        }
    }
}
