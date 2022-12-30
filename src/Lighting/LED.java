package Lighting;

public class LED {
    Color color = new Color();
    public void clear(){
        color = new Color();
    }

    public void addColor(Color color){
        this.color = this.color.add(color);
    }
    public void setColor(Color color){
        this.color.r = (byte)color.r;
        this.color.g = (byte)color.g;
        this.color.b = (byte)color.b;
    }
}
