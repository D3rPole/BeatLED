package Lighting.Fixtures;

import Lighting.Components.LEDstrip;
import Utils.Utils;
import Utils.Debug;

import java.util.Date;

public class LaserRotating extends Fixture{
    public int laserSpeed;
    public int lasers = 6;
    public LaserRotating(int numberLamps) {
        super(numberLamps);
    }

    long angle;


    @Override
    public void addToStrip(LEDstrip ledStrip, int from, int to, boolean renderBackwards) {
        if(fixtureType.equals("lasers_left")){
            laserSpeed = Utils.leftLaserSpeed;
        }
        if(fixtureType.equals("lasers_right")){
            laserSpeed = Utils.rightLaserSpeed;
        }

        angle = (java.lang.System.currentTimeMillis() * laserSpeed / 100)%360;
        double radian = angle / 180.0 * Math.PI;
        int size = to - from;
        int mul = 1;
        if(renderBackwards) mul = -1;
        for (int i = 0; i < lasers; i++) {
            double sin = (Math.sin(radian + (double)i / lasers * Math.PI)* mul + 1) / 2 * size;
            ledStrip.placePoint(from + sin,5,lights[i% lights.length].getCurrentColor(),from,to);
            /*ledStrip.setColorSingle((int) (from + sin)-2,lights[i% lights.length].getCurrentColor());
            ledStrip.setColorSingle((int) (from + sin)-1,lights[i% lights.length].getCurrentColor());
            ledStrip.setColorSingle((int) (from + sin),lights[i% lights.length].getCurrentColor());
            ledStrip.setColorSingle((int) (from + sin)+1,lights[i% lights.length].getCurrentColor());
            ledStrip.setColorSingle((int) (from + sin)+2,lights[i% lights.length].getCurrentColor());*/
        }
    }
}
