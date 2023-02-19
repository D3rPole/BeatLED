package BeatmapLoader.Beatmap;

import Beatsaber.LightEvent;

public class Diff {
    public double BPM;
    public String version;
    public Event[] events = new Event[0];

    @Override
    public String toString() {
        return "version " + version + " difficulty | " + events.length + " events";
    }
}
