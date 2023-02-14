package Lighting;

public class Effect {
    public enum effect{ BACK_LIGHTS, RING_LIGHTS, LEFT_LASER, RIGHT_LASER, CENTER_LIGHTS }
    public int type;
    public int fromLedIndex;
    public int toLedIndex;
    public boolean reversed;

    public Effect(int type, int fromLedIndex, int toLedIndex, boolean reversed){
        this.type = type;
        this.fromLedIndex = fromLedIndex;
        this.toLedIndex = toLedIndex;
        this.reversed = reversed;
    }
}
