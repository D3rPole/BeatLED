package UI.V2;

import javax.swing.*;

public class HardwareManager {
    JPanel panel;
    private JList devicesList;
    private JList effectsList;
    private JButton addEffectButton;
    private JButton removeEffectButton;
    private JButton addDeviceButton;
    private JButton removeDeviceButton;
    private JTextField deviceNameTextField;
    private JSpinner deviceSizeSpinner;
    private JSpinner devicePortSpinner;
    private JTextField deviceIpTextField;
    private JTextField effectNameTextField;
    private JComboBox effectTypeComboBox;
    private JSpinner effectFromSpinner;
    private JSpinner effectToSpinner;
    private JButton applyToEffectButton;
    private JButton applyToDeviceButton;
    private JPanel deviceInfoPanel;
    private JPanel effectInfoPanel;

    HardwareManager(){
        Utils.Utils.setEnabledRecursive(deviceInfoPanel,false);
        Utils.Utils.setEnabledRecursive(effectInfoPanel,false);
    }
}
