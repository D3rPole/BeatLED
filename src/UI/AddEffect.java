package UI;

import Lighting.DeviceLED;
import Utils.Config;

import javax.swing.*;

public class AddEffect {
    private JPanel addEffectPanel;
    private JComboBox<Item> typeComboBox;
    private JSpinner fromSpinner;
    private JSpinner toSpinner;
    private JCheckBox drawBackwardsCheckBox;
    private JButton addEffectButton;
    private JButton cancelButton;
    private JTextField nameTextField;

    JFrame frame;

    AddEffect(DeviceLED device){
        frame = new JFrame();
        frame.setContentPane(addEffectPanel);
        frame.setTitle("BeatLED | Add effect");
        frame.setSize(300,250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        typeComboBox.addItem(new Item("Back Lights",0));
        typeComboBox.addItem(new Item("Ring Lights",1));
        typeComboBox.addItem(new Item("Left Lasers",2));
        typeComboBox.addItem(new Item("Right Lasers",3));
        typeComboBox.addItem(new Item("Center Lights",4));

        cancelButton.addActionListener(e -> frame.dispose());

        addEffectButton.addActionListener(e -> {
            if (nameTextField.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "The device must have a name!");
                return;
            }
            if((int)toSpinner.getValue() < (int)fromSpinner.getValue()){
                JOptionPane.showMessageDialog(null, "to index cannot be bigger then from index");
                return;
            }
            device.addEffect(nameTextField.getText(), typeComboBox.getSelectedIndex(), (int)fromSpinner.getValue(), (int)toSpinner.getValue(), drawBackwardsCheckBox.isSelected());
            Config.save();
            frame.dispose();
        });
    }
}
