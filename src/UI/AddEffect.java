package UI;

import javax.swing.*;

public class AddEffect {
    private JPanel addEffectPanel;
    private JComboBox typeComboBox;
    private JSpinner fromSpinner;
    private JSpinner toSpinner;
    private JCheckBox drawBackwardsCheckBox;
    private JButton addEffectButton;
    private JButton cancelButton;
    private JTextField textField1;

    JFrame frame;

    AddEffect(){
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
    }
}
