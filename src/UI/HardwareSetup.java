package UI;

import Lighting.DeviceLED;
import Lighting.Effect;
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
    private JList<Item> deviceList;
    private JList<Item> effectList;
    private JButton applyChangesButton;
    private JTextField nameTextField;
    private JTextField ipTextField;
    private JSpinner portSpinner;
    private JSpinner ledCountSpinner;
    private JPanel deviceSettingsPanel;
    private JTextField effectNameTextField;
    private JComboBox typeComboBox;
    private JSpinner fromSpinner;
    private JSpinner toSpinner;
    private JCheckBox reversedCheckBox;
    private JButton applyEffectChangesButton;
    private JPanel changeEffectPanel;

    int selectedIndex = -1;
    int selectedEffectIndex = -1;
    HardwareSetup(){
        JFrame frame = new JFrame();
        frame.setContentPane(hardwareSetupPanel);
        frame.setTitle("BeatLED | Hardware setup");
        frame.setSize(800,600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        typeComboBox.addItem(new Item("Back Lights",0));
        typeComboBox.addItem(new Item("Ring Lights",1));
        typeComboBox.addItem(new Item("Left Lasers",2));
        typeComboBox.addItem(new Item("Right Lasers",3));
        typeComboBox.addItem(new Item("Center Lights",4));

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
                Config.save();
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
                DeviceLED selectedDevice = (DeviceLED) (deviceList.getSelectedValue()).getObj();
                selectedIndex = deviceList.getSelectedIndex();
                nameTextField.setText(selectedDevice.name);
                ipTextField.setText(selectedDevice.device.getIp());
                portSpinner.setValue(selectedDevice.device.getPort());
                ledCountSpinner.setValue(selectedDevice.ledStrip.getLength());
                updateEffectList();
                Utils.setEnabledRecursive(deviceSettingsPanel,true);
                Utils.setEnabledRecursive(changeEffectPanel,false);
                removeDeviceButton.setEnabled(true);
            }
            }
        });

        addEffectButton.addActionListener(e -> {
            AddEffect addEffect = new AddEffect(Config.devices.get(selectedIndex));
            addEffect.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    updateEffectList();
                }
            });
        });

        removeEffectButton.addActionListener(e -> {
            Config.devices.get(selectedIndex).removeEffect(effectList.getSelectedIndex());
            updateEffectList();
            Config.save();
        });

        effectList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 1){
                    Utils.setEnabledRecursive(changeEffectPanel, true);
                    selectedEffectIndex = effectList.getSelectedIndex();
                    Effect effect = Config.devices.get(selectedIndex).effects[selectedEffectIndex];

                    effectNameTextField.setText(effect.name);
                    typeComboBox.setSelectedIndex(effect.type);
                    fromSpinner.setValue(effect.fromLedIndex);
                    toSpinner.setValue(effect.toLedIndex);
                    reversedCheckBox.setSelected(effect.reversed);
                }
            }
        });

        applyEffectChangesButton.addActionListener(f -> {
            Effect effect = Config.devices.get(selectedIndex).effects[selectedEffectIndex];
            effect.name = effectNameTextField.getText();
            effect.type = typeComboBox.getSelectedIndex();
            effect.fromLedIndex = (int)fromSpinner.getValue();
            effect.toLedIndex = (int)toSpinner.getValue();
            effect.reversed = reversedCheckBox.isSelected();
            updateEffectList();
            Config.save();
        });

        updateDeviceList();
        Utils.setEnabledRecursive(deviceSettingsPanel,false);
        removeDeviceButton.setEnabled(false);
    }

    void updateEffectList(){
        Item[] items = new Item[Config.devices.get(selectedIndex).effects.length];

        for (int i = 0; i < items.length; i++) {
            items[i] = new Item();
            items[i].setObj(Config.devices.get(selectedIndex).effects[i]);
        }

        effectList.setListData(items);
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
