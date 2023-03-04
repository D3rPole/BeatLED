package UI.V2;

import UI.V2.CustomComponents.ImagePanel;
import Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import Utils.Debug;

public class FolderCard {
    JPanel folderCardPanel;
    private JPanel imagePanel;
    private JLabel folderName;
    private JLabel typeLabel;

    private String path;

    boolean isSelected = false;

    FolderCard(String path,String name, boolean parent){
        try {
            GridLayout layout = new GridLayout(1,1);
            imagePanel.setLayout(layout);
            String imgPath;
            if(parent){
                imgPath = getClass().getResource("/Assets/Back.png").getPath().replace("%20", " ");
                typeLabel.setText("Parent folder");
            }else {
                imgPath = getClass().getResource("/Assets/Directory.png").getPath().replace("%20", " ");
            }
            ImagePanel img = new ImagePanel(imgPath);
            imagePanel.add(img);
        } catch (IOException e) {

        }

        this.path = path;
        folderName.setText(name);
        init();
    }

    void init(){
        folderCardPanel.setBorder(BorderFactory.createBevelBorder(0));
        folderCardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isSelected){
                    Utils.ui.songList.updateSongList(path);
                }
                Utils.ui.songList.deselectAll();
                folderCardPanel.setBorder(BorderFactory.createBevelBorder(1));
                isSelected = true;
            }
        });
    }

    void deselect(){
        folderCardPanel.setBorder(BorderFactory.createBevelBorder(0));
        isSelected = false;
    }
}
