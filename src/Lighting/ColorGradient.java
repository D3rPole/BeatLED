package Lighting;
import java.util.Date;

public class ColorGradient {
    Color startColor;
    Color endColor;
    long time;
    String easing;

    public double perc;
    public long startedAtTime;

    public ColorGradient(Color startColor, Color endColor, long time, String easing){
        this.startColor = startColor;
        this.endColor = endColor;
        this.time = time;
        this.easing = easing;
    }

    Color linear(){
        long currentTime = new Date().getTime();
        perc = (double)(currentTime - startedAtTime) / time;
        double _perc = Math.min(1,perc);
        int r = (int)(startColor.r * (1 - _perc) + endColor.r * _perc);
        int g = (int)(startColor.g * (1 - _perc) + endColor.r * _perc);
        int b = (int)(startColor.b * (1 - _perc) + endColor.r * _perc);
        return new Color(r,g,b);
    }

    @Override
    public String toString() {
        return "startColor: " + startColor + ", endColor: " + endColor + ", time: " + time;
    }
}
