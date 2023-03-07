package BeatmapLoader;

import BeatmapLoader.Beatmap.*;
import Utils.Debug;
import Utils.Utils;
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

        if(!new File(path + "\\" + info.songFileName).exists()){ //Sometimes audio filename is different in info.dat then in beatmap folder... idk what beatsaber does to fix it
            Debug.log(info.songFileName + " does not exist. Searching audio file");
            info.songFileName = findAudio(path); //just itterates through beatmap folder till it finds .egg or .ogg
        }

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

    static String findAudio(String path){
        File directoryFile = new File(path);
        String[] directoryList = directoryFile.list();
        assert directoryList != null;
        for (int i = 0; i < directoryList.length; i++) {
            String fileName = directoryList[i];
            if(fileName.contains(".egg") || fileName.contains(".ogg")){
                Debug.log("Found " + fileName);
                return fileName;
            }
        }
        return "";
    }

    public static SimpleInfo parseSimpleInfo(String path){
        File infoFile = new File(path + "\\info.dat");
        if(!infoFile.exists()) return null;

        SimpleInfo simpleInfo = new SimpleInfo();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(Utils.readFile(infoFile));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        simpleInfo.path = path;
        simpleInfo.bpm = rootNode.get("_beatsPerMinute").asDouble();
        simpleInfo.songAuthorName = rootNode.get("_songAuthorName").asText();
        simpleInfo.songName = rootNode.get("_songName").asText();
        simpleInfo.songSubName = rootNode.get("_songSubName").asText();
        simpleInfo.levelAuthorName = rootNode.get("_levelAuthorName").asText();
        simpleInfo.coverImageFileName = rootNode.get("_coverImageFilename").asText();

        return simpleInfo;
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
            Debug.log("Opening diff file");
            rootNode = objectMapper.readTree(Utils.readFile(diffFile));
        } catch (JsonProcessingException e) {
            Debug.log(e);
            return null;
        }

        if(rootNode.has("_version")){
            diff.version = rootNode.get("_version").asText();
            parseV2(diff, rootNode);
        }else if(rootNode.has("version")){
            diff.version = rootNode.get("version").asText();
            parseV3(diff, rootNode);
        }else{
            Debug.log("Incompatible beatmap version, but trying v2 cuz some beatmaps are retarted and dont tell you version");
            diff.version = "uknown";
            parseV2(diff, rootNode);
        }

        return diff;
    }

    public static void parseV2(Diff diff, JsonNode rootNode){
        Debug.log("Parsing v2 diff");
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
        Debug.log("Parsing v3 diff");
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
