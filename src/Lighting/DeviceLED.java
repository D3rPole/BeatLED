package Lighting;

import Lighting.Components.LEDstrip;
import Lighting.Fixtures.Fixture;
import Utils.Debug;
import networking.Device;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class DeviceLED {
    public Device device;
    public LEDstrip ledStrip;
    public Effect[] effects;
    public String name;

    public DeviceLED(){}
    public DeviceLED(String name,String ip, int port, int ledCount) throws SocketException, UnknownHostException {
        this.name = name;
        device = new Device(ip,port);
        ledStrip = new LEDstrip(ledCount);
        effects = new Effect[0];
    }
    public void init(){
        ledStrip.initStrip();
        device.initDevice();
    }
    public void addEffect(String name,int type, int from, int to, boolean reversed){
        Effect effect = new Effect(name,type,from,to,reversed);
        int newLength = effects.length + 1;
        Effect[] newEffects = new Effect[newLength];
        for (int i = 0; i < effects.length; i++) {
            newEffects[i] = effects[i];
        }
        newEffects[newLength - 1] = effect;
        effects = newEffects;
    }

    public void removeEffect(int index){
        int newLength = effects.length - 1;
        Effect[] newEffects = new Effect[newLength];
        int j = 0;
        for (int i = 0; i < effects.length; i++) {
            if(i != index){
                newEffects[j] = effects[i];
                j++;
            }
        }
        effects = newEffects;
    }

    public void applyEffects(Fixture[] fixtures){
        ledStrip.clear();
        for (int i = 0; i < effects.length; i++) {
            Effect effect = effects[i];
            fixtures[effect.type].addToStrip(ledStrip,effect.fromLedIndex,effect.toLedIndex,effect.reversed);
        }
    }

    public void send() throws IOException {
        device.send(ledStrip.toByteArray());
    }

    @Override
    public String toString() {
        return name;
    }
}
