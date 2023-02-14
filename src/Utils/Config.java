package Utils;

import java.util.prefs.Preferences;

public class Config {
    public enum value{ OFF, ON, FLASH, FADEOUT, OTHER }
    public static int fadeoutTime = 500;
    public static int flashTime = 250;
    public static int onBrightness = 70;
    public static String beatmapFolder = "C:\\Users\\ikawe\\Desktop\\LED controller\\bs maps\\";

    public static void save(){
        Preferences prefs = Preferences.userNodeForPackage(Config.class);

        prefs.put("beatmapFolderPath", beatmapFolder);
        prefs.put("fadeoutTime", String.valueOf(fadeoutTime));
        prefs.put("flashTime", String.valueOf(flashTime));
        prefs.put("onBrightness", String.valueOf(onBrightness));

    }

    public static void load(){
        Preferences prefs = Preferences.userNodeForPackage(Config.class);

        beatmapFolder = prefs.get("beatmapFolderPath", "");
        fadeoutTime = Integer.parseInt(prefs.get("fadeoutTime", "500"));
        flashTime = Integer.parseInt(prefs.get("flashTime", "250"));
        onBrightness = Integer.parseInt(prefs.get("onBrightness", "70"));
    }

}

