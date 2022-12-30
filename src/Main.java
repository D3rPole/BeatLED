import BeatmapPlayer.BeatmapPlayer;

public class Main {

    public static void main(String[] args) {
        String path = "C:\\Users\\ikawe\\Desktop\\LED controller\\bs maps\\Centipede";

        BeatmapPlayer player = BeatmapPlayer.getInstance();
        player.load(path);
        player.play(0);
    }
}