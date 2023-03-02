package UI.V2;

import BeatmapLoader.Beatmap.DiffInfo;
import BeatmapLoader.Beatmap.Info;
import BeatmapLoader.Parser;
import Utils.Config;
import Utils.Utils;

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

    ArrayList<BeatmapCard> beatmapsCards;

    Info info;

    SongList(){
        JScrollBar scrollBar = songListScrollPane.getVerticalScrollBar();
        scrollBar.setUnitIncrement(10);
        scrollBar.setBlockIncrement(50);

        updateSongList();

        playButton.addActionListener(e ->{
            if(diffList.getSelectedIndex() == -1) return;
            Utils.beatmapPlayer.load(info.path);
            Utils.beatmapPlayer.play(diffList.getSelectedIndex());
        });
    }

    public void setInfo(Info info){
        this.info = info;
        titleLabel.setText(info.songName + " - " + info.songSubName);
        artistLabel.setText(info.songAuthorName);
        mapperLabel.setText(info.levelAuthorName);
        bpmLabel.setText(String.valueOf(info.bpm));

        DiffInfo[] diffInfos = info.diffs.toArray(new DiffInfo[0]);
        diffList.setListData(diffInfos);
    }

    public void deselectAll(){
        for (int i = 0; i < beatmapsCards.size(); i++) {
            beatmapsCards.get(i).deselect();
        }
    }

    public void updateSongList(){
        songListPanel.removeAll();
        String path = Config.beatmapFolder;
        File directoryPath = new File(path);
        if(!directoryPath.exists()){
            return;
        }

        String[] directoryList = directoryPath.list();
        assert directoryList != null;

        beatmapsCards = new ArrayList<>();
        for (int i = 0; i < directoryList.length; i++) {
            Info info = Parser.parseInfo(path + "\\" +  directoryList[i]);
            if(info != null){
                beatmapsCards.add(new BeatmapCard(info));
            }
        }

        GridLayout layout = new GridLayout(beatmapsCards.size(),1);
        songListPanel.setLayout(layout);

        for (int i = 0; i < beatmapsCards.size(); i++) {
            songListPanel.add(beatmapsCards.get(i).beatmapPanel);
        }
    }
}
