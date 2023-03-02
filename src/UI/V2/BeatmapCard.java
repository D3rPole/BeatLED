package UI.V2;

import BeatmapLoader.Beatmap.Info;
import Utils.Config;
import Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class BeatmapCard {
    JPanel beatmapPanel;
    private JPanel imagePanel;
    private JLabel nameLabel;
    private JLabel artistLabel;
    private JLabel mapperLabel;

    private Info info;

    BeatmapCard(Info info){
        this.info = info;
        try {
            GridLayout layout = new GridLayout(1,1);
            imagePanel.setLayout(layout);
            ImagePanel img = new ImagePanel(info.path + "\\" + info.coverImageFileName);
            imagePanel.add(img);
        } catch (IOException e) {

        }

        nameLabel.setText(info.songName + " - " + info.songSubName);
        artistLabel.setText(info.songAuthorName);
        mapperLabel.setText(info.levelAuthorName);
        init();
    }
    void init(){
        beatmapPanel.setBorder(BorderFactory.createBevelBorder(0));
        beatmapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Utils.ui.songList.deselectAll();
                Utils.ui.songList.setInfo(info);
                beatmapPanel.setBorder(BorderFactory.createBevelBorder(1));
            }
        });
    }

    void deselect(){
        beatmapPanel.setBorder(BorderFactory.createBevelBorder(0));
    }
}
