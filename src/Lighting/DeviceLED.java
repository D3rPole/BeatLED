package Lighting;

import Lighting.Components.LEDstrip;
import Lighting.Fixtures.Fixture;
import networking.Device;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class DeviceLED {
    public Device device;
    public LEDstrip ledStrip;
    public ArrayList<Effect> effects;
    public String name;

    public DeviceLED(){}
    public DeviceLED(String name,String ip, int port, int ledCount) throws SocketException, UnknownHostException {
        this.name = name;
        device = new Device(ip,port);
        ledStrip = new LEDstrip(ledCount);
        effects = new ArrayList<>();
    }
    public void init(){
        ledStrip.initStrip();
        device.initDevice();
    }
    public void addEffect(int type, int from, int to, boolean reversed){
        Effect effect = new Effect(type,from,to,reversed);
        effects.add(effect);
    }

    public void applyEffects(Fixture[] fixtures){
        ledStrip.clear();
        for (int i = 0; i < effects.size(); i++) {
            Effect effect = effects.get(i);
            if(i > fixtures.length) break;

            fixtures[i].addToStrip(ledStrip,effect.fromLedIndex,effect.toLedIndex,effect.reversed);
        }
        try {
            device.send(ledStrip.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
