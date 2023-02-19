package BeatmapLoader.Beatmap;

import Utils.Debug;
import Utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;

public class Info {
    public String path;

    public String version;
    public String songName;
    public String songSubName;
    public String songAuthorName;
    public String levelAuthorName;
    public double bpm;
    public String songFileName;
    public String coverImageFileName;
    public String environmentName;
    public double songTimeOffset;
    public ArrayList<DiffInfo> diffs;
}

