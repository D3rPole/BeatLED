package UI.V2;

import BeatmapLoader.Beatmap.Event;
import UI.V2.Preview.Preview;
import Utils.*;

import javax.swing.*;
import java.awt.*;

public class Controller {
    JPanel panel;
    private JCheckBox activeCheckBox;
    private JCheckBox manualControllCheckBox;
    private JComboBox<Item> typeComboBox;
    private JSpinner redSpinner;
    private JSpinner greenSpinner;
    private JSpinner blueSpinner;
    private JPanel previewPanel;
    private JButton sendCommandButton;
    private JPanel manualControlPanel;
    private JPanel infoPanel;
    private JLabel fpsTargetLabel;
    private JLabel fpsLabel;
    private JLabel frameTimeLabel;
    private JSpinner valueSpinner;

    private Preview preview;

    Controller(){
        GridLayout layout = new GridLayout(1,1);
        previewPanel.setLayout(layout);

        preview = new Preview();
        previewPanel.add(preview.previewPanel);

        Utils.setEnabledRecursive(infoPanel,false);
        Utils.setEnabledRecursive(manualControlPanel,false);

        activeCheckBox.addActionListener(e -> {
            Utils.ledController.setActive(activeCheckBox.isSelected());
            Utils.setEnabledRecursive(infoPanel,activeCheckBox.isSelected());
            if(!activeCheckBox.isSelected() && manualControllCheckBox.isSelected()){
                manualControllCheckBox.doClick();
            }
        });

        manualControllCheckBox.addActionListener(e -> {
            Utils.setEnabledRecursive(manualControlPanel,manualControllCheckBox.isSelected());
            if(manualControllCheckBox.isSelected() && !activeCheckBox.isSelected()){
                activeCheckBox.doClick();
            }
        });

        sendCommandButton.addActionListener(e -> {
            BeatmapLoader.Beatmap.Event event = new Event();
            event.setValue((int)valueSpinner.getValue());
            event.type = ((int)((UI.V2.Item)typeComboBox.getSelectedItem()).getObj());
            Utils.ledController.lightEvent(event);
            Debug.log(event);
        });

        setupManualControl();
    }

    void setupManualControl(){
        typeComboBox.addItem(new Item("Back lights",0));
        typeComboBox.addItem(new Item("Ring lights",1));
        typeComboBox.addItem(new Item("Left laser",2));
        typeComboBox.addItem(new Item("Right laser",3));
        typeComboBox.addItem(new Item("Center lights",4));

        typeComboBox.addItem(new Item("Ring rotation",8));
        typeComboBox.addItem(new Item("Ring zoom",9));

        typeComboBox.addItem(new Item("Left laser speed",12));
        typeComboBox.addItem(new Item("Right laser speed",13));

        typeComboBox.addItem(new Item("Boost lights",5));
    }

    public void updateFPS(long FPS, long frameTime, long target){
        if(!activeCheckBox.isSelected()) return;
        fpsTargetLabel.setText(target + " FPS");
        fpsLabel.setText(FPS + " FPS");
        frameTimeLabel.setText(frameTime / 1000 + " µs");
    }

    public void setActiveCheckBox(boolean active){
        if(activeCheckBox.isSelected() != active){
            activeCheckBox.doClick();
        }
    }
}
