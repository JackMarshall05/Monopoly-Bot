public class PropertyInfo {
    private boolean mortgaged = false;
    private int houses = 0; // 0..4, and treat 5 as hotel if you like
    private Player owner = null;
    public final PropertySet set;

    public PropertyInfo(PropertySet set){
        this.set = set;
    }

    public boolean mortgaged() { return mortgaged; }
    public void setMortgaged(boolean value) { mortgaged = value; }

    public int houses() { return houses; }
    public void addHouse() {
        if(set.equals(PropertySet.STATIONS) || set.equals(PropertySet.UTILITIES)){throw new IllegalArgumentException("Can't add house to station or utility");}
        houses++;
        if (houses > 5) throw new IllegalArgumentException("houses must be 0..5 Currently : " + houses);
    }
    public void removeHouses(int num) {
        houses -= num;
        if (houses < 0) throw new IllegalArgumentException("houses must be 0..5 Currently : " + houses);
    }
    public Player owner(){return owner;}
    public void setOwner(Player o){
        this.owner = o;
    }
}
