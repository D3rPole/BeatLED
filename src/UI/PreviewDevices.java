package UI;

import Lighting.Components.LEDstrip;
import Utils.Config;
import Utils.Debug;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class PreviewDevices {
    private JPanel previewPanel;
    private JPanel devicesPanel;

    private VisPanel[] panels;
    Timer timer;
    PreviewDevices(){
        JFrame frame = new JFrame();
        frame.setContentPane(previewPanel);
        frame.setTitle("BeatLED | Add device");
        frame.setSize(600,400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        GridLayout layout = new GridLayout(Config.devices.size(),2);
        devicesPanel.setLayout(layout);

        panels = new VisPanel[Config.devices.size()];
        for (int i = 0; i < Config.devices.size(); i++) {
            JLabel label = new JLabel(Config.devices.get(i).name, JLabel.LEFT);
            devicesPanel.add(label);

            VisPanel visPanel = new VisPanel(Config.devices.get(i).ledStrip);
            visPanel.setPreferredSize(new Dimension(9999,30));
            panels[i] = visPanel;
            devicesPanel.add(visPanel);
        }

        timer = new Timer();
        update();
    }

    void update(){
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < panels.length; i++) {
                    VisPanel panel = panels[i];
                    panel.repaint();
                }
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                }
            }
        },0,16);
    }
}