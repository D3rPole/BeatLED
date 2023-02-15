package UI;

import Beatsaber.Beatmap;
import Beatsaber.BeatmapDiff;
import Beatsaber.LightEvent;
import Beatsaber.MapLoader;
import Utils.Config;
import Utils.Utils;
import Utils.Debug;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class MainUI extends Component {
    private JPanel panelMain;
    private JList<Item> songlistList;
    private JScrollPane songlistPane;
    private JScrollPane diffPane;
    private JTextPane infoPane;
    private JList<BeatmapDiff> diffList;
    private JButton playButton;
    private JButton stopButton;
    private JPanel ledControllerPanel;
    private JCheckBox activeLEDControllerCheckbox;
    private JPanel songListPanel;
    private JPanel infoPanel;
    private JButton settingsButton;
    private JButton reloadListButton;
    private JButton openLogsButton;
    private JCheckBox manualControlCheckBox;
    private JComboBox<Item> typeComboBox;
    private JComboBox actionComboBox;
    private JSpinner redSpinner;
    private JSpinner blueSpinner;
    private JSpinner greenSpinner;
    private JButton sendCommandButton;
    private JButton hardwareSetupButton;
    private JPanel manualControlsPanel;
    private Logs logWindow;

    public MainUI(){
        JFrame frame = new JFrame();
        frame.setContentPane(panelMain);
        frame.setTitle("BeatLED");
        frame.setSize(1200,600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Utils.setEnabledRecursive(manualControlsPanel,false);
        typeComboBox.addItem(new Item("Back lights",0));
        typeComboBox.addItem(new Item("Ring lights",1));
        typeComboBox.addItem(new Item("Left laser",2));
        typeComboBox.addItem(new Item("Right laser",3));
        typeComboBox.addItem(new Item("Center lights",4));

        typeComboBox.addItem(new Item("Ring rotation",8));
        typeComboBox.addItem(new Item("Ring zoom",9));

        typeComboBox.addItem(new Item("Left laser speed",12));
        typeComboBox.addItem(new Item("Right laser speed",13));

        typeComboBox.addItem(new Item("Boost lights",5));

        actionComboBox.addItem(new Item("0",0));
        actionComboBox.addItem(new Item("1",1));
        actionComboBox.addItem(new Item("2",2));
        actionComboBox.addItem(new Item("3",3));
        actionComboBox.addItem(new Item("4",4));
        actionComboBox.addItem(new Item("5",5));
        actionComboBox.addItem(new Item("6",6));
        actionComboBox.addItem(new Item("7",7));
        actionComboBox.addItem(new Item("8",8));
        actionComboBox.addItem(new Item("9",9));


        activeLEDControllerCheckbox.addActionListener(e -> {
            Utils.ledController.setActive(activeLEDControllerCheckbox.isSelected());
            if(!activeLEDControllerCheckbox.isSelected() && manualControlCheckBox.isSelected()){
                manualControlCheckBox.doClick();
            }
        });

        stopButton.addActionListener(e -> Utils.beatmapPlayer.stop());

        settingsButton.addActionListener(e -> new Settings());

        openLogsButton.addActionListener(e -> logWindow = new Logs());

        loadBeatmapList(Config.beatmapFolder);

        reloadListButton.addActionListener(e -> {
            loadBeatmapList(Config.beatmapFolder);
        });

        manualControlCheckBox.addActionListener(e -> {
            Utils.setEnabledRecursive(manualControlsPanel,manualControlCheckBox.isSelected());
            if(manualControlCheckBox.isSelected() && !activeLEDControllerCheckbox.isSelected()){
                activeLEDControllerCheckbox.doClick();
            }
        });

        sendCommandButton.addActionListener(e -> {
            LightEvent event = new LightEvent(0, actionComboBox.getSelectedIndex(),((int)((Item)typeComboBox.getSelectedItem()).getObj()), new JSONObject());
            Utils.ledController.lightEvent(event);
            Debug.log(event);
        });

        hardwareSetupButton.addActionListener(e -> new HardwareSetup());
    }

    public void setActive(boolean active){
        activeLEDControllerCheckbox.setSelected(active);
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
        assert directoryList != null;
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
        if(logWindow == null) return;
        if(!logWindow.isOpen()) return;

        logWindow.log(o);
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
                Utils.beatmapPlayer.load(selected.path);
                Utils.beatmapPlayer.play(index);
            }
        }
    };

    MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            JList list = (JList)e.getSource();
            Item item = (Item) list.getSelectedValue();
            if(item == null) return;
            if(item.obj==null && e.getClickCount() == 2){
                loadBeatmapList(item.getPath());
            }
            if(item.obj!=null){
                fillInfo((Beatmap) item.getObj());
                selected = (Beatmap) item.getObj();
            }
        }
    };
}
