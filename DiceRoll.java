public class DiceRoll {
    public final int roll1 = dieRoll();
    public final int roll2 = dieRoll();
    public final int rollTotal = roll1 + roll2;
    public final boolean rolledDouble = roll1 == roll2;

    public int dieRoll(){
        return (int)(Math.random() * 6) + 1;
    }
}
