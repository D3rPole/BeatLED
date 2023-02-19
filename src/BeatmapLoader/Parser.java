package BeatmapLoader;

import BeatmapLoader.Beatmap.Diff;
import BeatmapLoader.Beatmap.DiffInfo;
import BeatmapLoader.Beatmap.Event;
import BeatmapLoader.Beatmap.Info;
import Beatsaber.LightEvent;
import Utils.Utils;
import Utils.Debug;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;

public class Parser {
    public static Info parseInfo(String path) {
        File infoFile = new File(path + "\\info.dat");
        if(!infoFile.exists()) return null;

        Info info = new Info();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(Utils.readFile(infoFile));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        info.path = path;
        info.version = rootNode.get("_version").asText();
        info.songName = rootNode.get("_songName").asText();
        info.songSubName = rootNode.get("_songSubName").asText();
        info.songAuthorName = rootNode.get("_songAuthorName").asText();
        info.levelAuthorName = rootNode.get("_levelAuthorName").asText();
        info.bpm = rootNode.get("_beatsPerMinute").asDouble();
        info.songFileName = rootNode.get("_songFilename").asText();
        info.coverImageFileName = rootNode.get("_coverImageFilename").asText();
        info.environmentName = rootNode.get("_environmentName").asText();
        info.songTimeOffset = rootNode.get("_songTimeOffset").asDouble();

        JsonNode diffNode = rootNode.get("_difficultyBeatmapSets");

        info.diffs = new ArrayList<>();
        for (int i = 0; i < diffNode.size(); i++) {
            String diffSubName = diffNode.get(i).get("_beatmapCharacteristicName").asText();
            JsonNode diffs = diffNode.get(i).get("_difficultyBeatmaps");
            for (int j = 0; j < diffs.size(); j++) {
                DiffInfo diff = new DiffInfo();
                diff.diffName = diffs.get(j).get("_difficulty").asText();
                diff.diffSubName = diffSubName;
                diff.diffFileName = path + "\\" + diffs.get(j).get("_beatmapFilename").asText();
                info.diffs.add(diff);
            }
        }
        return info;
    }

    public static boolean isBeatmap(String path){
        boolean bool = parseInfo(path) != null;
        Debug.log(path + " is beatmap: " + bool);
        return bool;
    }

    public static Diff parseDiff(String path, double BPM) {
        File diffFile = new File(path);
        if(!diffFile.exists()) {Debug.log(path + " does not exist"); return null;}
        Diff diff = new Diff();
        diff.BPM = BPM;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(Utils.readFile(diffFile));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if(rootNode.has("_version")){
            parseV2(diff, rootNode);
        }else if(rootNode.has("version")){
            parseV3(diff, rootNode);
        }else{
            Debug.log("Incompatible beatmap version");
            return null;
        }

        return diff;
    }

    public static void parseV2(Diff diff, JsonNode rootNode){
        diff.version = rootNode.get("_version").asText();
        JsonNode eventsNode = rootNode.get("_events");
        diff.events = new Event[eventsNode.size()];
        for (int i = 0; i < eventsNode.size(); i++) {
            JsonNode eventNode = eventsNode.get(i);
            Event event = new Event();
            event.parseV2((long) (eventNode.get("_time").asDouble() * 60000 / diff.BPM),eventNode);
            diff.events[i] = event;
        }
    }
    public static void parseV3(Diff diff, JsonNode rootNode){
        diff.version = rootNode.get("version").asText();
        JsonNode eventsNode = rootNode.get("basicBeatmapEvents");

        diff.events = new Event[eventsNode.size()];
        for (int i = 0; i < eventsNode.size(); i++) {
            JsonNode eventNode = eventsNode.get(i);
            Event event = new Event();
            event.parseV3((long) (eventNode.get("b").asDouble() * 60000 / diff.BPM),eventNode);
            diff.events[i] = event;
        }
    }

}
