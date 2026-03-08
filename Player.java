import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Player {
    private int money = 1500;
    private int pos = 0;
    public final int number;
    private ArrayList<Property> properties = new ArrayList<Property>();
    private PlayerStrat strat;
    private final Board board;

    public Player(int num, Board board, PlayerStrat strat) {
        this.number = num;
        this.board = board;
        this.strat = strat;
    }

    public void move(int moveAmount, Property[] board) {
        pos = (pos + moveAmount) % 40;
        if (pos < moveAmount) {//has this move made the player pass go
            money += 200;
        }
        board[pos].onLand(this);
    }

    public int pos() {
        return pos;
    }

    public void receiveAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can't receive negative amount of money");
        }
        money += amount;
    }

    public void payAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can't have negative amount of rent to pay");
        }

        // Keep mortgaging properties until the player can afford the payment or runs out of options
        while (amount > money) {
            // Sort properties
            ArrayList<Property> orderedProperties = new ArrayList<>(properties);
            orderedProperties.sort(Comparator.comparingInt(Property::value).reversed()); // lowest first

            // Find the lowest value unmortgaged property
            Property propertyToMortgage = null;
            for (int i = 0; i < orderedProperties.size(); i++) {
                Property p = orderedProperties.get(i);
                if (p.info != null && !p.info.mortgaged()) {
                    propertyToMortgage = p;
                    break;
                }
            }

            // No more properties available to mortgage -> player eliminated
            if (propertyToMortgage == null) {
                break;
            }

            // Mortgage property for half its price for every house in set
            mortgageProperty(propertyToMortgage);
        }

        // Pay the amount
        money -= amount;
    }

    public int money() {
        return money;
    }

    public void goToJail() {
        pos = 10;
    }

    public boolean decideBuyProperty(Property property) {
        //TODO test
        return property.price <= strat.propertyValues().get(property.boardIndex).value()
                && property.price < money - (strat.playSafeThreshold() * Board.calcAverageCost(board.board));
    }

    public void buyProperty(Property property) {
        if (money < property.price) {
            throw new IllegalStateException("""
                    Player has less money than the price of this property.
                    The decideBuyProperty method should have returned false but returned:
                    """ + " " + decideBuyProperty(property));
        }
        money -= property.price;
        addProperty(property);
    }

    public int decideBuyHouses(PropertySet set) {
        //TODO test
        if(set.houseCost() < money - (strat.playSafeThreshold() * Board.calcAverageCost(board.board))){
            return (int)((money - (strat.playSafeThreshold() * Board.calcAverageCost(board.board)))/set.houseCost());
        }
        return 0;
    }

    public void buyHouses(PropertySet set, int amount) {
        while (amount > 0) {
            if (money < set.houseCost()) {
                return;
            }
            addHouses(set.properties().getFirst());
            amount--;
        }
    }

    public int decideTradePrice(Property property) {
        //TODO test
        int price = strat.propertyValues().get(board.getIndex(property)).value();
        if(percentageOfPropertyOwned(property.info.set) >= 0.5){
            price *= strat.setValues().get(property.info.set);
        }
        if(price > money){price = money;}
        if(price < 0){ price = 0;}
        return price;
    }

    public boolean decideAcceptTrade(Property property, int offeredPrice, float calculatedAverageCost) {
        //TODO test
        int price = decideTradePrice(property);
        return price > offeredPrice && money - offeredPrice > strat.playSafeThreshold() * calculatedAverageCost;
    }

    public void trade(Property property, Player targetPlayer, int price) {
        if (targetPlayer.equals(this)) {
            throw new IllegalArgumentException("Trying to trade property with themself");
        }
        targetPlayer.removeProperty(property);
        addProperty(property);
        payAmount(price);
        assert money >= 0;
        targetPlayer.receiveAmount(price);
    }

    public boolean decideUnmortgage(Property property, float calculatedAverageCost){
        //TODO test
        int unmortgagePrice = 11 * property.price / 20;
        return unmortgagePrice < money && money - unmortgagePrice > strat.playSafeThreshold() * calculatedAverageCost;
    }

    public boolean bankrupt() {
        return money < 0;
    }

    public void addProperty(Property property) {
        if (property == null) {
            throw new IllegalArgumentException("Can't have null properties");
        }
        if (properties.contains(property)) {
            throw new IllegalArgumentException("Can't have duplicate properties. Already have " + property.name);
        }
        if (property.info.owner() != null) {
            throw new IllegalStateException("Player " + number + " can't have property it is already owned by " + property.info.owner().number);
        }
        properties.add(property);
        property.info.setOwner(this);
    }

    public void removeProperty(Property property) {
        if (property == null) {
            throw new IllegalArgumentException("Can't have null properties");
        }
        if (!properties.contains(property)) {
            throw new IllegalArgumentException("Trying to remove property that isn't in property : " + property.name + " is not in " + properties);
        }
        property.info.setOwner(null);
        properties.remove(property);
    }

    public List<Property> properties() {
        return Collections.unmodifiableList(properties);
    }

    public boolean ownSet(PropertySet set) {
        if (set == null) throw new IllegalArgumentException("Owned set can't be null");

        int totalInSet = set.propertiesInSet();
        if (totalInSet < 0) throw new IllegalArgumentException("Set can't have less than 0 in the set " + set.name());
        ; // defensive

        int ownedInSet = 0;
        for (Property p : properties) {
            if (p == null || p.info == null) continue;
            PropertySet ps = p.info.set;
            if (ps != null && ps.equals(set)) {
                ownedInSet++;
                if (ownedInSet == totalInSet) return true; // early exit
            }
        }
        return false; //to have reached here then ownedInSet == totalInSet must be false
    }

    public void addHouses(Property property) {
        if(property.info.mortgaged()){
            throw new IllegalArgumentException("Can't add houses to mortgaged Property");
        }
        if (property.info.houses() == 5) {
            return;
        }
        money -= property.info.set.houseCost();
        property.info.addHouse();
        assert money >= 0;
    }

    public void mortgageProperty(Property property) {
        if (!properties.contains(property)) {
            throw new IllegalArgumentException("Player don't own property you are trying to mortgage");
        }
        if (property.info.mortgaged()) {
            throw new IllegalArgumentException("Trying to mortgage a property that is already mortgaged");
        }
        property.info.setMortgaged(true);
        money += property.price / 2;
        mortgageHouses(property.info.set);
        assert property.info.houses() == 0;
    }

    public void unmortgageProperty(Property property) {
        if (!properties.contains(property)) {
            throw new IllegalArgumentException("Player don't own property you are trying to mortgage");
        }
        if (!property.info.mortgaged()) {
            throw new IllegalArgumentException("Trying to unmortgage a property that is already unmortgaged");
        }
        property.info.setMortgaged(false);
        money -= 11 * property.price / 20;
    }

    public void mortgageHouses(PropertySet set) {
        for (Property property : set.properties()) {
            money += (property.info.set.houseCost() * property.info.houses()) / 2;
            property.info.removeHouses(property.info.houses());
        }
    }

    public void clearProperty() {
        for (Property property : properties) {
            property.info.setOwner(null);
            if (!property.info.mortgaged()) {
                throw new IllegalArgumentException("Player " + number + " still has a property : " + property.name + " that has not been mortgaged and has no more money ");
            }
            if (property.info.houses() != 0) {
                throw new IllegalArgumentException("Player " + number + " still has a property : " + property.name + " with houses and has no more money ");
            }
        }
        properties.clear();
    }

    public PlayerStrat getStrat(){return strat;}

    public float percentageOfPropertyOwned(PropertySet set){
        int numOwned = 0;
        for(Property property : properties){
            if(property.info.set.equals(set)) numOwned++;
        }
        return (float) numOwned /set.propertiesInSet();
    }

    public void setPos(int pos){
        this.pos = pos;
    }
}
