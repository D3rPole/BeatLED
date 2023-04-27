package Utils;

import Lighting.DeviceLED;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.prefs.Preferences;

import static Utils.Utils.readFile;

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
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("beatmapFolderPath",beatmapFolder);
        node.put("fadeoutTime", fadeoutTime);
        node.put("flashTime", flashTime);
        node.put("onBrightness", onBrightness);
        node.put("flashBrightness", flashBrightness);

        try {
            String jsonStr = mapper.writeValueAsString(devices);
            node.put("devices", jsonStr);

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

    private static Object getSetting(ObjectNode node, String fieldName, Object defaultValue){
        if(node.has(fieldName)){
            Debug.log(node.get(fieldName));
            return node.get(fieldName);
        }
        node.putPOJO(fieldName,defaultValue);
        return defaultValue;
    }

    public static void load(){
        try {
            Debug.log("loading Config");
            File file = new File(System.getenv("APPDATA") + "\\beatLED\\config.json");
            if(!file.exists()){
                chooseFolder();
                return;
            }
            String jsonStr = Utils.readFile(file);
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.readValue(jsonStr, new TypeReference<>() {});

            beatmapFolder = ((TextNode)getSetting(node,"beatmapFolderPath", "")).asText();
            fadeoutTime = ((IntNode)getSetting(node,"fadeoutTime", fadeoutTime)).intValue();
            flashTime = ((IntNode)getSetting(node,"flashTime", flashTime)).intValue();
            onBrightness = ((IntNode)getSetting(node,"onBrightness", onBrightness)).intValue();
            flashBrightness = ((IntNode)getSetting(node,"flashBrightness", flashBrightness)).intValue();


            if(!new File(beatmapFolder).exists()){
                chooseFolder();
            }
            jsonStr = ((TextNode)getSetting(node,"devices","")).asText();
            devices = mapper.readValue(jsonStr, new TypeReference<>() {});

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

