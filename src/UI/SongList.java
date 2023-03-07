package UI;

import BeatmapLoader.Beatmap.DiffInfo;
import BeatmapLoader.Beatmap.Info;
import BeatmapLoader.Beatmap.SimpleInfo;
import BeatmapLoader.Parser;
import BeatmapPlayer.OggPlayer;
import Utils.*;

import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SongList {
    JPanel panel;
    private JButton playButton;
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
    private JTextField searchTextField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton stopButton;
    private JProgressBar beatmapTimeProgressBar;
    private JLabel beatmapTimeLabel;

    ArrayList<BeatmapCard> beatmapsCards;
    ArrayList<FolderCard> folderCards;

    SimpleInfo simpleInfo;

    Info info;

    String currentFolder;

    SongList(){
        JScrollBar scrollBar = songListScrollPane.getVerticalScrollBar();
        scrollBar.setUnitIncrement(10);
        scrollBar.setBlockIncrement(50);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Home.png")));
        backToBeatmapFolderButton.setIcon(icon);

        icon = new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Refresh.png")));
        refreshButton.setIcon(icon);

        icon = new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Search.png")));
        searchButton.setIcon(icon);

        icon = new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Play.png")));
        playButton.setIcon(icon);

        icon = new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Stop.png")));
        stopButton.setIcon(icon);

        updateSongList(Config.beatmapFolder,"");

        playButton.addActionListener(e ->{
            if(diffList.getSelectedIndex() == -1) return;
            if(Utils.beatmapPlayer.playing) Utils.beatmapPlayer.stop();
            Utils.beatmapPlayer.load(simpleInfo.path);
            Utils.beatmapPlayer.play(diffList.getSelectedIndex());
            new Thread(() -> {
                updateTimeLoop();
            }).start();
        });

        stopButton.addActionListener(e -> Utils.beatmapPlayer.stop());

        searchButton.addActionListener(e -> {
            updateSongList(currentFolder,searchTextField.getText().toLowerCase());
        });

        searchTextField.addActionListener(e -> {
            updateSongList(currentFolder,searchTextField.getText().toLowerCase());
        });

        refreshButton.addActionListener(e -> {
            updateSongList(currentFolder,"");
            searchTextField.setText("");
        });

        backToBeatmapFolderButton.addActionListener(e -> updateSongList(Config.beatmapFolder,""));
    }

    public void updateTimeLoop(){
        while(true){
            try {
                if(!Utils.beatmapPlayer.songPlayer.isAlive()) break;
                OggPlayer player = Utils.beatmapPlayer.songPlayer;
                long currentTime = player.getCurrentTime();

                int seconds = (int) (currentTime / 1000); // convert milliseconds to seconds
                int minutes = seconds / 60; // calculate the number of minutes
                seconds %= 60; // calculate the remaining seconds

                beatmapTimeLabel.setText(String.format("%02d:%02d", minutes, seconds));
                beatmapTimeProgressBar.setValue((seconds % 20) * 5);
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {}
        }
    }

    public void setInfo(SimpleInfo simpleInfo){
        this.simpleInfo = simpleInfo;
        titleLabel.setText(simpleInfo.songName + " - " + simpleInfo.songSubName);
        artistLabel.setText(simpleInfo.songAuthorName);
        mapperLabel.setText(simpleInfo.levelAuthorName);
        bpmLabel.setText(String.valueOf(simpleInfo.bpm));

        info = Parser.parseInfo(simpleInfo.path);

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

    boolean contains(SimpleInfo info,String filter){
        return info.songName.toLowerCase().contains(filter) || info.songSubName.toLowerCase().contains(filter) || info.levelAuthorName.toLowerCase().contains(filter) || info.songAuthorName.toLowerCase().contains(filter);
    }

    public void updateSongList(String path, String filter){
        currentFolder = path;
        Debug.log("updating songlist at: " + path);
        songListPanel.removeAll();
        File directoryFile = new File(path);
        if(!directoryFile.exists()){
            return;
        }

        String[] directoryList = directoryFile.list();
        assert directoryList != null;

        folderCards = new ArrayList<>();
        beatmapsCards = new ArrayList<>();

        folderCards.add(new FolderCard(directoryFile.getParent(), directoryFile.getParent(),true));
        Thread[] threads = new Thread[directoryList.length];
        Debug.log("Checking " + directoryList.length + " directory");
        for (int i = 0; i < directoryList.length; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                SimpleInfo info = Parser.parseSimpleInfo(path + "\\" + directoryList[finalI]);
                if (info != null) {
                    if(filter.equals("")) {
                        beatmapsCards.add(new BeatmapCard(info));
                    }else{
                        if(contains(info,filter)){
                            beatmapsCards.add(new BeatmapCard(info));
                        }
                    }
                } else if (new File(path + "\\" + directoryList[finalI]).isDirectory()) {
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

        new Thread(() -> {
            for (BeatmapCard beatmapsCard : beatmapsCards) {
                beatmapsCard.imgPanel.loadImage();
            }
        }).start();
        new Thread(() -> {
            for (FolderCard folderCard : folderCards) {
                folderCard.imgPanel.loadImage();
            }
        }).start();
    }
}
