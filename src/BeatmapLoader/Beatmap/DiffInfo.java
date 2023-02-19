package BeatmapLoader.Beatmap;

public class DiffInfo {
    public String diffName;
    public String diffSubName;
    public String diffFileName;

    @Override
    public String toString() {
        return diffName + " " + diffSubName;
    }
}
