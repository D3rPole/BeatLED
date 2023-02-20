package Lighting.Components;

import Lighting.DeviceLED;

public class LEDstrip {
    LED[] strip;
    int length;

    LEDstrip(){}
    public LEDstrip(int length){
        this.length = length;
        strip = new LED[this.length];
        for (int i = 0; i < this.length; i++) {
            strip[i] = new LED();
        }
    }

    public void setStrip(LEDstrip strip){
        if(strip.getLength() != this.getLength()) return;
        for (int i = 0; i < this.getLength(); i++) {
            this.strip[i] = strip.strip[i];
        }
    }

    public Color getLEDColor(int index){
        return strip[index].color;
    }

    public void initStrip(){
        strip = new LED[this.length];
        for (int i = 0; i < this.length; i++) {
            strip[i] = new LED();
        }
    }

    public void clear(){
        for (int i = 0; i < length; i++) {
            strip[i].clear();
        }
    }
    public void setColorSingle(int index, Color color){
        strip[Math.floorMod(index,length)].addColor(color);
    }

    public void placePoint(double position,int blur,Color color,int from,int to){
        for (int i = (int) (position - blur); i < (position + blur); i++) {
            if(i >= from && i <= to) {
                double brightness = 100 * Math.pow((1 - Math.abs(i - position) / blur), 2);
                strip[i].addColor(color.brightness((int) brightness));
            }
        }
    }
    public void setColorSolid(Color color){
        for (int i = 0; i < length; i++) {
            strip[i].setColor(color);
        }
    }

    public byte[] toByteArray(){
        byte[] arr = new byte[length*3];
        for (int i = 0; i < length; i++) {
            LED led = strip[i];
            arr[i*3] = (byte)led.color.r;
            arr[i*3 + 1] = (byte)led.color.g;
            arr[i*3 + 2] = (byte)led.color.b;
        }
        return arr;
    }

    public int getLength() {
        return length;
    }
}
