package UI;

import BeatmapLoader.Beatmap.DiffInfo;
import BeatmapLoader.Beatmap.Info;
import BeatmapLoader.Beatmap.SimpleInfo;
import BeatmapLoader.Parser;
import BeatmapPlayer.OggPlayer;
import Utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SongList {
    JPanel panel;
    private JButton playButton;
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
    private JLabel beatmapTimeLabel;
    private JButton pauseButton;
    private JPanel playerPanel;
    private JSlider beatmapProgressSlider;

    ArrayList<BeatmapCard> beatmapsCards;
    ArrayList<FolderCard> folderCards;

    SimpleInfo simpleInfo;

    Info info;

    String currentFolder;

    boolean holdingSlider = false;

    SongList(){
        JScrollBar scrollBar = songListScrollPane.getVerticalScrollBar();
        scrollBar.setUnitIncrement(10);
        scrollBar.setBlockIncrement(50);

        AtomicReference<ImageIcon> icon = new AtomicReference<>(new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Home.png"))));
        backToBeatmapFolderButton.setIcon(icon.get());

        icon.set(new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Refresh.png"))));
        refreshButton.setIcon(icon.get());

        icon.set(new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Search.png"))));
        searchButton.setIcon(icon.get());

        icon.set(new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Play.png"))));
        playButton.setIcon(icon.get());

        icon.set(new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Stop.png"))));
        stopButton.setIcon(icon.get());

        icon.set(new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/pause.png"))));
        pauseButton.setIcon(icon.get());

        updateSongList(Config.beatmapFolder,"");

        pauseButton.addActionListener(e -> {
            Utils.beatmapPlayer.pause();
            if(Utils.beatmapPlayer.paused){
                icon.set(new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/Play.png"))));
                pauseButton.setIcon(icon.get());
            }else{
                icon.set(new ImageIcon(Objects.requireNonNull(Utils.loadImageFromResource("/Assets/pause.png"))));
                pauseButton.setIcon(icon.get());
            }
        });

        playButton.addActionListener(e ->{
            if(diffList.getSelectedIndex() == -1) return;
            if(Utils.beatmapPlayer.playing) Utils.beatmapPlayer.stopBeatmap();
            Utils.beatmapPlayer.load(simpleInfo.path);
            Utils.beatmapPlayer.play(diffList.getSelectedIndex(),0);
            new Thread(this::updateTimeLoop).start();
        });

        stopButton.addActionListener(e -> Utils.beatmapPlayer.stopBeatmap());

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

        beatmapProgressSlider.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                holdingSlider = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Utils.beatmapPlayer.setTime(beatmapProgressSlider.getValue() * Utils.beatmapPlayer.songPlayer.getDuration() / 100);
                holdingSlider = false;
            }
        });

        backToBeatmapFolderButton.addActionListener(e -> updateSongList(Config.beatmapFolder,""));

        stopPlaying();
    }

    public void startPlaying(){
        Utils.setEnabledRecursive(playerPanel,true);
    }

    public void stopPlaying(){
        Utils.setEnabledRecursive(playerPanel,false);
        beatmapProgressSlider.setValue(0);

        beatmapTimeLabel.setText("00:00 / 00:00");
    }

    public void updateTimeLoop(){
        while(true){
            try {
                if(!playerPanel.isEnabled()) break;
                if(Utils.beatmapPlayer.songPlayer.isPlaying()) {
                    OggPlayer player = Utils.beatmapPlayer.songPlayer;
                    long currentTime = player.getTime();

                    int seconds = (int) (currentTime / 1000); // convert milliseconds to seconds
                    int minutes = seconds / 60; // calculate the number of minutes
                    seconds %= 60; // calculate the remaining seconds

                    long duration = player.getDuration();
                    int secondsDuration = (int) (duration / 1000); // convert milliseconds to seconds
                    int minutesDuration = secondsDuration / 60; // calculate the number of minutes
                    secondsDuration %= 60; // calculate the remaining seconds
                    beatmapTimeLabel.setText(String.format("%02d:%02d", minutes, seconds) + " / " + String.format("%02d:%02d", minutesDuration, secondsDuration));
                    if (duration != 0 && !holdingSlider) {
                        beatmapProgressSlider.setValue((int) (100 * currentTime / duration));
                    }
                }
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
        songListScrollPane.getVerticalScrollBar().setValue(0);
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

        new Thread(() -> {
            for (FolderCard folderCard : folderCards) {
                folderCard.imgPanel.loadImage();
                folderCard.imgPanel.repaint();
            }
        }).start();

        new Thread(() -> {
            for (BeatmapCard beatmapsCard : beatmapsCards) {
                beatmapsCard.imgPanel.loadImage();
                beatmapsCard.imgPanel.repaint();
            }
        }).start();

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
