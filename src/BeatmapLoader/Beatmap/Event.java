package BeatmapLoader.Beatmap;

import Lighting.Components.Color;
import Utils.Config;
import com.fasterxml.jackson.databind.JsonNode;

public class Event {
    public long time;
    public Config.value value;
    public double floatValue;
    public int valueAlt;
    public int type;
    public Color color = null;
    public Custom customData = new Custom();

    private Color colorFromInt(int rgb)
    {
        rgb = rgb - 2000000000;
        int red = (rgb >> 16) & 0x0ff;
        int green = (rgb >> 8) & 0x0ff;
        int blue = (rgb) & 0x0ff;
        return new Color(red, green, blue);
    }

    public Event(){

    }
    public void setValue(int value){
        valueAlt = value;
        if(valueAlt >= 2000000000){
            color = colorFromInt(valueAlt);
        }
        switch(valueAlt){
            case 0:
                this.value = Config.value.OFF;
                break;
            case 1, 5, 9:
                this.value = Config.value.ON;
                break;
            case 2, 6, 10:
                this.value = Config.value.FLASH;
                break;
            case 3, 7, 11:
                this.value = Config.value.FADEOUT;
                break;
            case 4, 8, 12:
                this.value = Config.value.TRANSITION;
                break;
            default:
                this.value = Config.value.OTHER;
                break;
        }
    }
    public void parseV2(long time, JsonNode eventNode){
        this.time = time;
        this.valueAlt = eventNode.get("_value").asInt();
        this.type = eventNode.get("_type").asInt();

        if(eventNode.has("_floatValue")){
            floatValue = eventNode.get("_floatValue").floatValue();
        }
        if(eventNode.has("_customData")){
            customData = new Custom(eventNode.get("_customData"));
        }
        if(customData != null) if(customData.color != null) color = customData.color;

        if(this.valueAlt >= 2000000000){
            color = colorFromInt(this.valueAlt);
        }
        setValue(valueAlt);
    }
    public void parseV3(long time, JsonNode eventNode){
        this.time = time;
        this.valueAlt = eventNode.get("i").asInt();
        this.type = eventNode.get("et").asInt();

        if(eventNode.has("f")){
            floatValue = eventNode.get("f").floatValue();
        }
        /*if(eventNode.has("_customData")){
            customData = new Custom(eventNode.get("_customData"));
        }
        if(customData != null) if(customData.color != null) color = customData.color;
        */
        if(this.valueAlt >= 2000000000){
            color = colorFromInt(this.valueAlt);
        }
        setValue(valueAlt);
    }
    @Override
    public String toString() {
        return "(LightEvent) value: " + valueAlt + ", type: " + type + ", time: " + time + ", customData: " + customData;
    }
}
