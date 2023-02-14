package Utils;

import Lighting.Components.Color;
import Lighting.Components.LEDController;
import UI.MainUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Utils {
    public static MainUI ui;
    public static LEDController ledController;
    public static int leftLaserSpeed;
    public static int rightLaserSpeed;
    public static Color colorA = new Color(255,0,0);
    public static Color colorB = new Color(0,0,255);
    public static double BPM;
    public static int difficulty;

    public static String readFile(File file) {
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
    }
}
