
import BeatmapPlayer.BeatmapPlayer;
import Lighting.LEDController;
import Utils.Utils;
import Utils.Config;
import Utils.Debug;
import com.formdev.flatlaf.FlatDarculaLaf;

public class Main {

    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        Config.load();
        Utils.ui = new UI.Main();
        Utils.beatmapPlayer = new BeatmapPlayer();
        Thread thread = new Thread(() -> {
            Utils.ledController = new LEDController();
            Utils.ledController.update();
        });
        thread.start();
    }
}