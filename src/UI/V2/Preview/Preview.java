package UI.V2.Preview;

import Utils.Config;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Preview {
    public JPanel previewPanel;
    private JPanel deviceNamesPanel;
    private JPanel previewsPanel;

    private Timer timer;

    private VisPanel[] visPanels;

    public Preview(){
        GridLayout layout = new GridLayout(Config.devices.size(),1);
        previewsPanel.setLayout(layout);
        deviceNamesPanel.setLayout(layout);
        visPanels = new VisPanel[Config.devices.size()];

        for (int i = 0; i < Config.devices.size(); i++) {
            GridBagLayout layoutB = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            visPanels[i] = new VisPanel(Config.devices.get(i).ledStrip);

            previewsPanel.add(visPanels[i]);
            deviceNamesPanel.add(new JLabel(Config.devices.get(i).name));
        }
        timer = new Timer();
        update();
    }

    void update(){
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < visPanels.length; i++) {
                    visPanels[i].repaint();
                }
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                }
            }
        },0,16);
    }
}
