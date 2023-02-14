package UI;

import Lighting.DeviceLED;

import javax.swing.*;

public class AddDevice {
    private JTextField ipTextField;
    private JTextField nameTextField;
    private JSpinner ledCountSpinner;
    private JButton addDeviceButton;
    private JButton cancelButton;
    private JSpinner portSpinner;
    private JPanel addDevicePanel;

    AddDevice(){
        JFrame frame = new JFrame();
        frame.setContentPane(addDevicePanel);
        frame.setTitle("BeatLED | Settings");
        frame.setSize(300,250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        cancelButton.addActionListener(e -> frame.dispose());

        addDeviceButton.addActionListener(e -> {
            DeviceLED device = new DeviceLED()
        });
    }
}
