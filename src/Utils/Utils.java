package Utils;

import BeatmapPlayer.BeatmapPlayer;
import Lighting.Components.Color;
import Lighting.LEDController;
import UI.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Utils {
    public static Main ui;
    public static LEDController ledController;
    public static BeatmapPlayer beatmapPlayer;
    public static int leftLaserSpeed;
    public static int rightLaserSpeed;
    public static Color colorA = new Color(255,0,0);
    public static Color colorB = new Color(0,0,255);
    public static Color white = new Color(255,255,255);
    public static double BPM;
    public static int difficulty;

    /*public static String readFile(File file) {
        try {
            Scanner scanner = new Scanner(file);
            String str = "";
            while (scanner.hasNext()){
                str += scanner.next();
            }
            return str;
        }catch(FileNotFoundException e){
            Debug.log(e);
        }
        return "";
    }*/

    public static String readFile(File file) {
        try {
            Scanner scanner = new Scanner(file);
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()){
                sb.append(scanner.nextLine());
            }
            scanner.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            Debug.log(e);
        }
        return "";
    }
    public static void setEnabledRecursive(Component c, boolean enabled) {
        c.setEnabled(enabled);
        if (c instanceof Container) {
            Component[] components = ((Container) c).getComponents();
            for (Component child : components) {
                setEnabledRecursive(child, enabled);
            }
        }
    }

    public static BufferedImage loadImageFromResource(String fileName){
        BufferedImage buff;
        try {
            buff = ImageIO.read(Objects.requireNonNull(Utils.class.getResourceAsStream(fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buff;
    }
}
