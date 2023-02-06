package Beatsaber.MapLoaderV2;

import Beatsaber.Beatmap;
import Beatsaber.BeatmapDiff;
import Utils.*;
import Lighting.Color;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MapLoader {
    String path;
    Beatmap beatmap;

    public boolean load(String path) {
        Debug.log("Loading map: " + path);
        if(path.equals("")) return false;
        this.path = path;
        File file = new File(path);
        if(!file.exists()) return false;
        //Load info.dat and verify
        File info = new File(path + "\\info.dat");
        if(!info.exists()){
            info = new File(path + "\\Info.dat");
        }
        if(!info.exists()) return false;
        Debug.log("info.dat exists, validating...");
        JSONObject infoJson = new JSONObject(readFile(info));
        beatmap = new Beatmap(path,infoJson);
        if(!verifyInfo(infoJson)) return false;
        Debug.log("info.dat validated");
        Debug.log("map loaded!");
        return true;
    }

    boolean verifyInfo(JSONObject info){
        if(!info.has("_songFilename")) return false;
        if(info.has("_environmentName")) beatmap.environmentName = info.getString("_environmentName");

        Debug.log("_songFilename exists");
        if(!info.has("_beatsPerMinute")) return false;
        beatmap.BPM = info.getDouble("_beatsPerMinute");
        beatmap.songName = info.getString("_songName");
        beatmap.songAuthor = info.getString("_songAuthorName");
        Utils.BPM = beatmap.BPM;
        Debug.log("_beatsPerMinute: " + beatmap.BPM);
        if(!info.has("_difficultyBeatmapSets")) return false;
        JSONArray diffs = info.getJSONArray("_difficultyBeatmapSets");
        Debug.log("checking _difficultyBeatmapSets");
        for (int i = 0; i < diffs.length(); i++) {
            JSONObject _diff = diffs.getJSONObject(i);
            if(!_diff.has("_beatmapCharacteristicName")) return false;
            JSONArray __diff = _diff.getJSONArray("_difficultyBeatmaps");
            for (int j = 0; j < __diff.length(); j++) {
                JSONObject diff = __diff.getJSONObject(j);
                if (!verifyDiffInfo(diff)) return false;
            }
        }
        Debug.log("_difficultyBeatmapSets valid");
        if(!(new File(path + "\\" + info.get("_songFilename"))).exists()){Debug.log(info.get("_songFilename") + " doesn't exist"); return false;}
        Debug.log(info.get("_songFilename") + " exists");
        return true;
    }
    boolean verifyDiffInfo(JSONObject diffInfo){
        Debug.log("verifying diff info");
        if(!diffInfo.has("_beatmapFilename")) return false;
        File diffFile = new File(path + "\\" + diffInfo.get("_beatmapFilename"));
        if(!diffFile.exists()) return false;
        Debug.log("verified " + diffInfo.get("_beatmapFilename"));
        BeatmapDiff diff = new BeatmapDiff(diffInfo.getString("_beatmapFilename"),beatmap.BPM);
        diff.path = path + "\\" + diffInfo.get("_beatmapFilename");
        if(diffInfo.has("_customData")){
            JSONObject customData = diffInfo.getJSONObject("_customData");
            if(customData.has("_envColorLeft")){
                JSONObject color = customData.getJSONObject("_envColorLeft");
                diff.colorA = new Color((int)(color.getFloat("r")*255),(int)(color.getFloat("g")*255),(int)(color.getFloat("b")*255));
                Debug.log("colorA set to " + diff.colorA);
            }
            if(customData.has("_envColorRight")){
                JSONObject color = customData.getJSONObject("_envColorRight");
                diff.colorB = new Color((int)(color.getFloat("r")*255),(int)(color.getFloat("g")*255),(int)(color.getFloat("b")*255));
                Debug.log("colorB set to " + diff.colorB);
            }
        }
        Debug.log("adding BeatmapDiff to Beatmap");
        beatmap.addDiff(diff);
        return true;
    }
    String readFile(File file) {
        try {
            Scanner scanner = new Scanner(file);
            String str = "";
            while (scanner.hasNext()){
                str += scanner.next();
            }
            return str;
        }catch(FileNotFoundException e){
            Debug.log(e);
        }
        return "";
    }

    public Beatmap getBeatmap() {
        return beatmap;
    }
}
