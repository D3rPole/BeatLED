package BeatmapLoader.Beatmap;

import java.util.ArrayList;

public class DiffInfo {
    public String diffName;
    public String diffSubName;
    public String diffFileName;

    public ArrayList<String> warnings = new ArrayList<>();

    @Override
    public String toString() {
        return diffName + " " + diffSubName;
    }
}
