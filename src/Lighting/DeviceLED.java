package Lighting;

import Lighting.Components.LEDstrip;
import networking.Device;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class DeviceLED {
    public Device device;
    public LEDstrip ledStrip;
    public ArrayList<Effect> effects;

    DeviceLED(String ip,int port,int ledCount) throws SocketException, UnknownHostException {
        device = new Device(ip,port);
        ledStrip = new LEDstrip(ledCount);
    }

    public void addEffect(int type, int from, int to, boolean reversed){
        Effect effect = new Effect(type,from,to,reversed);
        effects.add(effect);
    }

    public void applyEffects(){
        ledStrip.clear();

    }
}
