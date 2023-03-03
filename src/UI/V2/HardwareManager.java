package UI.V2;

import Lighting.DeviceLED;
import Lighting.Effect;
import Utils.*;

import javax.swing.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class HardwareManager {
    JPanel panel;
    private JList<DeviceLED> devicesList;
    private JList<Effect> effectsList;
    private JButton addEffectButton;
    private JButton removeEffectButton;
    private JButton addDeviceButton;
    private JButton removeDeviceButton;
    private JTextField deviceNameTextField;
    private JSpinner deviceSizeSpinner;
    private JSpinner devicePortSpinner;
    private JTextField deviceIpTextField;
    private JTextField effectNameTextField;
    private JComboBox<String> effectTypeComboBox;
    private JSpinner effectFromSpinner;
    private JSpinner effectToSpinner;
    private JButton applyToEffectButton;
    private JButton applyToDeviceButton;
    private JPanel deviceInfoPanel;
    private JPanel effectInfoPanel;

    private DeviceLED deviceLED;

    HardwareManager(){
        Utils.setEnabledRecursive(effectInfoPanel,false);
        removeDeviceButton.setEnabled(false);

        updateDeviceList();

        effectTypeComboBox.addItem("Back Lights");
        effectTypeComboBox.addItem("Ring Lights");
        effectTypeComboBox.addItem("Left Lasers");
        effectTypeComboBox.addItem("Right Lasers");
        effectTypeComboBox.addItem("Center Lights");

        devicesList.addListSelectionListener(e -> {
            deviceLED = devicesList.getSelectedValue();
            if(deviceLED == null){
                Utils.setEnabledRecursive(effectInfoPanel,false);
                return;
            }
            Utils.setEnabledRecursive(effectInfoPanel,true);
            removeDeviceButton.setEnabled(true);
            updateEffectList();

            deviceNameTextField.setText(deviceLED.name);
            devicePortSpinner.setValue(deviceLED.device.getPort());
            deviceIpTextField.setText(deviceLED.device.getIp());
            deviceSizeSpinner.setValue(deviceLED.ledStrip.getLength());
        });

        effectsList.addListSelectionListener(e -> {
            if(effectsList.getSelectedValue() == null) return;
            Effect effect = effectsList.getSelectedValue();

            effectNameTextField.setText(effect.name);
            effectFromSpinner.setValue(effect.fromLedIndex);
            effectToSpinner.setValue(effect.toLedIndex);
            effectTypeComboBox.setSelectedIndex(effect.type);
        });

        applyToDeviceButton.addActionListener(e -> {
            if(verifyDeviceData()){
                try {
                    deviceLED.changeDevice(deviceNameTextField.getText(),deviceIpTextField.getText(),(int)devicePortSpinner.getValue(),(int)deviceSizeSpinner.getValue());
                    Config.save();
                    int index = devicesList.getSelectedIndex();
                    updateDeviceList();
                    devicesList.setSelectedIndex(index);
                } catch (SocketException | UnknownHostException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        });
        addDeviceButton.addActionListener(e -> {
            if(verifyDeviceData()){
                try {
                    Config.devices.add(new DeviceLED(deviceNameTextField.getText(),deviceIpTextField.getText(),(int)devicePortSpinner.getValue(),(int)deviceSizeSpinner.getValue()));
                    Config.save();
                    updateDeviceList();
                    devicesList.setSelectedIndex(Config.devices.size() - 1);
                } catch (SocketException | UnknownHostException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        });
        removeDeviceButton.addActionListener(e -> {
            if(devicesList.getSelectedIndex() == -1) return;
            Config.devices.remove(devicesList.getSelectedIndex());
            Config.save();
            updateDeviceList();
            removeDeviceButton.setEnabled(false);
        });
    }

    boolean verifyDeviceData(){
        if(deviceNameTextField.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Device name can't be empty");
            return false;
        }
        if((int)deviceSizeSpinner.getValue() <= 0){
            JOptionPane.showMessageDialog(null, "Size must be higher then 0");
            return false;
        }
        if((int)devicePortSpinner.getValue() <= 0){
            JOptionPane.showMessageDialog(null, "Port must be higher then 0");
            return false;
        }
        try {
            InetAddress.getByName(deviceIpTextField.getText());
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Invalid IP address \n" + e);
        }
        return true;
    }

    void updateDeviceList(){
        if(Utils.ui != null) Utils.ui.controller.updatePreview();
        DeviceLED[] deviceLED = Config.devices.toArray(new DeviceLED[0]);
        devicesList.setListData(deviceLED);
    }

    void updateEffectList(){
        effectsList.setListData(deviceLED.effects);
    }
}
