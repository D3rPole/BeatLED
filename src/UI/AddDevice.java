package UI;

import Lighting.DeviceLED;
import Utils.Config;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AddDevice {
    private JTextField ipTextField;
    private JTextField nameTextField;
    private JSpinner ledCountSpinner;
    private JButton addDeviceButton;
    private JButton cancelButton;
    private JSpinner portSpinner;
    private JPanel addDevicePanel;

    JFrame frame;

    AddDevice(){
        frame = new JFrame();
        frame.setContentPane(addDevicePanel);
        frame.setTitle("BeatLED | Add device");
        frame.setSize(300,250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        portSpinner.setValue(Config.defaultPort);

        cancelButton.addActionListener(e -> frame.dispose());

        addDeviceButton.addActionListener(e -> {
            if (nameTextField.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "The device must have a name!");
                return;
            }
            if (ipTextField.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "The device must a IP-Address!");
                return;
            }
            if ((int) ledCountSpinner.getValue() <= 0) {
                JOptionPane.showMessageDialog(null, "Number of LEDs must be bigger then 0!");
                return;
            }
            DeviceLED device = null;
            try {
                device = new DeviceLED(nameTextField.getText(), ipTextField.getText(), (int) portSpinner.getValue(), (int) ledCountSpinner.getValue());
            } catch (SocketException | UnknownHostException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Config.devices.add(device);
            Config.save();
            frame.dispose();
        });

        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
