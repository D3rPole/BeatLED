package Beatsaber;

import java.io.File;

import Utils.*;
import org.json.JSONObject;

public class MapLoader {
    String path;
    Beatmap beatmap;

    public boolean load(String path) {
        Debug.log("Loading map: " + path);
        if (path.equals("")) return false;
        this.path = path;
        File file = new File(path);
        if (!file.exists()) return false;
        //Load info.dat and verify
        File info = new File(path + "\\info.dat");
        if (!info.exists()) {
            info = new File(path + "\\Info.dat");
        }
        if (!info.exists()) return false;
        Debug.log("info.dat exists, validating...");
        JSONObject infoJson = new JSONObject(Utils.readFile(info));
        String version;
        if(infoJson.has("_version")){
            version = infoJson.getString("_version");
        }else if(infoJson.has("version")){
            version = infoJson.getString("version");
        }else{
            return false;
        }
        if(version.startsWith("2.")){
            Beatsaber.MapLoaderV2.MapLoader mapLoader = new Beatsaber.MapLoaderV2.MapLoader();
            Debug.log("Loading V2 Beatmap");
            if(!mapLoader.load(this.path)) return false;
            beatmap = mapLoader.getBeatmap();
        } else if (version.startsWith("3.")) {
            Beatsaber.MapLoaderV3.MapLoader mapLoader = new Beatsaber.MapLoaderV3.MapLoader();
            Debug.log("Loading V3 Beatmap");
            if(!mapLoader.load(this.path)) return false;
            beatmap = mapLoader.getBeatmap();
        }else{
            Debug.log("version incompatible");
        }
        return true;
    }

    public Beatmap getBeatmap() {
        return beatmap;
    }
}
