package UI;

import BeatmapPlayer.BeatmapPlayer;
import Beatsaber.Beatmap;
import Beatsaber.BeatmapDiff;
import Beatsaber.MapLoader;
import Utils.Config;
import Utils.Utils;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class MainUI extends Component {
    private JPanel panelMain;
    private JList songlistList;
    private JScrollPane songlistPane;
    private JScrollPane diffPane;
    private JTextPane infoPane;
    private JList diffList;
    private JButton play;
    private JButton stop;
    private JPanel ledController;
    private JCheckBox activeLEDController;
    private JTextPane logs;
    private JScrollPane logScroll;
    private JPanel songList;
    private JPanel info;
    private JButton settingsButton;
    private JButton reloadList;
    private JButton clearLogsButton;

    public MainUI(){
        JFrame frame = new JFrame();
        frame.setContentPane(panelMain);
        frame.setTitle("BeatLED");
        frame.setSize(1200,600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        activeLEDController.addActionListener(e -> Utils.ledController.setActive(activeLEDController.isSelected()));

        stop.addActionListener(e -> BeatmapPlayer.getInstance().stop());

        settingsButton.addActionListener(e -> {
            Settings settings = new Settings();
        });

        loadBeatmapList(Config.beatmapFolder);

        reloadList.addActionListener(e -> {
            loadBeatmapList(Config.beatmapFolder);
        });

        clearLogsButton.addActionListener(e -> {
            StyledDocument doc = logs.getStyledDocument();
            try {
                doc.remove(0,doc.getLength());
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void setActive(boolean active){
        activeLEDController.setSelected(active);
    }
    void loadBeatmapList(String path){
        File directoryPath = new File(path);
        if(!directoryPath.exists()){
            Item[] item = new Item[2];
            item[0] = new Item();
            item[0].setDisplayedText("-------------> invalid folder <-------------");
            item[1] = new Item();
            item[1].setDisplayedText("  !!!Change beatmap folder in settings!!!");
            songlistList.setListData(item);
            return;
        }

        String[] directoryList = directoryPath.list();
        Item[] items = new Item[directoryList.length + 1];

        items[0] = new Item();
        items[0].setPath(Config.beatmapFolder);
        items[0].setDisplayedText("-------------> Back to Home folder <-------------");

        for (int i = 0; i < directoryList.length; i++) {
            Item item = new Item();
            item.setPath(path + "\\" + directoryList[i]);
            MapLoader loader = new MapLoader();
            if(loader.load(item.getPath())){
                item.setObj(loader.getBeatmap());
            }else{
                item.setDisplayedText("-------------> (Folder)" + directoryList[i] + "<-------------");
            }
            items[i + 1] = item;
        }

        songlistList.setListData(items);
        songlistList.addMouseListener(mouseAdapter);
    }

    public void log(Object o){
        StyledDocument doc = logs.getStyledDocument();
        try {
            String str = "\n";
            if(o == null) str += "null";
            else str += o.toString();
            doc.insertString(doc.getLength(), str, doc.getStyle(""));
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        JScrollBar bar = logScroll.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }

    void fillInfo(Beatmap beatmap){
        StyledDocument doc = infoPane.getStyledDocument();
        try {
            String str = "Song name: " + beatmap.songName + "\n" + "Song author: " + beatmap.songAuthor;
            doc.remove(0, doc.getLength());
            doc.insertString(doc.getLength(), str, doc.getStyle(""));
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        BeatmapDiff[] diffs = beatmap.diffs.toArray(new BeatmapDiff[0]);
        diffList.setListData(diffs);
        diffList.addMouseListener(playDiff);
    }
    Beatmap selected;
    MouseAdapter playDiff = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            JList list = (JList)e.getSource();
            if (e.getClickCount() == 2) {
                int index = list.getSelectedIndex();
                BeatmapPlayer player = BeatmapPlayer.getInstance();
                player.load(selected.path);
                player.play(index);
            }
        }
    };

    MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            JList list = (JList)e.getSource();
            if (e.getClickCount() == 2) {
                Item item = (Item) list.getSelectedValue();
                if(item == null) return;
                if(item.obj==null){
                    loadBeatmapList(item.getPath());
                }else{
                    fillInfo((Beatmap) item.getObj());
                    selected = (Beatmap) item.getObj();
                }
            }
        }
    };
}
