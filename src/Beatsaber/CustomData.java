package Beatsaber;

import Lighting.Components.Color;
import Lighting.Components.ColorGradient;
import Utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

public class CustomData {
    public boolean lockPosition;
    public int direction = -1;
    public int preciseSpeed = -1;
    public int[] lightIDs = new int[0];
    public Color color = null;
    public ColorGradient gradient = null;

    CustomData(){}
    CustomData(JSONObject data){
        if(data.has("_color")){
            JSONArray colorArray = data.getJSONArray("_color");
            int r = (int)(colorArray.getFloat(0)*255);
            int g = (int)(colorArray.getFloat(1)*255);
            int b = (int)(colorArray.getFloat(2)*255);

            color = new Color(r,g,b);
        }
        if(data.has("_lightID")){
            Object id = data.get("_lightID");
            if(id instanceof JSONArray){
                lightIDs = new int[((JSONArray) id).length()];
                for (int i = 0; i < ((JSONArray) id).length(); i++) {
                    lightIDs[i] = ((JSONArray) id).getInt(i);
                }
            }else{
                lightIDs = new int[]{data.getInt("_lightID")};
            }
        }
        if(data.has("_lightGradient")){
            JSONObject gradientJSON = data.getJSONObject("_lightGradient");
            long time = (long)(gradientJSON.getDouble("_duration") * 60000 / Utils.BPM);
            Color startColor = new Color(gradientJSON.getJSONArray("_startColor"));
            Color endColor = new Color(gradientJSON.getJSONArray("_endColor"));
            String easing = gradientJSON.getString("_easing");
            this.gradient = new ColorGradient(startColor,endColor,time,easing);
        }
        if(data.has("_lockPosition")) lockPosition = data.getBoolean("_lockPosition");
        if(data.has("_direction")) direction = data.getInt("_direction");
        if(data.has("_preciseSpeed")) preciseSpeed = data.getInt("_preciseSpeed");
    }

    @Override
    public String toString() {
        String str = "color: " + color + ", lightIDs : {";
        for (int i = 0; i < lightIDs.length; i++) {
            str += lightIDs[i];
            if(i < lightIDs.length) str += ", ";
        }
        str += "}";
        return str;
    }
}
