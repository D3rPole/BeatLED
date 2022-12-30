package Lighting.Fixtures;

import Lighting.LEDstrip;

public class SimpleLamp extends Fixture{
    public SimpleLamp(int numberLamps) {
        super(numberLamps);
    }


    @Override
    public void addToStrip(LEDstrip ledStrip, int position, int size, boolean renderBackwards) {
        int mul = 1;
        if(renderBackwards) mul = -1;
        for (int i = 0; i < size; i++) {
            ledStrip.setColorSingle(position + i*mul,lights[i%lights.length].getCurrentColor());
        }
    }
}
