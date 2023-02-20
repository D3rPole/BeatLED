package Lighting;

public class Effect {
    public String name;

    public int type;
    public int fromLedIndex;
    public int toLedIndex;
    public boolean reversed;

    public Effect(String name, int type, int fromLedIndex, int toLedIndex, boolean reversed){
        this.name = name;
        this.type = type;
        this.fromLedIndex = fromLedIndex;
        this.toLedIndex = toLedIndex;
        this.reversed = reversed;
    }

    Effect(){}

    @Override
    public String toString() {
        return name;
    }
}
