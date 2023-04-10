
import BeatmapPlayer.BeatmapPlayerV2;
import Lighting.LEDController;
import Utils.Utils;
import Utils.Config;
import com.formdev.flatlaf.FlatDarculaLaf;

public class Main {

    public static void main(String[] args) {
        Config.load();
        FlatDarculaLaf.setup();
        Utils.ui = new UI.Main();
        Utils.beatmapPlayer = new BeatmapPlayerV2();
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