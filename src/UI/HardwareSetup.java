package UI;

import Lighting.DeviceLED;
import Utils.Config;
import Utils.Utils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.SocketException;
import java.net.UnknownHostException;

public class HardwareSetup {
    private JButton removeDeviceButton;
    private JButton addEffectButton;
    private JButton addDeviceButton;
    private JButton removeEffectButton;
    private JPanel hardwareSetupPanel;
    private JList deviceList;
    private JList EffectList;
    private JButton applyChangesButton;
    private JTextField nameTextField;
    private JTextField ipTextField;
    private JSpinner portSpinner;
    private JSpinner ledCountSpinner;
    private JPanel deviceSettingsPanel;

    int selectedIndex = -1;
    HardwareSetup(){
        JFrame frame = new JFrame();
        frame.setContentPane(hardwareSetupPanel);
        frame.setTitle("BeatLED | Hardware setup");
        frame.setSize(800,600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        addDeviceButton.addActionListener(e -> {
            AddDevice addDevice = new AddDevice();
            addDevice.frame.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosed(WindowEvent e) {
                    updateDeviceList();
                }
            });
        });

        removeDeviceButton.addActionListener(e -> {
            Config.devices.remove(deviceList.getSelectedIndex());
            updateDeviceList();
            Config.save();
        });

        applyChangesButton.addActionListener(e -> {
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

            try {
                Config.devices.set(selectedIndex,new DeviceLED(nameTextField.getText(), ipTextField.getText(), (int) portSpinner.getValue(), (int) ledCountSpinner.getValue()));
            } catch (SocketException | UnknownHostException ex) {
                JOptionPane.showMessageDialog(null, ex);
                return;
            }
            updateDeviceList();
        });

        deviceList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    DeviceLED selectedDevice = (DeviceLED) ((Item)deviceList.getSelectedValue()).getObj();
                    selectedIndex = deviceList.getSelectedIndex();
                    nameTextField.setText(selectedDevice.name);
                    ipTextField.setText(selectedDevice.device.getIp());
                    portSpinner.setValue(selectedDevice.device.getPort());
                    ledCountSpinner.setValue(selectedDevice.ledStrip.getLength());
                    Utils.setEnabledRecursive(deviceSettingsPanel,true);
                    removeDeviceButton.setEnabled(true);
                }
            }
        });

        addEffectButton.addActionListener(e -> new AddEffect());

        updateDeviceList();
        Utils.setEnabledRecursive(deviceSettingsPanel,false);
        removeDeviceButton.setEnabled(false);
    }

    void updateDeviceList(){
        Item[] items = new Item[Config.devices.size()];

        for (int i = 0; i < items.length; i++) {
            items[i] = new Item();
            items[i].setObj(Config.devices.get(i));
        }

        deviceList.setListData(items);
    }
}
