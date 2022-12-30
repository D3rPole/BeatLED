package Lighting.Fixtures;

import Lighting.LEDstrip;
import Utils.Utils;

import java.util.Date;

public class LaserRotating extends Fixture{
    public int laserSpeed;
    public int lasers = 6;
    public LaserRotating(int numberLamps) {
        super(numberLamps);
    }


    @Override
    public void addToStrip(LEDstrip ledStrip, int position, int size, boolean renderBackwards) {
        if(fixtureType.equals("lasers_left")){
            laserSpeed = Utils.leftLaserSpeed;
        }
        if(fixtureType.equals("lasers_right")){
            laserSpeed = Utils.rightLaserSpeed;
        }

        long angle = (new Date().getTime() * laserSpeed / 10)%360;
        double radian = angle / 180.0 * Math.PI;
        int mul = 1;
        if(renderBackwards) mul = -1;
        for (int i = 0; i < lasers; i++) {
            double sin = Math.sin(radian + (double)i / lasers * 2 * Math.PI) * size / 2;
            ledStrip.placePoint(position + (int)sin * mul,Math.max(1,size / 5),lights[i% lights.length].getCurrentColor());
        }
    }
}
