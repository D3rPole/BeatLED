package UI.V2;

import javax.swing.*;

public class Main {

    private Controller controller;
    private HardwareManager hardwareManager;
    private Settings settings;
    private SongList songList;

    private JTabbedPane tabbedPane;
    private JPanel mainPanel;

    public Main(){
        JFrame frame = new JFrame();
        frame.setContentPane(mainPanel);
        frame.setTitle("BeatLED");
        frame.setSize(1200,600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        songList = new SongList();
        controller = new Controller();
        hardwareManager = new HardwareManager();
        settings = new Settings();

        tabbedPane.addTab("Song list", songList.panel);
        tabbedPane.addTab("LED controller", controller.panel);
        tabbedPane.addTab("Hardware manager", hardwareManager.panel);
        tabbedPane.addTab("Settings", settings.panel);
    }

    public void updateTPS(long TPS, long tickTime, long Target){

    }
}
