import java.util.*;

public class Board {
    public static final int SIZE = 40;
    public final Property[] board;

    private final Game game;

    public static final String[] NAMES = {
            "GO","Old Kent Road","Community Chest1","Whitechapel Road","Income Tax","Kings Cross Station","The Angel Islington","Chance1","Euston Road","Pentonville Road",
            "Jail / Just Visiting","Pall Mall","Electric Company","Whitehall","Northumberland Avenue","Marylebone Station","Bow Street","Community Chest2","Marlborough Street","Vine Street",
            "Free Parking","Strand","Chance2","Fleet Street","Trafalgar Square","Fenchurch St Station","Leicester Square","Coventry Street","Water Works","Piccadilly",
            "Go To Jail","Regent Street","Oxford Street","Community Chest3","Bond Street","Liverpool Street Station","Chance3","Park Lane","Super Tax","Mayfair"
    };

    public static final int[] PRICES = {0,60,0,60,200,200,100,0,100,120,0,140,150,140,160,200,180,0,180,200,0,220,0,220,240,200,260,260,150,280,0,300,300,0,320,200,0,350,100,400};


    private Queue<ChanceCard> chanceCards = initChanceCards();
    private Queue<CommunityChestCard> communityChestCards = initCommunityChestCards();

    public Board(Game game) {
        this.game = game;

        this.board = generateBoard();

        for(Property property : board){
            if(property.info != null && property.info.set != null){
                property.info.set.addProperty(property);
            }
        }
    }

    public Property[] generateBoard() {
        Property[] board = new Property[SIZE];
        int i = 0;

        board[i] = go(NAMES[i]);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{2,10,30,90,160,250}, PropertySet.BROWN, i);
        i++;

        board[i] = communityChest(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{4,20,60,180,320,450}, PropertySet.BROWN, i);
        i++;

        board[i] = tax(NAMES[i], PRICES[i], i);
        i++;

        board[i] = station(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{6,30,90,270,400,550}, PropertySet.LIGHT_BLUE, i);
        i++;

        board[i] = chance(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{6,30,90,270,400,550}, PropertySet.LIGHT_BLUE, i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{8,40,100,300,450,600}, PropertySet.LIGHT_BLUE, i);
        i++;

        board[i] = justVisiting(NAMES[i]); // index 10
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{10,50,150,450,625,750}, PropertySet.PINK, i);
        i++;

        board[i] = utility(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{10,50,150,450,625,750}, PropertySet.PINK, i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{12,60,180,500,700,900}, PropertySet.PINK, i);
        i++;

        board[i] = station(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{14,70,200,550,750,950}, PropertySet.ORANGE, i);
        i++;

        board[i] = communityChest(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{14,70,200,550,750,950}, PropertySet.ORANGE, i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{16,80,220,600,800,1000}, PropertySet.ORANGE, i);
        i++;

        board[i] = freeParking(NAMES[i]);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{18,90,250,700,875,1050}, PropertySet.RED, i);
        i++;

        board[i] = chance(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{18,90,250,700,875,1050}, PropertySet.RED, i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{20,100,300,750,925,1100}, PropertySet.RED, i);
        i++;

        board[i] = station(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{22,110,330,800,975,1150}, PropertySet.YELLOW, i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{22,110,330,800,975,1150}, PropertySet.YELLOW, i);
        i++;

        board[i] = utility(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{24,120,360,850,1025,1200}, PropertySet.YELLOW, i);
        i++;

        board[i] = goToJail(NAMES[i]);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{26,130,390,900,1100,1275}, PropertySet.GREEN, i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{26,130,390,900,1100,1275}, PropertySet.GREEN, i);
        i++;

        board[i] = communityChest(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{28,150,450,1000,1200,1400}, PropertySet.GREEN, i);
        i++;

        board[i] = station(NAMES[i], i);
        i++;

        board[i] = chance(NAMES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{35,175,500,1100,1300,1500}, PropertySet.DARK_BLUE, i);
        i++;

        board[i] = tax(NAMES[i], PRICES[i], i);
        i++;

        board[i] = reg(NAMES[i], PRICES[i], new int[]{50,200,600,1400,1700,2000}, PropertySet.DARK_BLUE, i);
        i++;

        return board;
    }




    private Queue<ChanceCard> initChanceCards(){
        ArrayList<ChanceCard> unShuffledCards = new ArrayList<ChanceCard>(Arrays.asList(ChanceCard.values()));
        Collections.shuffle(unShuffledCards);
        return new ArrayDeque<>(unShuffledCards);
    }

    private Queue<CommunityChestCard> initCommunityChestCards(){
        ArrayList<CommunityChestCard> unShuffledCards = new ArrayList<CommunityChestCard>(Arrays.asList(CommunityChestCard.values()));
        Collections.shuffle(unShuffledCards);
        return new ArrayDeque<CommunityChestCard>(unShuffledCards);
    }

    // ========= Helpers =========

    public void decideToBuy(Property property, Player player){
        if(property.info.owner() != null){
            throw new IllegalStateException("There should have already been a check that property.info.owner() is null because you can only buy a property that has no owner");
        }
        if(player.decideBuyProperty(property)) {
            player.buyProperty(property);
        }
    }

    public void exchangeRent(Property property, Player player, int rentToPay){
        if(property.info.owner() == player){return;}
        player.payAmount(rentToPay);
        if(player.money() < 0){
            //give as much money as the player has by adding the now negative total to the rentToPay
            property.info.owner().receiveAmount(rentToPay + player.money());
            return;
        }
        property.info.owner().receiveAmount(rentToPay);
    }

    /** Regular colour-group property: rent depends on info().houses() — same behavior for all such tiles. */
    private Property reg(String name, int price, int[] rent, PropertySet set, int index) {
        PropertyInfo info = new PropertyInfo(set);
        return new Property(name, price, rent, info, index) {
            @Override public void onLand(Player p) {
                if(info.owner() == null){
                    decideToBuy(this, p);
                    return;
                }
                int rentToPay = getRent(info.houses());
                if(info.owner().ownSet(info.set) && info.houses() == 0){rentToPay *= 2;}
                exchangeRent(this, p , rentToPay);
            }
        };
    }

    /** Stations: placeholder base rent (adjust later when you track ownership). */
    private Property station(String name, int index) {
        PropertyInfo info = new PropertyInfo(PropertySet.STATIONS);
        return new Property(name, 200, new int[]{25,50,100,200}, info, index) {
            @Override public void onLand(Player p) {
                if(info.owner() == null){
                    decideToBuy(this, p);
                    return;
                }
                int rentToPay = getRent((int)p.properties().stream().filter(prop -> prop.info.set.equals(PropertySet.STATIONS)).count());
                exchangeRent(this, p , rentToPay);
            }//houses are the amount of stations
        };
    }

    /** Utilities: placeholder ≈ 4 × avg dice (7) = 28 (adjust later when you track dice/ownership). */
    private Property utility(String name, int index) {
        PropertyInfo info = new PropertyInfo(PropertySet.UTILITIES);
        return new Property(name, 150, new int[]{4,10}, info, index) {
            @Override public void onLand(Player p) {
                if(info.owner() == null){
                    decideToBuy(this, p);
                    return;
                }
                int rentToPay = getRent((int)p.properties().stream().filter(prop -> prop.info.set.equals(PropertySet.UTILITIES)).count()) * new DiceRoll().rollTotal;
                exchangeRent(this, p , rentToPay);
            }
        };
    }

    /** Flat tax tiles. */
    private Property tax(String name, int amount, int index) {
        return new Property(name, 0, new int[]{amount}, null, index) {
            @Override public void onLand(Player p) { p.payAmount(amount); }
        };
    }

    /** Chance tile: currently no-op on land (hook deck here later). */
    private Property chance(String name, int index) {
        return new Property(name, 0, new int[]{0}, null, index) {
            @Override public void onLand(Player p) {
                assert chanceCards.peek() != null;
                assert board[p.pos()].name.contains("Chance");
                chanceCards.peek().apply(game, p);
                chanceCards.add(chanceCards.poll());
            }
        };
    }

    /** Community Chest tile: currently no-op on land (hook deck here later). */
    private Property communityChest(String name, int index) {
        return new Property(name, 0, new int[]{0}, null, index) {
            @Override public void onLand(Player p) {
                assert communityChestCards.peek() != null;
                assert board[p.pos()].name.contains("Community Chest");
                communityChestCards.peek().apply(game, p);
                communityChestCards.add(communityChestCards.poll());
            }
        };
    }

    /** Jail / Just Visiting tile: no-op. */
    private Property justVisiting(String name) {
        return new Property(name, 0, new int[]{0}, null, 10) {
            @Override public void onLand(Player p) {}
        };
    }

    /** Free Parking: no-op. */
    private Property freeParking(String name) {
        return new Property(name, 0, new int[]{0}, null, 20) {
            @Override public void onLand(Player p) { /* TODO: add the house rule for free parking */ }
        };
    }

    /** GO: no-op on land (collect is usually on pass). */
    private Property go(String name) {
        return new Property(name, 0, new int[]{0}, null, 0) {
            @Override public void onLand(Player p) {
                //DO NOTHING BECAUSE THE MOVEMENT SYSTEM DEALS WITH GIVING OUT PASSING GO MONEY
            }
        };
    }

    /** Go To Jail movement using your Player.move() API. */
    private Property goToJail(String name) {
        return new Property(name, 0, new int[]{0}, null, 30) {
            @Override public void onLand(Player p) { p.goToJail(); }
        };
    }

    // Accessor
    public Property get(int index) {
        if (index < 0 || index >= SIZE) throw new IndexOutOfBoundsException("index " + index);
        return board[index];
    }

    public int getIndex(Property property) {
        for(int i = 0; i < board.length; i++){
            if(board[i].equals(property)){
                return i;
            }
        }
        throw new IllegalStateException("Property given is not on this board.");
    }

    public static float calcAverageCost(Property[] properties){
        int totalLoss = 0;
        for(Property property : properties){
            totalLoss += getCostOfLand(property);
        }
        return (float) totalLoss / properties.length;
    }

    public static int calcMaxCost(Property[] properties){
        int maxLoss = 0;
        for(Property property : properties){
            int loss = getCostOfLand(property);
            if(loss > maxLoss){ maxLoss = loss;}
        }
        return maxLoss;
    }

    public static int getCostOfLand(Property property){
        if(property.info != null){ return property.getRent(property.info.houses());}
        if(property.name.contains("Community Chest") || property.name.contains("Chance")){return 0;}
        if(property.name.equals("Income Tax")){ return 200;}
        if(property.name.equals("Super Tax")){return 100;}
        if(property.name.equals("GO")
                || property.name.equals("Free Parking")
                || property.name.equals("Go To Jail")
                || property.name.equals("Jail / Just Visiting")){return 0;}
        throw new IllegalStateException("Property has not been matched to any condition " + property.name);
    }
}
