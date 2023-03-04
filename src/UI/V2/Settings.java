package UI.V2;

import Utils.Config;

import javax.swing.*;
import java.io.File;

public class Settings {
    JPanel panel;
    private JButton applySettingsButton;
    private JButton resetSettingsButton;
    private JTextField beatmapFolderPathTextField;
    private JButton openExplorerButton;
    private JSpinner fadeoutTimeSpinner;
    private JSpinner flashTimeSpinner;
    private JSpinner onBrightnessSpinner;

    Settings(){
        beatmapFolderPathTextField.setText(Config.beatmapFolder);
        fadeoutTimeSpinner.setValue(Config.fadeoutTime);
        flashTimeSpinner.setValue(Config.flashTime);
        onBrightnessSpinner.setValue(Config.onBrightness);

        applySettingsButton.addActionListener(e -> {
            if(!verifySettings()) return;
            Config.beatmapFolder = beatmapFolderPathTextField.getText();
            Config.fadeoutTime = (int)fadeoutTimeSpinner.getValue();
            Config.flashTime = (int)flashTimeSpinner.getValue();
            Config.onBrightness = (int)onBrightnessSpinner.getValue();
            Config.save();
        });

        openExplorerButton.addActionListener(e -> {
            JFileChooser explorer = new JFileChooser();
            explorer.setDialogTitle("Choose Beatmap Folder");
            explorer.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int err = explorer.showOpenDialog(null);
            if(err == JFileChooser.APPROVE_OPTION){
                beatmapFolderPathTextField.setText(explorer.getSelectedFile().getPath());
                Config.beatmapFolder = explorer.getSelectedFile().getPath();
                Config.save();
            }else{
                beatmapFolderPathTextField.setText("");
            }
        });
    }

    boolean verifySettings(){
        if(!new File(beatmapFolderPathTextField.getText()).exists()){
            JOptionPane.showMessageDialog(null, "Beatmap folder path is invalid");
            return false;
        }
        if((int)fadeoutTimeSpinner.getValue() <= 0){
            JOptionPane.showMessageDialog(null, "Fadeout time must be over 0ms");
            return false;
        }
        if((int)flashTimeSpinner.getValue() <= 0){
            JOptionPane.showMessageDialog(null, "Flash time must be over 0ms");
            return false;
        }
        if((int)onBrightnessSpinner.getValue() <= 0){
            JOptionPane.showMessageDialog(null, "On brightness must be over 0%");
            return false;
        }
        return true;
    }
}
