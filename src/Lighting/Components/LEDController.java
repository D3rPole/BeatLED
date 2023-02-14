package Lighting.Components;

import Beatsaber.LightEvent;
import Lighting.DeviceLED;
import Lighting.Effect;
import Utils.*;
import Lighting.Fixtures.Fixture;
import Lighting.Fixtures.LaserRotating;
import Lighting.Fixtures.RingRotating;
import Lighting.Fixtures.SimpleLamp;
import networking.Device;
import org.json.JSONObject;

import java.io.File;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class LEDController {
    Fixture[] fixtures;

    /*Device barSides;
    Device shishaTeller;
    Device barLEDs;
    Device bildLEDs;*/

    DeviceLED[] devices;

    TimerTask timerTask;

    boolean active = false;

    public LEDController(){
        Debug.log("Creating LEDController");
        devices = new DeviceLED[1];
        try {
            devices[0] = new DeviceLED("192.168.178.77",65506,100);
            devices[0].addEffect(Effect.effect.CENTER_LIGHTS.ordinal(),20,22,false);
            /*barSides = new Device("192.168.178.67",65506);
            shishaTeller = new Device("192.168.178.72",65506);
            barLEDs = new Device("192.168.178.76",65506);
            bildLEDs = new Device("192.168.178.73",65506);*/
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }

        setEnvironment("DefaultEnvironment");

        Runnable task = () -> {
            Timer timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            };
            timer.schedule(timerTask,0,20);
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    public void setEnvironment(String environment){
        Debug.log("environment: " + environment);
        URL resource = getClass().getResource("/LightIDTables/" + environment + ".json");
        String path;
        if(resource == null){
            path = getClass().getResource("/LightIDTables/DefaultEnvironment.json").getFile();
        }else{
            path = resource.getFile();
        }
        path = path.replace("%20"," ");
        File envFile = new File(path);
        Debug.log(path);
        JSONObject envJson = new JSONObject(Utils.readFile(envFile));

        int fixtureCount = envJson.length();
        fixtures = new Fixture[fixtureCount];
        for (int i = 0; i < fixtures.length; i++) {
            JSONObject fixtureInfo = envJson.getJSONObject((1 + i) + "");
            int lightCount = fixtureInfo.length() - 1;
            String fixtureType = fixtureInfo.getString("type");
            switch (fixtureType){
                case("lights_back"), ("lights_center"), ("ring_non_rotating") -> fixtures[i] = new SimpleLamp(lightCount);
                case("lasers_left"), ("lasers_right") -> fixtures[i] = new LaserRotating(lightCount);
                case("ring_rotating"), ("ring_rotating_secondary") -> fixtures[i] = new RingRotating(lightCount);
            }
            fixtures[i].fixtureType = fixtureType;
            Debug.log(fixtures[i]);
        }
    }

    /*LEDstrip ledStripBarTop = new LEDstrip(157);
    LEDstrip ledStripShisha = new LEDstrip(10);

    LEDstrip ledBarSides = new LEDstrip(357);
    LEDstrip ledBildSides = new LEDstrip(130);*/


    void update(){
        if(active) {
            /*ledStripBarTop.clear();
            ledStripShisha.clear();
            ledBarSides.clear();
            ledBildSides.clear();*/

            for (int i = 0; i < fixtures.length; i++) {
                fixtures[i].update();
            }
        /*
        0: backlights
        1: ring
        2: left laser
        3: right laser
        4: center lights
         */

            /*fixtures[4].addToStrip(ledStripShisha, 0, 10);

            fixtures[0].addToStrip(ledStripBarTop, 0, 30);
            fixtures[0].addToStrip(ledStripBarTop, 157, 30, true);
            fixtures[1].addToStrip(ledStripBarTop, 78, 48, true);
            fixtures[1].addToStrip(ledStripBarTop, 79, 48);
            fixtures[4].addToStrip(ledStripBarTop, 68, 20);

            fixtures[2].addToStrip(ledBarSides, 63, 120);
            fixtures[3].addToStrip(ledBarSides, 189, 120, true);
            fixtures[0].addToStrip(ledBarSides, 252, 35);
            fixtures[4].addToStrip(ledBarSides, 287, 35);
            fixtures[0].addToStrip(ledBarSides, 322, 35);

            fixtures[1].addToStrip(ledBildSides, 14, 51);
            fixtures[1].addToStrip(ledBildSides, 79, 51);

            fixtures[4].addToStrip(ledBildSides, 0, 14);
            fixtures[4].addToStrip(ledBildSides, 65, 14);*/

            for (int i = 0; i < devices.length; i++) {
                devices[i].applyEffects(fixtures);
            }
            /*try {
                shishaTeller.send(ledStripShisha.toByteArray());
                barSides.send(ledBarSides.toByteArray());
                barLEDs.send(ledStripBarTop.toByteArray());
                bildLEDs.send(ledBildSides.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/
        }
    }

    public void lightEvent(LightEvent event){
        if(event.type == 12) Utils.leftLaserSpeed = event.valueAlt;
        if(event.type == 13) Utils.rightLaserSpeed = event.valueAlt;
        if(event.type > 4) return;
        if(event.customData.lightIDs.length == 0){
            all(event);
        }else{
            lightID(event);
        }
        Utils.ui.log(event);
    }
    void all(LightEvent event){
        Fixture fixture = fixtures[event.type];
        switch (event.value){
            case ON -> fixture.on();
            case OFF -> fixture.off();
            case FLASH -> fixture.flash();
            case FADEOUT -> fixture.fadeout();
            case OTHER -> {

            }
        }
        if(event.customData.gradient != null){
            fixture.setGradient(event.customData.gradient);
        }
        if(event.color != null) {
            fixture.overrideColor = true;
        }
        if(fixture.overrideColor){
            if(event.color != null){
                fixture.setColor(event.color);
            }
            return;
        }
        if(event.valueAlt < 4){
            fixture.setColor(Utils.colorA);
        }else{
            fixture.setColor(Utils.colorB);
        }
    }
    void lightID(LightEvent event){
        Fixture fixture = fixtures[event.type];

        int[] lightIDs = event.customData.lightIDs;
        switch (event.value){
            case ON -> fixture.on(lightIDs);
            case OFF -> fixture.off(lightIDs);
            case FLASH -> fixture.flash(lightIDs);
            case FADEOUT -> fixture.fadeout(lightIDs);
            case OTHER -> {

            }
        }
        if(event.customData.gradient != null){
            fixture.setGradient(lightIDs,event.customData.gradient);
        }
        if(event.color != null) {
            fixture.overrideColor = true;
        }
        if(fixture.overrideColor){
            if(event.color != null){
                fixture.setColor(lightIDs,event.color);
            }
            return;
        }
        if(event.valueAlt < 4){
            fixture.setColor(lightIDs, Utils.colorA);
        }else{
            fixture.setColor(lightIDs, Utils.colorB);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        Utils.ui.setActive(active);
    }
}
