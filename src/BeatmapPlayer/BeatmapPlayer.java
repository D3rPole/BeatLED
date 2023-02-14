package BeatmapPlayer;
import Beatsaber.Beatmap;
import Beatsaber.BeatmapDiff;
import Beatsaber.LightEvent;
import Beatsaber.MapLoader;
import Beatsaber.MapLoaderV2.DiffParser;
import Utils.Debug;
import Utils.Utils;

import java.util.*;

public class BeatmapPlayer {
    Beatmap beatmap;
    boolean initiated;
    String songPath;
    public OggPlayer songPlayer;

    long timeA;

    public void load(String path){
        Debug.log("loading Beatmap...");
        MapLoader mapLoader = new MapLoader();
        if(mapLoader.load(path)){
            initiated = true;
            beatmap = mapLoader.getBeatmap();
            songPath = beatmap.songPath;
            Debug.log("Beatmap loaded, player ready");
        }else{
            Debug.log("Something went wrong ):");
        }
    }

    public void stop(){
        if(songPlayer == null) return;
        songPlayer.stop();
        Utils.ledController.setActive(false);
    }

    public void play(int difficulty){
        if(initiated){
            stop();
            songPlayer = new OggPlayer(songPath);
            Utils.difficulty = difficulty;
            BeatmapDiff diff = beatmap.diffs.get(difficulty);
            DiffParser parser = new DiffParser();
            parser.parse(diff);
            Utils.colorA = diff.colorA;
            Utils.colorB = diff.colorB;
            songPlayer.start();
            Utils.ledController.setEnvironment(beatmap.environmentName);
            Utils.ledController.setActive(true);
            playEvents(diff,0.0);
            Debug.log("Playing Audioclip");
        }else{
            Debug.log("Beatmap Player is not ready");
        }
    }

    public void playEvents(BeatmapDiff diff,double startTime){
        Date date = new Date();
        timeA = date.getTime();
        Debug.log("Playing LightEvents");
        ArrayList<LightEvent> events = new ArrayList<>(diff.events);
        LightEvent event;
        if(events.size() == 0) {Debug.log("no events"); return;}
        do{
            event = events.get(0);
            events.remove(0);
        }while(event.time < startTime);
        nextEvent(events);
    }

    public TimerTask nextEventTask;

    public void nextEvent(ArrayList<LightEvent> events){
        if(!this.songPlayer.isAlive()){
            return;
        }
        if(events.size() > 0){
            LightEvent event = events.get(0);
            events.remove(0);
            Timer timer = new Timer();
            long songTime;
            if(songPlayer.line != null) {
                songTime = songPlayer.line.getMicrosecondPosition() / 1000;
            }else{
                Date date = new Date();
                songTime = (date.getTime() - timeA);
            }
            long time = event.time - songTime;

            nextEventTask = new TimerTask() {
                public void run() {
                    Utils.ledController.lightEvent(event);
                    nextEvent(events);
                }
            };

            timer.schedule(nextEventTask,  Math.max(0, time));
        }else Debug.log("no events");
    }
}
