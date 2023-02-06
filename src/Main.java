import BeatmapPlayer.BeatmapPlayer;

public class Main {

    public static void main(String[] args) {
        //App ui = new App();

        String path = "C:\\Users\\ikawe\\Desktop\\LED controller\\bs maps\\lord & master";

        BeatmapPlayer player = BeatmapPlayer.getInstance();
        player.load(path);
        player.play(0);
    }
}