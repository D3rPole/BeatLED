package Lighting;

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
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LEDController {
    Fixture[] fixtures;

    TimerTask timerTask;

    boolean active = false;

    public LEDController(){
        Debug.log("Creating LEDController");

        setEnvironment("DefaultEnvironment");

        Runnable task = () -> {
            Timer timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            };
            timer.schedule(timerTask,0,32);
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

    int ticks = 0;
    long lastTime = new Date().getTime();
    void update(){
        ticks++;
        if(new Date().getTime() - lastTime > 1000){
            lastTime = new Date().getTime();
            Debug.log("tps: " + ticks);
            ticks = 0;
        }
        if(active) {
            //Thread thread = new Thread(()->{
                for (int i = 0; i < fixtures.length; i++) {
                    fixtures[i].update();
                }
            //});
            //thread.run();

            for (int i = 0; i < Config.devices.size(); i++) {
                //Config.devices.get(i).applyEffects(fixtures);
                Config.deviceArray[i].applyEffects(fixtures);
            }
            try {
                for (int i = 0; i < Config.devices.size(); i++) {
                    //Config.devices.get(i).send();
                    Config.deviceArray[i].device.send(Config.deviceArray[i].ledStrip.toByteArray());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
