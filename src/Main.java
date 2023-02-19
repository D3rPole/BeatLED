
import BeatmapLoader.Beatmap.Info;
import BeatmapLoader.Parser;
import BeatmapPlayer.BeatmapPlayer;
import Lighting.LEDController;
import UI.MainUI;
import Utils.Utils;
import Utils.Config;
import Utils.Debug;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.formdev.flatlaf.FlatDarculaLaf;

public class Main {

    public static void main(String[] args) {
        Config.load();
        FlatDarculaLaf.setup();
        Utils.ui = new MainUI();
        Utils.beatmapPlayer = new BeatmapPlayer();
        Thread thread = new Thread(() -> {
            Utils.ledController = new LEDController();
            Utils.ledController.update();
        });
        thread.start();

        /*try {
            Info info = Parser.parseInfo("C:\\Users\\ikawe\\Desktop\\LED controller\\bs maps\\Dubstep\\Basement Dwellers");
            Debug.log(Parser.parseDiff(info.diffs.get(0).diffFileName,info.bpm));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
    }
}