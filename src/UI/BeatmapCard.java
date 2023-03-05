package UI;

import BeatmapLoader.Beatmap.SimpleInfo;
import UI.CustomComponents.ImagePanel;
import Utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class BeatmapCard {
    JPanel beatmapPanel;
    private JPanel imagePanel;
    private JLabel nameLabel;
    private JLabel artistLabel;
    private JLabel mapperLabel;

    ImagePanel imgPanel;

    private final SimpleInfo simpleInfo;

    BeatmapCard(SimpleInfo simpleInfo){
        this.simpleInfo = simpleInfo;
        try {
            GridLayout layout = new GridLayout(1,1);
            imagePanel.setLayout(layout);
            imgPanel = new ImagePanel(simpleInfo.path + "\\" + simpleInfo.coverImageFileName);
            imagePanel.add(imgPanel);
        } catch (IOException ignored) {

        }

        nameLabel.setText(simpleInfo.songName + " - " + simpleInfo.songSubName);
        artistLabel.setText(simpleInfo.songAuthorName);
        mapperLabel.setText(simpleInfo.levelAuthorName);
        init();
    }
    void init(){
        beatmapPanel.setBorder(BorderFactory.createBevelBorder(0));
        beatmapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Utils.ui.songList.deselectAll();
                Utils.ui.songList.setInfo(simpleInfo);
                beatmapPanel.setBorder(BorderFactory.createBevelBorder(1));
            }
        });
    }

    void deselect(){
        beatmapPanel.setBorder(BorderFactory.createBevelBorder(0));
    }
}
