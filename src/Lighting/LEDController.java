package Lighting;

import BeatmapLoader.Beatmap.Event;
import Lighting.Components.Color;
import Lighting.Components.LEDstrip;
import Lighting.Fixtures.Fixture;
import Lighting.Fixtures.LaserRotating;
import Lighting.Fixtures.RingRotating;
import Lighting.Fixtures.SimpleLamp;
import Utils.Config;
import Utils.Debug;
import Utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import networking.Device;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

public class LEDController {
    Fixture[] fixtures;
    boolean active = false;
    public long TPS;
    public long tickTime;
    final int TARGET_FPS = 120;
    final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
    long lastLoopTime = System.nanoTime();
    public boolean strobing = false;

    int strobeFreq;
    int strobeDuty;
    int strobeBrightness;

    int tempTPS = 0;
    long time;

    public LEDController(){
        Debug.log("Creating LEDController");
        setEnvironment("DefaultEnvironment");
    }
    public void setEnvironment(String environment){
        Debug.log("environment: " + environment);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode envJson = null;
        try (InputStream in = getClass().getResourceAsStream("/LightIDTables/" + environment + ".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            envJson = objectMapper.readTree(reader.lines().collect(Collectors.joining()));
        } catch (IOException e) {
            try (InputStream in = getClass().getResourceAsStream("/LightIDTables/DefaultEnvironment.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                envJson = objectMapper.readTree(reader.lines().collect(Collectors.joining()));
            } catch (IOException ex) {
                Debug.log(ex);
            }
        }

        int fixtureCount = envJson.size();
        fixtures = new Fixture[fixtureCount];
        for (int i = 0; i < fixtures.length; i++) {
            JsonNode fixtureInfo = envJson.get((1 + i) + "");
            int lightCount = fixtureInfo.size() - 1;
            String fixtureType = fixtureInfo.get("type").asText();
            switch (fixtureType){
                case("lights_back"), ("lights_center"), ("ring_non_rotating") -> fixtures[i] = new SimpleLamp(lightCount);
                case("lasers_left"), ("lasers_right") -> fixtures[i] = new LaserRotating(lightCount);
                case("ring_rotating"), ("ring_rotating_secondary") -> fixtures[i] = new RingRotating(lightCount);
            }
            fixtures[i].fixtureType = fixtureType;
            Debug.log(fixtures[i]);
        }
    }
    public void update(){
        Debug.log("starting update loop");
        Utils.ui.controller.updateStrobe();
        time = System.nanoTime();
        ScheduledFuture s = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::loop, 0, OPTIMAL_TIME, TimeUnit.NANOSECONDS);
        /*while(true) {
            long start = System.nanoTime();
            if(time + 1000000000 < System.nanoTime()){
                TPS = tempTPS;
                tempTPS = 0;
                time = System.nanoTime();
            }
            tempTPS++;
           // TPS = 1000000000 / (start - lastLoopTime);
            lastLoopTime = start;

            if (active) {
                if(strobing){
                    applyStrobe();
                }else{
                    applyBeatmapEvents();
                }
                send();
            }
            tickTime = System.nanoTime() - start;
            Utils.ui.controller.updateFPS(TPS,tickTime,TARGET_FPS);
            try {
                TimeUnit.NANOSECONDS.sleep(OPTIMAL_TIME - tickTime);
            } catch (Exception e) {}
        }*/
    }
    public void loop(){
        long start = System.nanoTime();
        if(time + 1000000000 < System.nanoTime()){
            TPS = tempTPS;
            tempTPS = 0;
            time = System.nanoTime();
        }
        tempTPS++;
        // TPS = 1000000000 / (start - lastLoopTime);
        lastLoopTime = start;
        if (active) {
            if(strobing){
                applyStrobe();
            }else{
                try {
                    Utils.beatmapPlayer.update();
                    applyBeatmapEvents();
                }catch(Exception e){
                    throw e;
                }
            }
            //send();
        }
        tickTime = System.nanoTime() - start;
        Utils.ui.controller.updateFPS(TPS,tickTime,TARGET_FPS);
    }
    void applyStrobe(){
        int nanoPerFlash = 1000000000 / strobeFreq;
        long time = System.nanoTime();
        int brightness = 0;
        if(time%nanoPerFlash > (long) nanoPerFlash * (100 - strobeDuty) / 100){
            brightness = strobeBrightness;
        }
        Color c = new Color(255,255,255).brightness(brightness);
        for (int i = 0; i < Config.devices.size(); i++) {
            Config.devices.get(i).ledStrip.setColorSolid(c);
        }
    }
    void applyBeatmapEvents(){
        for (int i = 0; i < fixtures.length; i++) {
            fixtures[i].update();
        }

        for (int i = 0; i < Config.devices.size(); i++) {
            Config.devices.get(i).applyEffects(fixtures);
        }
    }
    public void setStrobeSettings(int freq, int duty, int brightness){
        strobeFreq = freq;
        strobeDuty = duty;
        strobeBrightness = brightness;
    }
    public void send(){
        try {
            Device[] devices = new Device[Config.devices.size()];
            LEDstrip[] strips = new LEDstrip[Config.devices.size()];
            for (int i = 0; i < Config.devices.size(); i++) {
                devices[i] = Config.devices.get(i).device;
                strips[i] = Config.devices.get(i).ledStrip;
            }
            for (int i = 0; i < devices.length; i++) {
                devices[i].send(strips[i].toByteArray());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void lightEvent(Event event){
        if(event.type == 12) Utils.leftLaserSpeed = event.valueAlt;
        if(event.type == 13) Utils.rightLaserSpeed = event.valueAlt;
        if(event.type == 8 || event.type == 9) { //Ring spin and zoom causes global flash, idk what else to do
            for (int i = 0; i < fixtures.length; i++) {
                fixtures[i].flashOnlyActive();
            }
        }
        if(event.type > 4) return;
        if(event.customData.lightIDs.length == 0){
            all(event);
        }else{
            lightID(event);
        }
        Utils.ui.logs.logEvent(event);
    }
    void all(Event event){
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
        if(event.valueAlt <= 4){
            fixture.setColor(Utils.colorA);
        }else if(event.valueAlt <= 8){
            fixture.setColor(Utils.colorB);
        }else{
            fixture.setColor(Utils.white);
        }
    }
    void lightID(Event event){
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
        if(event.valueAlt <= 4){
            fixture.setColor(lightIDs, Utils.colorA);
        }else if(event.valueAlt <= 8){
            fixture.setColor(lightIDs, Utils.colorB);
        }else{
            fixture.setColor(lightIDs, Utils.white);
        }
    }
    public void setActive(boolean active) {
        this.active = active;
        Utils.ui.controller.setActiveCheckBox(active);
    }

    public boolean isActive() {
        return active;
    }
}
