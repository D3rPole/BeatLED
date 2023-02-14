package Lighting.Components;
import Utils.Config;

import java.util.Date;

public class Light {
    Color color = new Color();
    Color currentColor = new Color();
    long timeSinceChange;
    private Config.value currentValue = Config.value.OFF;
    private boolean gradiantHappening;
    private ColorGradient gradient;

    long millis(){ return java.lang.System.currentTimeMillis();}
    public void setColor(Color color){
        this.color = color;
    }
    public Color getCurrentColor(){
        return currentColor;
    }
    public void on(){
        currentValue = Config.value.ON;
        timeSinceChange = millis();
    }
    public void off(){
        currentValue = Config.value.OFF;
        timeSinceChange = millis();
    }
    public void flash(){
        currentValue = Config.value.FLASH;
        timeSinceChange = millis();
    }
    public void fadeout(){
        currentValue = Config.value.FADEOUT;
        timeSinceChange = millis();
    }
    public void setGradient(ColorGradient gradient){
        this.gradient = gradient;
        this.gradient.startedAtTime = new Date().getTime();
        gradiantHappening = true;
    }

    public void update(){
        int brightness;
        if(gradiantHappening){
            applyGradient();
        }
        switch (currentValue) {
            case ON:
                currentColor = color.brightness(Config.onBrightness);
                break;
            case FLASH:
                brightness = (int) ((timeSinceChange + Config.flashTime - millis()) * 100 / Config.flashTime) * (100 - Config.onBrightness) / 100;
                currentColor = color.brightness(Config.onBrightness + Math.max(0, brightness));
                break;
            case FADEOUT:
                brightness = (int) ((timeSinceChange + Config.fadeoutTime - millis()) * 100 / Config.fadeoutTime) * Config.onBrightness / 100;
                currentColor = color.brightness(brightness);
                break;
            default:
                currentColor = new Color();
                break;
        }

    }

    private void applyGradient(){
        if(gradient == null) {
            gradiantHappening = false;
            return;
        }
        if(gradient.perc >= 1){
            gradiantHappening = false;
            color = gradient.endColor;
            return;
        }
        color = gradient.linear();
    }
}
