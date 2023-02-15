package Lighting.Fixtures;

import Lighting.Components.LEDstrip;

public class RingRotating extends Fixture{

    public RingRotating(int numberLamps) {
        super(numberLamps);
    }

    @Override
    public void addToStrip(LEDstrip ledStrip, int from, int to, boolean renderBackwards) {
        int mul = 1;
        int offset = 0;
        int size = to - from;
        if(renderBackwards) {
            mul = -1;
            offset = size;
        }
        for (int i = 0; i <= size; i++) {
            ledStrip.setColorSingle(from + offset + i*mul,lights[(int)(((double)i / size)*(lights.length-1))].getCurrentColor());
        }
    }

}
