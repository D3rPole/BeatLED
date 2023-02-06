package Beatsaber;

import Utils.Debug;
import Lighting.Color;

import java.util.ArrayList;

public class BeatmapDiff {
    String diffName;
    public double BPM;
    public String path;
    public ArrayList<LightEvent> events = new ArrayList<>();
    public Color colorA = new Color(255,0,0);
    public Color colorB = new Color(0,0,255);

    public BeatmapDiff(String diffName, double BPM){
        this.diffName = diffName;
        this.BPM = BPM;
        Debug.log("-- Creating diff: " + diffName + " --");
    }

    public void addEvent(LightEvent event){
        events.add(event);
    }

    @Override
    public String toString() {
        return diffName;
    }
}
