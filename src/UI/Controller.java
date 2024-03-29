package UI;

import BeatmapLoader.Beatmap.Event;
import Lighting.Components.Color;
import UI.Preview.Preview;
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
    private JSlider strobeFreqSlider;
    private JSpinner strobeFreqSpinner;
    private JSlider strobeDutySlider;
    private JSlider strobeBrightnessSlider;
    private JSpinner strobeDutySpinner;
    private JSpinner strobeBrightnessSpinner;
    private JButton strobesToggleButton;

    private Preview preview;

    private boolean chroma = false;

    Controller(){
        updatePreview();

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
            chroma = false;
            if(manualControllCheckBox.isSelected() && !activeCheckBox.isSelected()){
                activeCheckBox.doClick();
            }
        });

        sendCommandButton.addActionListener(e -> {
            BeatmapLoader.Beatmap.Event event = new Event();
            event.setValue((int)valueSpinner.getValue());
            if(chroma) {
                event.color = new Color((int) redSpinner.getValue(), (int) greenSpinner.getValue(), (int) blueSpinner.getValue());
            }
            event.type = ((int)((Item)typeComboBox.getSelectedItem()).getObj());
            Utils.ledController.lightEvent(event);
            Debug.log(event);
        });

        setupManualControl();
    }

    void updatePreview(){
        previewPanel.removeAll();
        GridLayout layout = new GridLayout(1,1);
        previewPanel.setLayout(layout);

        preview = new Preview();
        previewPanel.add(preview.previewPanel);
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


        strobeBrightnessSlider.addChangeListener(e -> strobeBrightnessSpinner.setValue(strobeBrightnessSlider.getValue()));
        strobeDutySlider.addChangeListener(e -> strobeDutySpinner.setValue(strobeDutySlider.getValue()));
        strobeFreqSlider.addChangeListener(e -> strobeFreqSpinner.setValue(strobeFreqSlider.getValue() / 4));

        redSpinner.addChangeListener(e -> {
            if((int)redSpinner.getValue() > 255) redSpinner.setValue(255);
            if((int)redSpinner.getValue() < 0) redSpinner.setValue(0);
            chroma = true;
        });
        blueSpinner.addChangeListener(e -> {
            if((int)blueSpinner.getValue() > 255) blueSpinner.setValue(255);
            if((int)blueSpinner.getValue() < 0) blueSpinner.setValue(0);
            chroma = true;
        });
        greenSpinner.addChangeListener(e -> {
            if((int)greenSpinner.getValue() > 255) greenSpinner.setValue(255);
            if((int)greenSpinner.getValue() < 0) greenSpinner.setValue(0);
            chroma = true;
        });

        strobeBrightnessSpinner.addChangeListener(e -> {
            if((int)strobeBrightnessSpinner.getValue() > 100) strobeBrightnessSpinner.setValue(100);
            if((int)strobeBrightnessSpinner.getValue() < 0) strobeBrightnessSpinner.setValue(0);

            strobeBrightnessSlider.setValue((int)strobeBrightnessSpinner.getValue());
            if(Utils.ledController == null) return;
            Utils.ledController.setStrobeSettings((int)strobeFreqSpinner.getValue(),(int)strobeDutySpinner.getValue(),(int)strobeBrightnessSpinner.getValue());
        });
        strobeDutySpinner.addChangeListener(e -> {
            if((int)strobeDutySpinner.getValue() > 100) strobeDutySpinner.setValue(100);
            if((int)strobeDutySpinner.getValue() < 0) strobeDutySpinner.setValue(0);

            strobeDutySlider.setValue((int)strobeDutySpinner.getValue());
            if(Utils.ledController == null) return;
            Utils.ledController.setStrobeSettings((int)strobeFreqSpinner.getValue(),(int)strobeDutySpinner.getValue(),(int)strobeBrightnessSpinner.getValue());
        });
        strobeFreqSpinner.addChangeListener(e -> {
            if((int)strobeFreqSpinner.getValue() > 25) strobeFreqSpinner.setValue(25);
            if((int)strobeFreqSpinner.getValue() < 1) strobeFreqSpinner.setValue(1);

            strobeFreqSlider.setValue((int)strobeFreqSpinner.getValue() * 4);
            if(Utils.ledController == null) return;
            Utils.ledController.setStrobeSettings((int)strobeFreqSpinner.getValue(),(int)strobeDutySpinner.getValue(),(int)strobeBrightnessSpinner.getValue());
        });

        strobeBrightnessSpinner.setValue(Config.onBrightness);
        strobeDutySpinner.setValue(50);
        strobeFreqSpinner.setValue(10);

        strobesToggleButton.addActionListener(e -> {
            Utils.ledController.strobing = !Utils.ledController.strobing;
            if(Utils.ledController.strobing){
                strobesToggleButton.setText("Stop");
            }else{
                strobesToggleButton.setText("Start");
            }
        });
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

    public void updateStrobe(){
        Utils.ledController.setStrobeSettings((int)strobeFreqSpinner.getValue(),(int)strobeDutySpinner.getValue(),(int)strobeBrightnessSpinner.getValue());
    }
}
