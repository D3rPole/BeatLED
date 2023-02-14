package Lighting.Fixtures;

import Lighting.Components.Color;
import Lighting.Components.ColorGradient;
import Lighting.Components.LEDstrip;
import Lighting.Components.Light;

public abstract class Fixture {
    public Light[] lights;

    public String fixtureType = "";
    public boolean overrideColor = false;

    public Fixture(int numberLamps){
        lights = new Light[numberLamps];
        for (int i = 0; i < numberLamps; i++) {
            lights[i] = new Light();
        }
    }

    public void update(){
        for (int i = 0; i < lights.length; i++) {
            lights[i].update();
        }
    }

    public void on(){
        for (int i = 0; i < lights.length; i++) {
            lights[i].on();
        }
    }
    public void off(){
        for (int i = 0; i < lights.length; i++) {
            lights[i].off();
        }
    }
    public void flash(){
        for (int i = 0; i < lights.length; i++) {
            lights[i].flash();
        }
    }
    public void fadeout(){
        for (int i = 0; i < lights.length; i++) {
            lights[i].fadeout();
        }
    }
    public void setColor(Color color){
        for (int i = 0; i < lights.length; i++) {
            lights[i].setColor(color);
        }
    }
    public void setGradient(ColorGradient gradient){
        for (int i = 0; i < lights.length; i++) {
            lights[i].setGradient(gradient);
        }
    }

    public void on(int[] lampID){
        for (int i = 0; i < lampID.length; i++) {
            int id = lampID[i];
            if(id < lights.length) {
                lights[id].on();
            }
        }
    }
    public void off(int[] lampID){
        for (int i = 0; i < lampID.length; i++) {
            int id = lampID[i];
            if(id < lights.length) {
                lights[id].off();
            }
        }
    }
    public void flash(int[] lampID){
        for (int i = 0; i < lampID.length; i++) {
            int id = lampID[i];
            if(id < lights.length) {
                lights[id].flash();
            }
        }
    }
    public void fadeout(int[] lampID){
        for (int i = 0; i < lampID.length; i++) {
            int id = lampID[i];
            if(id < lights.length) {
                lights[id].fadeout();
            }
        }
    }
    public void setColor(int[] lampID, Color color){
        for (int i = 0; i < lampID.length; i++) {
            int id = lampID[i];
            if(id < lights.length) {
                lights[id].setColor(color);
            }
        }
    }
    public void setGradient(int[] lampID,ColorGradient gradient){
        for (int i = 0; i < lampID.length; i++) {
            int id = lampID[i];
            if(id < lights.length) {
                lights[id].setGradient(gradient);
            }
        }
    }

    public void addToStrip(LEDstrip ledStrip, int fromIndex, int toIndex){
        addToStrip(ledStrip,fromIndex,toIndex,false);
    }
    public abstract void addToStrip(LEDstrip ledStrip, int fromIndex, int toIndex, boolean reversed);
}
