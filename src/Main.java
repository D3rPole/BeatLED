
import BeatmapPlayer.BeatmapPlayer;
import Lighting.Components.LEDController;
import UI.MainUI;
import Utils.Utils;
import Utils.Config;
import com.formdev.flatlaf.FlatDarculaLaf;

public class Main {

    public static void main(String[] args) {
        Config.load();
        FlatDarculaLaf.setup();
        Utils.ui = new MainUI();
        Utils.beatmapPlayer = new BeatmapPlayer();
        Utils.ledController = new LEDController();
    }
}