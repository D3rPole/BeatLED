package Utils;

import Lighting.DeviceLED;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Config {
    public enum value{ OFF, ON, FLASH, FADEOUT, TRANSITION, OTHER }
    public static int fadeoutTime = 500;
    public static int flashTime = 250;
    public static int flashBrightness = 120;
    public static int onBrightness = 70;
    public static String beatmapFolder = "";
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
        Preferences prefs = Preferences.userRoot().node("BeatLED\\Config");

        prefs.put("beatmapFolderPath", beatmapFolder);
        prefs.put("fadeoutTime", String.valueOf(fadeoutTime));
        prefs.put("flashTime", String.valueOf(flashTime));
        prefs.put("onBrightness", String.valueOf(onBrightness));
        prefs.put("flashBrightness", String.valueOf(flashBrightness));

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(devices);
            prefs.put("devices", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        saveJson();
    }

    public static void saveJson(){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.createObjectNode();

        ((ObjectNode)node).put("beatmapFolderPath",beatmapFolder);
        ((ObjectNode)node).put("fadeoutTime", String.valueOf(fadeoutTime));
        ((ObjectNode)node).put("flashTime", String.valueOf(flashTime));
        ((ObjectNode)node).put("onBrightness", String.valueOf(onBrightness));
        ((ObjectNode)node).put("flashBrightness", String.valueOf(flashBrightness));

        try {
            String jsonStr = mapper.writeValueAsString(devices);
            ((ObjectNode)node).put("devices", jsonStr);

            jsonStr = mapper.writeValueAsString(node);

            File file = new File(System.getenv("APPDATA") + "\\beatLED\\config.json");
            if(!file.exists()) {
                File fileFolder = new File(System.getenv("APPDATA") + "\\beatLED");
                if(!fileFolder.exists()) fileFolder.mkdirs();
                Debug.log("Config file missing, creating file.");
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(System.getenv("APPDATA") + "\\beatLED\\config.json");
            byte[] strToBytes = jsonStr.getBytes();
            outputStream.write(strToBytes);

            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load(){
        Debug.log("loading Config");
        Preferences prefs = Preferences.userRoot().node("BeatLED\\Config");

        beatmapFolder = prefs.get("beatmapFolderPath", "");
        fadeoutTime = Integer.parseInt(prefs.get("fadeoutTime", "500"));
        flashTime = Integer.parseInt(prefs.get("flashTime", "250"));
        onBrightness = Integer.parseInt(prefs.get("onBrightness", "70"));
        flashBrightness = Integer.parseInt(prefs.get("flashBrightness", "120"));

        if(!new File(beatmapFolder).exists()){
            chooseFolder();
        }
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

    public static void chooseFolder(){
        JFileChooser explorer = new JFileChooser();
        explorer.setDialogTitle("Choose Beatmap Folder");
        explorer.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int err = explorer.showOpenDialog(null);
        if(err == JFileChooser.APPROVE_OPTION){
            beatmapFolder = explorer.getSelectedFile().getPath();
        }else{
            throw new RuntimeException("Choose a fucking folder retard!");
        }
        save();
    }
}

