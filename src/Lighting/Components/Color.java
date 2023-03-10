package Lighting.Components;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;

public class Color {
    public int r = 0;
    public int b = 0;
    public int g = 0;

    public Color(int r, int g, int b){
        this.r = clamp(0,255,r);
        this.g = clamp(0,255,g);
        this.b = clamp(0,255,b);
    }
    public Color(JSONArray color){
        double a = 1;
        if(color.length() > 3) a = color.getDouble(3);
        r = (int)(color.getFloat(0) * a * 255);
        g = (int)(color.getFloat(1) * a * 255);
        b = (int)(color.getFloat(2) * a * 255);
    }

    public Color(JsonNode color){
        double a = 1;
        if(color.size() > 3) a = color.get(3).asDouble();
        r = (int)(color.get(0).asDouble() * a * 255);
        g = (int)(color.get(1).asDouble() * a * 255);
        b = (int)(color.get(2).asDouble() * a * 255);
    }
    public Color(){}

    public Color add(Color color){
        return new Color(clamp(0,255,r + color.r),clamp(0,255,g + color.g),clamp(0,255,b + color.b));
    }

    public Color brightness(int brightness){
        brightness = clamp(0,100,brightness);
        return new Color(r * brightness / 100,g * brightness / 100,b * brightness / 100);
    }

    int clamp(int min,int max,int value){
        return Math.max(min,Math.min(max,value));
    }

    public boolean equals(Color c) {
        return c.r == r && c.g == g && c.b == b;
    }

    @Override
    public String toString() {
        return "(Color) r: " + r + ", g: " + g + ", b: " + b;
    }
}
