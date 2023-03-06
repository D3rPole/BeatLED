package UI;

import UI.CustomComponents.ImagePanel;
import Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class FolderCard {
    JPanel folderCardPanel;
    private JPanel imagePanel;
    private JLabel folderName;
    private JLabel typeLabel;

    private String path;

    ImagePanel imgPanel;

    boolean isSelected = false;

    FolderCard(String path,String name, boolean parent){
        try {
            GridLayout layout = new GridLayout(1,1);
            imagePanel.setLayout(layout);
            String imgPath;
            if(parent){
                imgPath = "/Assets/Back.png";
                typeLabel.setText("Parent folder");
            }else {
                imgPath = "/Assets/Directory.png";
            }
            imgPanel = new ImagePanel(imgPath);
            imagePanel.add(imgPanel);
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
                    Utils.ui.songList.updateSongList(path,"");
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
