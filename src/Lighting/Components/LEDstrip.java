package Lighting.Components;

public class LEDstrip {
    LED[] strip;
    int length;

    public LEDstrip(int length){
        this.length = length;
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

    public void placePoint(int index,int blur,Color color){
        strip[Math.floorMod(index,length)].addColor(color);
        for (int i = 1; i < blur; i++) {
            strip[Math.floorMod(index + i,length)].addColor(color.brightness(10*blur / (i*i)));
            strip[Math.floorMod(index - i,length)].addColor(color.brightness(10*blur / (i*i)));
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
            arr[i*3] = (byte)led.color.b;
            arr[i*3 + 1] = (byte)led.color.g;
            arr[i*3 + 2] = (byte)led.color.r;
        }
        return arr;
    }
}
