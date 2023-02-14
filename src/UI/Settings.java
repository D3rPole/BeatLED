package UI;

import Utils.Config;

import javax.swing.*;

import static java.awt.MouseInfo.getPointerInfo;

public class Settings {
    private JPanel settingsPanel;
    private JFormattedTextField path;
    private JButton explore;
    private JButton ok;
    private JButton apply;
    private JSpinner fadeout;
    private JSpinner flash;
    private JSpinner onBrightness;

    Settings(){
        JFrame frame = new JFrame();
        frame.setContentPane(settingsPanel);
        frame.setTitle("BeatLED | Settings");
        frame.setSize(600,400);
        frame.setLocation(getPointerInfo().getLocation());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        path.setValue(Config.beatmapFolder);
        fadeout.setValue(Config.fadeoutTime);
        flash.setValue(Config.flashTime);
        onBrightness.setValue(Config.onBrightness);

        explore.addActionListener(e -> {
            JFileChooser explorer = new JFileChooser();
            explorer.setDialogTitle("Choose Beatmap Folder");
            explorer.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int err = explorer.showOpenDialog(null);
            if(err == JFileChooser.APPROVE_OPTION){
                path.setValue(explorer.getSelectedFile().toPath());
            }else{
                path.setValue("");
            }
            if(isSettingsChanged()) apply.setEnabled(true);
        });

        fadeout.addChangeListener(e -> {if(isSettingsChanged()) apply.setEnabled(true);});
        flash.addChangeListener(e -> {if(isSettingsChanged()) apply.setEnabled(true);});
        onBrightness.addChangeListener(e -> {if(isSettingsChanged()) apply.setEnabled(true);});

        ok.addActionListener(e -> {
            apply();
            frame.dispose();
        });

        apply.addActionListener(e -> apply());
    }

    boolean isSettingsChanged(){
        if(!Config.beatmapFolder.equals(path.getValue())) return true;
        if(Config.fadeoutTime != (int)fadeout.getValue()) return true;
        if(Config.onBrightness != (int) onBrightness.getValue()) return true;
        if(Config.flashTime != (int)flash.getValue()) return true;
        return false;
    }

    void apply(){
        if(isSettingsChanged()){
            Config.beatmapFolder = path.getValue().toString();
            Config.flashTime = (int)flash.getValue();
            Config.onBrightness = (int)onBrightness.getValue();
            Config.fadeoutTime = (int)fadeout.getValue();
        }
        Utils.Config.save();
        apply.setEnabled(false);
    }
}
