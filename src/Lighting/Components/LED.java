package Lighting.Components;

public class LED {
    Color color = new Color();
    public void clear(){
        color = new Color();
    }

    public void addColor(Color color){
        this.color = this.color.add(color);
    }
    public void setColor(Color color){
        this.color.r = color.r;
        this.color.g = color.g;
        this.color.b = color.b;
    }
}
