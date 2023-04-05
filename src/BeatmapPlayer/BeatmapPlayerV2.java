package BeatmapPlayer;

import BeatmapLoader.Beatmap.Diff;
import BeatmapLoader.Beatmap.Info;
import BeatmapLoader.Parser;
import Utils.Debug;

public class BeatmapPlayerV2 {
    Info info;
    boolean initiated;
    String songPath;
    public OggPlayer songPlayer;
    Diff diff;
    int eventMarker = 0;

    public boolean playing = false;

    public void load(String path){
        if(playing) return;
        playing = true;
        Debug.log("loading Beatmap...");
        if(Parser.isBeatmap(path)){
            initiated = true;
            info = Parser.parseInfo(path);
            songPath = info.path + "\\" + info.songFileName;
            Debug.log("Beatmap loaded, player ready");
        }else{
            Debug.log("Something went wrong ):");
        }
    }

    public void play(int difficulty){
        eventMarker = 0;
    }
    public void setTime(){

    }

    public void update(){

    }
}
