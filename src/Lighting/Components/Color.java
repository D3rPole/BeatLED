package Lighting.Components;

import com.fasterxml.jackson.databind.JsonNode;

public class Color {
    public int r = 0;
    public int b = 0;
    public int g = 0;

    public Color(int r, int g, int b){
        this.r = clamp(0,255,r);
        this.g = clamp(0,255,g);
        this.b = clamp(0,255,b);
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
        int overdrive = clamp(100,200,brightness) - 100;
        brightness = clamp(0,100,brightness);
        int r = this.r * brightness / 100 + 255 * overdrive / 100;
        int g = this.g * brightness / 100 + 255 * overdrive / 100;
        int b = this.b * brightness / 100 + 255 * overdrive / 100;
        return new Color(r,g,b);
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
