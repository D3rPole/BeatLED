package BeatmapLoader.Beatmap;

import Lighting.Components.Color;
import Lighting.Components.ColorGradient;
import Utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;

public class Custom {
    public boolean lockPosition;
    public int direction = -1;
    public int preciseSpeed = -1;
    public int[] lightIDs = new int[0];
    public Color color = null;
    public ColorGradient gradient = null;

    Custom(){}
    Custom(JsonNode customData){
        if(customData.has("_color")){
            JsonNode colorArray = customData.get("_color");
            int r = (int)(colorArray.get(0).asDouble()*255);
            int g = (int)(colorArray.get(1).asDouble()*255);
            int b = (int)(colorArray.get(2).asDouble()*255);

            color = new Color(r,g,b);
        }
        if(customData.has("_lightID")){
            JsonNode id = customData.get("_lightID");
            if(id.isInt()){
                lightIDs = new int[]{customData.get("_lightID").asInt()};
            }else{
                lightIDs = new int[((JsonNode) id).size()];
                for (int i = 0; i < ((JsonNode) id).size(); i++) {
                    lightIDs[i] = ((JsonNode) id).get(i).asInt();
                }
            }
        }
        if(customData.has("_lightGradient")){
            JsonNode gradientJSON = customData.get("_lightGradient");
            long time = (long)(gradientJSON.get("_duration").asDouble() * 60000 / Utils.BPM);
            Color startColor = new Color(gradientJSON.get("_startColor"));
            Color endColor = new Color(gradientJSON.get("_endColor"));
            String easing = gradientJSON.get("_easing").asText();
            this.gradient = new ColorGradient(startColor,endColor,time,easing);
        }
        if(customData.has("_lockPosition")) lockPosition = customData.get("_lockPosition").asBoolean();
        if(customData.has("_direction")) direction = customData.get("_direction").asInt();
        if(customData.has("_preciseSpeed")) preciseSpeed = customData.get("_preciseSpeed").asInt();
    }

    @Override
    public String toString() {
        String str = "color: " + color + ", lightIDs : {";
        for (int i = 0; i < lightIDs.length; i++) {
            str = str + lightIDs[i];
            if(i < lightIDs.length - 1) str += ", ";
        }
        str += "}";
        return str;
    }
}
