package UI.V2;

import Lighting.DeviceLED;
import Lighting.Effect;
import Utils.Config;
import Utils.Utils;

import javax.swing.*;

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
        Utils.setEnabledRecursive(deviceInfoPanel,false);
        Utils.setEnabledRecursive(effectInfoPanel,false);

        updateDeviceList();

        effectTypeComboBox.addItem("Back Lights");
        effectTypeComboBox.addItem("Ring Lights");
        effectTypeComboBox.addItem("Left Lasers");
        effectTypeComboBox.addItem("Right Lasers");
        effectTypeComboBox.addItem("Center Lights");

        devicesList.addListSelectionListener(e -> {
            deviceLED = devicesList.getSelectedValue();
            Utils.setEnabledRecursive(deviceInfoPanel,true);
            Utils.setEnabledRecursive(effectInfoPanel,true);
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
