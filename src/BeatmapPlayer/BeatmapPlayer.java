package BeatmapPlayer;

import BeatmapLoader.Beatmap.Diff;
import BeatmapLoader.Beatmap.DiffInfo;
import BeatmapLoader.Beatmap.Event;
import BeatmapLoader.Beatmap.Info;
import BeatmapLoader.Parser;
import Utils.Debug;
import Utils.Utils;

public class BeatmapPlayer {
    Info info;
    boolean initiated;
    String songPath;
    public OggPlayer songPlayer;
    Diff diff;
    int eventMarker = 0;
    public boolean playing = false;
    long time;
    public boolean paused = false;

    public void load(String path){
        if(playing) return;
        playing = true;
        Debug.log("loading Beatmap...");
        if(Parser.isBeatmap(path)){
            initiated = true;
            info = Parser.parseInfo(path);
            songPath = info.path + "\\" + info.songFileName;
            songPlayer = new OggPlayer(songPath);
            Debug.log("Beatmap loaded, player ready");
        }else{
            Debug.log("Something went wrong ):");
        }
    }

    public void play(int difficulty){
        if(!initiated) return;

        songPlayer.play();
        Utils.difficulty = difficulty;
        DiffInfo diffInfo = info.diffs.get(difficulty);
        diff = Parser.parseDiff(diffInfo.diffFileName,info.bpm);
        Utils.ledController.setEnvironment(info.environmentName);
        Utils.ledController.setActive(true);
        eventMarker = 0;
        playing = true;
        Utils.ui.songList.startPlaying();
    }
    public void stop(){
        Utils.ledController.setActive(false);
        Utils.ui.songList.stopPlaying();
        songPlayer.stopAudio();
        playing = false;
    }

    long pauseTime;
    public void pause(){
        paused = !paused;
        if(paused){
            pauseTime = songPlayer.getTime();
            songPlayer.pause();
        }else{
            songPlayer.setTime(pauseTime);
        }
    }
    public void setTime(long time){
        if(!playing) return;
        if(paused){
            pauseTime = time;
            return;
        }
        songPlayer.setTime(time);
        while(time > diff.events[eventMarker].time){
            eventMarker++;
        }
        if(eventMarker == 0) return;
        while(time < diff.events[eventMarker - 1].time){
            eventMarker--;
            if(eventMarker == 0) return;
        }
    }

    public void update(){
        //if(!songPlayer.isPlaying()) return;
        if(!playing) return;
        if(eventMarker >= diff.events.length){
            stop();
        }
        time = songPlayer.getTime();
        while(time > diff.events[eventMarker].time){
            Event event = diff.events[eventMarker];
            Utils.ledController.lightEvent(event);
            eventMarker++;
        }
    }
}
