package Utils;

import Lighting.DeviceLED;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Config {
    public enum value{ OFF, ON, FLASH, FADEOUT, TRANSITION, OTHER }
    public static int fadeoutTime = 500;
    public static int flashTime = 250;
    public static int onBrightness = 70;
    public static String beatmapFolder = "C:\\Users\\ikawe\\Desktop\\LED controller\\bs maps\\";
    public static int defaultPort = 65506;

    public static ArrayList<DeviceLED> devices = new ArrayList<>();
    public static DeviceLED[] deviceArray = new DeviceLED[0];

    public static void updateArray(){
        deviceArray = new DeviceLED[devices.size()];
        for (int i = 0; i < deviceArray.length; i++) {
            deviceArray[i] = devices.get(i);
        }
    }

    public static void save(){
        Debug.log("Saving config");
        Preferences prefs = Preferences.userNodeForPackage(Config.class);

        prefs.put("beatmapFolderPath", beatmapFolder);
        prefs.put("fadeoutTime", String.valueOf(fadeoutTime));
        prefs.put("flashTime", String.valueOf(flashTime));
        prefs.put("onBrightness", String.valueOf(onBrightness));

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(devices);
            prefs.put("devices", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public static void load(){
        Debug.log("loading Config");
        Preferences prefs = Preferences.userNodeForPackage(Config.class);

        beatmapFolder = prefs.get("beatmapFolderPath", "");
        fadeoutTime = Integer.parseInt(prefs.get("fadeoutTime", "500"));
        flashTime = Integer.parseInt(prefs.get("flashTime", "250"));
        onBrightness = Integer.parseInt(prefs.get("onBrightness", "70"));
        String json = prefs.get("devices", "");
        try {
            ObjectMapper mapper = new ObjectMapper();
            devices = mapper.readValue(json, new TypeReference<>() {
            });
            for(DeviceLED device : devices){
                Debug.log("    " + device);
                try {
                    device.init();
                }catch (Exception e){
                    Debug.log(e);
                }
            }

        } catch (JsonProcessingException e) {
            Debug.log(e);
        }
        updateArray();
    }

}

