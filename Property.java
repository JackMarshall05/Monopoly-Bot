public class Property {
    public final String name;
    public final int price;
    public final int[] rent;       // REGULAR: [base, 1h, 2h, 3h, 4h, hotel]; Stations/Utilities use their patterns
    public final PropertyInfo info;// null for non-ownables
    public final int boardIndex;

    public Property(String name, int price, int[] rent, PropertyInfo info, int boardIndex) {
        this.name = name;
        this.price = price;
        this.rent = rent;
        this.info = info;
        this.boardIndex = boardIndex;
    }

    public static Property PropertyFactory(String name, int price, int[] rent, PropertyInfo info, int boardIndex){
        return new Property(name, price, rent, info, boardIndex);
    }

    public final boolean ownable() {
        return info.set != null && !"Other".equalsIgnoreCase(info.set.name());
    }

    public final int getRent(int houseCount) {
        if (rent == null || houseCount < 0 || houseCount >= rent.length) return 0;
        return rent[houseCount];
    }

    /** Override per-tile logic in anonymous inner classes when constructing the board. */
    public void onLand(Player player){
        throw new IllegalStateException("onLan is supposed to be override by anonymous inner class on init");
    }

    public int value(){
        if(info.owner() == null){throw new IllegalStateException("Can't get value of property if its not owned");}
        return (int)info.owner().getStrat().propertyValues().get(boardIndex).value() * price;
    }

    public String toString(){
        return this.name;
    }

    public boolean equals(Object o){
        return o instanceof Property && ((Property) o).name.equals(name);
    }
}