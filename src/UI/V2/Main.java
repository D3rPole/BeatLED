package UI.V2;

import javax.swing.*;
import java.awt.*;

public class Main {

    public Controller controller;
    public HardwareManager hardwareManager;
    public Settings settings;
    public SongList songList;
    public Logs logs;

    private JTabbedPane tabbedPane;
    private JPanel mainPanel;

    public Main(){
        JFrame frame = new JFrame();
        frame.setContentPane(mainPanel);
        frame.setTitle("BeatLED");
        frame.setSize(1000,600);
        frame.setMinimumSize(new Dimension(800,600));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        logs = new Logs();
        songList = new SongList();
        controller = new Controller();
        hardwareManager = new HardwareManager();
        settings = new Settings();

        tabbedPane.addTab("Song list", songList.panel);
        tabbedPane.addTab("LED controller", controller.panel);
        tabbedPane.addTab("Hardware manager", hardwareManager.panel);
        tabbedPane.addTab("Settings", settings.panel);
        tabbedPane.addTab("Logs", logs.panel);
    }
}
