package UI;

import BeatmapPlayer.BeatmapPlayer;
import Beatsaber.Beatmap;
import Beatsaber.BeatmapDiff;
import Beatsaber.MapLoader;
import Utils.Config;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class MainUI {
    private JPanel panelMain;
    private JList songlistList;
    private JScrollPane songlistPane;
    private JScrollPane diffPane;
    private JTextPane infoPane;
    private JList diffList;
    private JButton play;
    private JButton stop;

    public MainUI(){
        JFrame frame = new JFrame();
        frame.setContentPane(panelMain);
        frame.setTitle("BeatLED");
        frame.setSize(800,600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


        loadBeatmapList(Config.beatmapFolder);
    }

    void loadBeatmapList(String path){
        File directoryPath = new File(path);
        String[] directoryList = directoryPath.list();
        Item[] items = new Item[directoryList.length + 1];

        items[0] = new Item();
        items[0].setPath(Config.beatmapFolder);
        items[0].setDisplayedText("-------------> Back to Home folder <-------------");

        for (int i = 0; i < directoryList.length; i++) {
            Item item = new Item();
            item.setPath(path + directoryList[i]);
            File file = new File(item.getPath());
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
