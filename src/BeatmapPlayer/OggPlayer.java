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

public class OggPlayer extends Thread {
    private String path;
    public boolean playing;
    @Override
    public void run() {
        play(path);
    }
    SourceDataLine line;

    long duration;
    OggPlayer(String path){
        this.path = path;
    }

    private void play(String filePath) {
        if(playing) return;
        final File file = new File(filePath);

        try{
            AudioInputStream in = getAudioInputStream(file);
            AudioFormat outFormat = getOutFormat(in.getFormat());
            final Info info = new Info(SourceDataLine.class, outFormat);

            line = (SourceDataLine) AudioSystem.getLine(info);
            if (line != null) {
                playing = true;
                line.open(outFormat);
                line.start();
                AudioInputStream inputMystream = AudioSystem.getAudioInputStream(outFormat, in);
                //duration = (long)(1000 * inputMystream.getFrameLength() / inputMystream.getFormat().getFrameRate());
                stream(inputMystream, line);
                line.drain();
                line.stop();
                playing = false;
            }

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void stream(AudioInputStream in, SourceDataLine line) throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }

    public long getDuration(){
        return duration;
    }

    public long getCurrentTime(){
        if(line == null) return -1;
        return line.getMicrosecondPosition() / 1000;
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }
}
