package UI;

import javax.swing.*;

import static java.awt.MouseInfo.getPointerInfo;

public class HardwareSetup {
    private JButton removeDeviceButton;
    private JButton addEffectButton;
    private JButton addDeviceButton;
    private JButton removeEffectButton;
    private JPanel hardwareSetupPanel;

    HardwareSetup(){
        JFrame frame = new JFrame();
        frame.setContentPane(hardwareSetupPanel);
        frame.setTitle("BeatLED | Hardware setup");
        frame.setSize(800,600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        addDeviceButton.addActionListener(e -> new AddDevice());
    }
}
