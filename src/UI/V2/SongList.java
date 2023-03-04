package UI.V2;

import BeatmapLoader.Beatmap.DiffInfo;
import BeatmapLoader.Beatmap.Info;
import BeatmapLoader.Parser;
import Utils.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class SongList {
    JPanel panel;
    private JButton playButton;
    private JButton stopButton;
    private JList songList;
    private JList<DiffInfo> diffList;
    private JPanel songListPanel;
    private JScrollPane songListScrollPane;
    private JLabel titleLabel;
    private JLabel artistLabel;
    private JLabel mapperLabel;
    private JLabel bpmLabel;
    private JLabel chromaLabel;
    private JButton backToBeatmapFolderButton;

    ArrayList<BeatmapCard> beatmapsCards;
    ArrayList<FolderCard> folderCards;

    Info info;

    SongList(){
        JScrollBar scrollBar = songListScrollPane.getVerticalScrollBar();
        scrollBar.setUnitIncrement(10);
        scrollBar.setBlockIncrement(50);

        updateSongList(Config.beatmapFolder);

        playButton.addActionListener(e ->{
            if(diffList.getSelectedIndex() == -1) return;
            Utils.beatmapPlayer.load(info.path);
            Utils.beatmapPlayer.play(diffList.getSelectedIndex());
        });

        stopButton.addActionListener(e -> Utils.beatmapPlayer.stop());

        backToBeatmapFolderButton.addActionListener(e -> updateSongList(Config.beatmapFolder));
    }

    public void setInfo(Info info){
        this.info = info;
        titleLabel.setText(info.songName + " - " + info.songSubName);
        artistLabel.setText(info.songAuthorName);
        mapperLabel.setText(info.levelAuthorName);
        bpmLabel.setText(String.valueOf(info.bpm));

        DiffInfo[] diffInfos = info.diffs.toArray(new DiffInfo[0]);
        diffList.setListData(diffInfos);
        diffList.setSelectedIndex(0);
    }

    public void deselectAll(){
        for (int i = 0; i < folderCards.size(); i++) {
            folderCards.get(i).deselect();
        }
        for (int i = 0; i < beatmapsCards.size(); i++) {
            beatmapsCards.get(i).deselect();
        }
    }

    public void updateSongList(String path){
        Debug.log(path);
        songListPanel.removeAll();
        File directoryPath = new File(path);
        if(!directoryPath.exists()){
            return;
        }

        String[] directoryList = directoryPath.list();
        assert directoryList != null;

        folderCards = new ArrayList<>();
        beatmapsCards = new ArrayList<>();

        folderCards.add(new FolderCard(directoryPath.getParent(), directoryPath.getParent(),true));
        Thread[] threads = new Thread[directoryList.length];

        for (int i = 0; i < directoryList.length; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                Info info = Parser.parseInfo(path + "\\" + directoryList[finalI]);
                if (info != null) {
                    beatmapsCards.add(new BeatmapCard(info));
                } else if (directoryPath.isDirectory()) {
                    folderCards.add(new FolderCard(path + "\\" + directoryList[finalI], directoryList[finalI],false));
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            try {
                t.join(); // Wait for each thread to complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //GridLayout layout = new GridLayout(beatmapsCards.size() + folderCards.size() + 1,1);
        GridBagLayout layout = new GridBagLayout();
        songListPanel.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;

        for (FolderCard folderCard : folderCards) {
            c.gridy++;
            songListPanel.add(folderCard.folderCardPanel,c);
        }
        for (BeatmapCard beatmapsCard : beatmapsCards) {
            c.gridy++;
            songListPanel.add(beatmapsCard.beatmapPanel,c);
        }

        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        songListPanel.add(new JPanel(),c);
        Debug.log("list reloaded");
    }
}
