package BeatmapPlayer;

import Utils.Debug;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public class OggPlayerDeprecated {
    private String path;
    public boolean playing;
    SourceDataLine line;
    AudioInputStream in;
    AudioInputStream inputMystream;
    AudioFormat outFormat;
    Info info;
    long duration;
    long timePaused;
    int skippedBytesTotal = 0;
    OggPlayerDeprecated(String path){
        this.path = path;
        thread = new Thread(() -> {
            run();
        });
    }

    Thread thread;

    private boolean stop;

    public void run() {
        if(playing) return;
        final File file = new File(path);

        try{
            in = getAudioInputStream(file);
            outFormat = getOutFormat(in.getFormat());
            info = new Info(SourceDataLine.class, outFormat);

            duration = (long)(1000L * in.available() / outFormat.getFrameRate());

            line = (SourceDataLine) AudioSystem.getLine(info);
            if (line != null) {
                playing = true;
                line.open(outFormat);
                line.start();
                inputMystream = AudioSystem.getAudioInputStream(outFormat, in);
                stream(inputMystream, line);
                line.drain();
                line.stop();
                playing = false;
            }

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void stopAudio(){
        if(!thread.isAlive()) return;
        stop = true;
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    long bytesToSkip;
    synchronized public void setTime(long time){
        stopAudio();
        thread = new Thread(() -> {
            run();
        });
        thread.start();

        bytesToSkip = (long)((time / 1000.0) * outFormat.getFrameRate()) * outFormat.getFrameSize() + skippedBytesTotal;
    }

    public void pause(){
        timePaused = getTime();
        stopAudio();
    }
    public void unpause(){
        setTime(timePaused);
    }

    public void play() {
        thread.start();
    }

    private void stream(AudioInputStream in, SourceDataLine line) throws IOException {
        if(bytesToSkip != 0){
            Debug.log("Skipping " + bytesToSkip);
            byte[] arr = new byte[outFormat.getFrameSize()];
            int skippedBytes = 0;
            for (int i = 0; i != -1; i = in.read(arr,0,outFormat.getFrameSize())) {
                skippedBytes += i;
                if(skippedBytes >= bytesToSkip) break;
            }
            skippedBytesTotal += skippedBytes;
            bytesToSkip = 0;
        }
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            if(stop) {
                stop = false;
                break;
            }
            line.write(buffer, 0, n);
        }
    }

    public long getDuration(){
        if(!line.isOpen()) return 0;
        return duration;
    }

    public long getTime(){
        if(!line.isOpen()) return 0;
        return (long) (line.getMicrosecondPosition() / 1000 + skippedBytesTotal / outFormat.getFrameSize() / outFormat.getFrameRate() * 1000);
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    public boolean isPlaying(){
        return playing;
    }


}
