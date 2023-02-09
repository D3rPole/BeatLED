
import Lighting.LEDController;
import UI.MainUI;
import Utils.Utils;
import com.formdev.flatlaf.FlatDarculaLaf;

public class Main {

    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        Utils.ui = new MainUI();
        Utils.ledController = new LEDController();
    }
}