package BeatmapPlayer;
import BeatmapLoader.Beatmap.Diff;
import BeatmapLoader.Beatmap.DiffInfo;
import BeatmapLoader.Beatmap.Event;
import BeatmapLoader.Beatmap.Info;
import BeatmapLoader.Parser;
import Utils.Debug;
import Utils.Utils;

import java.util.*;

public class BeatmapPlayer {
    Info info;
    boolean initiated;
    String songPath;
    public OggPlayer songPlayer;

    long timeA;

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

    public void stop(){
        if(songPlayer == null) return;
        playing = false;
        songPlayer.stopAudio();
        Utils.ledController.setActive(false);
    }

    public void play(int difficulty){
        if(initiated){
            Debug.log("playing...");
            stop();
            playing = true;
            songPlayer = new OggPlayer(songPath);
            Utils.difficulty = difficulty;
            DiffInfo diffInfo = info.diffs.get(difficulty);
            Diff diff = Parser.parseDiff(diffInfo.diffFileName,info.bpm);
            songPlayer.play();
            Utils.ledController.setEnvironment(info.environmentName);
            Utils.ledController.setActive(true);
            playEvents(diff,0.0);
            Debug.log("Playing Audioclip");
        }else{
            Debug.log("Beatmap Player is not ready");
        }
    }

    public void playEvents(Diff diff,double startTime){
        Date date = new Date();
        timeA = date.getTime();
        Debug.log("Playing LightEvents");
        ArrayList<Event> events = new ArrayList<>(List.of(diff.events));
        Event event;
        if(events.size() == 0) {Debug.log("no events"); return;}
        do{
            event = events.get(0);
            events.remove(0);
        }while(event.time < startTime);
        nextEvent(events);
    }

    public TimerTask nextEventTask;

    public void nextEvent(ArrayList<Event> events){
        if(!this.songPlayer.isPlaying()){
            return;
        }
        if(events.size() > 0){
            Event event = events.get(0);
            events.remove(0);
            Timer timer = new Timer();
            long songTime = songPlayer.getTime();
            long time = event.time - songTime;

            nextEventTask = new TimerTask() {
                public void run() {
                    Utils.ledController.lightEvent(event);
                    nextEvent(events);
                    Thread.currentThread().interrupt();
                }
            };

            timer.schedule(nextEventTask,  Math.max(0, time));
        }else{
            Debug.log("no events");
            Utils.ledController.setActive(false);
            playing = false;
        }
    }
}
