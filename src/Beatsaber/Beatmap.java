package Beatsaber;

import Utils.Debug;
import org.json.JSONObject;

import java.util.ArrayList;

public class Beatmap {
    public ArrayList<BeatmapDiff> diffs = new ArrayList<BeatmapDiff>();
    public JSONObject info;
    public String songPath;
    public String path;
    public double BPM;
    public String environmentName  = "DefaultEnvironment";

    public Beatmap(String path, JSONObject info){
        Debug.log("Beatmap created");
        this.info = info;
        this.path = path;
        songPath = this.path + "\\" + info.getString("_songFilename");
    }
    public void addDiff(BeatmapDiff diff){
        diffs.add(diff);
        Debug.log("diff added to Beatmap: " + diff);
    }
}
