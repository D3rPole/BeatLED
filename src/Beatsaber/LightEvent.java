package Beatsaber;

import Utils.Config;
import org.json.JSONObject;
import Lighting.Components.Color;

public class LightEvent {
    public long time;
    public Config.value value;
    public int valueAlt;
    public int type;
    public Color color = null;
    public JSONObject json;
    public CustomData customData = new CustomData();

    private Color colorFromInt(int rgb)
    {
        rgb = rgb - 2000000000;
        int red = (rgb >> 16) & 0x0ff;
        int green = (rgb >> 8) & 0x0ff;
        int blue = (rgb) & 0x0ff;
        return new Color(red, green, blue);
    }

    public LightEvent(long time, int value, int type, JSONObject json){
        this.time = time;
        this.valueAlt = value;
        this.type = type;
        this.json = json;
        if(json.has("_customData")){
            customData = new CustomData(json.getJSONObject("_customData"));
        }
        if(customData != null) if(customData.color != null) color = customData.color;

        if(this.valueAlt >= 2000000000){
            color = colorFromInt(this.valueAlt);
        }

        switch(this.valueAlt){
            case 0:
                this.value = Config.value.OFF;
                break;
            case 1, 5:
                this.value = Config.value.ON;
                break;
            case 2, 6:
                this.value = Config.value.FLASH;
                break;
            case 3, 7:
                this.value = Config.value.FADEOUT;
                break;
            default:
                this.value = Config.value.OTHER;
                break;
        }
    }

    @Override
    public String toString() {
        return "(LightEvent) value: " + valueAlt + ", type: " + type + ", time: " + time + ", customData: " + customData;
    }
}
