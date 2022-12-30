package Beatsaber.MapLoaderV2;

import Beatsaber.BeatmapDiff;
import Beatsaber.LightEvent;
import Utils.Debug;
import Utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class DiffParser {
    public void parse(BeatmapDiff diff){
        Debug.log("loading file: " + diff.path);
        File file = new File(diff.path);
        Debug.log("creating JSONObject");
        JSONObject json = new JSONObject(Utils.readFile(file));
        Debug.log("parsing V2 diff");
        if(!json.has("_events")) {Debug.log("missing _events"); return;}
        JSONArray events = json.getJSONArray("_events");
        Debug.log("parsing " + events.length() + " events");
        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);
            if(!event.has("_time")){  Debug.log("missing _time"); return;}
            if(!event.has("_type")){  Debug.log("missing _type"); return;}
            if(!event.has("_value")){ Debug.log("missing _value"); return;}
            long time = (long)(event.getDouble("_time") * 60000 / diff.BPM);
            int type = event.getInt("_type");
            int value = event.getInt("_value");

            diff.addEvent(new LightEvent(time,value,type,event));
        }
    }
}
