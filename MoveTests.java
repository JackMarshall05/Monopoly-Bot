import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class MoveTests {
    int[] communityChestPos = new int[]{};
    int[] chancePos = new int[]{};

    @Test
    public void payRegRent() {
        Object[][] properties = new Object[][] {
                {"Old Kent Road", 1,  new int[]{ 2, 10, 30,  90, 160,  250}},
                {"Whitechapel Road", 3, new int[]{ 4, 20, 60, 180, 320,  450}},
                {"The Angel Islington", 6, new int[]{ 6, 30, 90, 270, 400,  550}},
                {"Euston Road", 8, new int[]{ 6, 30, 90, 270, 400,  550}},
                {"Pentonville Road", 9, new int[]{ 8, 40,100, 300, 450,  600}},
                {"Pall Mall", 11, new int[]{10, 50,150, 450, 625,  750}},
                {"Whitehall", 13, new int[]{10, 50,150, 450, 625,  750}},
                {"Northumberland Avenue", 14, new int[]{12, 60,180, 500, 700,  900}},
                {"Bow Street", 16, new int[]{14, 70,200, 550, 750,  950}},
                {"Marlborough Street", 18, new int[]{14, 70,200, 550, 750,  950}},
                {"Vine Street", 19, new int[]{16, 80,220, 600, 800, 1000}},
                {"Strand", 21, new int[]{18, 90,250, 700, 875, 1050}},
                {"Fleet Street", 23, new int[]{18, 90,250, 700, 875, 1050}},
                {"Trafalgar Square", 24, new int[]{20,100,300, 750, 925, 1100}},
                {"Leicester Square", 26, new int[]{22,110,330, 800, 975, 1150}},
                {"Coventry Street", 27, new int[]{22,110,330, 800, 975, 1150}},
                {"Piccadilly", 29, new int[]{24,120,360, 850,1025, 1200}},
                {"Regent Street", 31, new int[]{26,130,390, 900,1100, 1275}},
                {"Oxford Street", 32, new int[]{26,130,390, 900,1100, 1275}},
                {"Bond Street", 34, new int[]{28,150,450,1000,1200, 1400}},
                {"Park Lane", 37, new int[]{35,175,500,1100,1300, 1500}},
                {"Mayfair", 39, new int[]{50,200,600,1400,1700, 2000}}
        };

        for (Object[] prop : properties) {
            String name = (String) prop[0];
            int index = (int) prop[1];
            int[] rents = (int[]) prop[2];

            // Go through all house levels 0–5
            for (int houses = 0; houses < rents.length; houses++) {
                int expected = rents[houses];
                payRentForProperty(expected, houses, index, name, new Game());
            }


            payRentForPropertyWithSet(rents[0] * 2, index, name, new Game());
        }
    }

    private void payRentForProperty(int expectedRent, int houses, int propertyIndex, String propertyName, Game g) {
        Board b = g.board();
        Player p1 = g.players().get(0);
        Player p2 = g.players().get(1);
        p2.receiveAmount(1000); //so that p2 can afford every range of rents

        int p1InitialMoney = p1.money();
        int p2InitialMoney = p2.money();

        p1.addProperty(b.get(propertyIndex));
        for(int i = 0; i < houses; i++) { b.get(propertyIndex).info.addHouse();}
        Assertions.assertEquals(p1InitialMoney, p1.money());
        Assertions.assertEquals(p2InitialMoney, p2.money());

        System.out.println(p1.properties());
        p2.move(propertyIndex, b.board);

        Assertions.assertEquals(propertyName, b.get(propertyIndex).name);
        Assertions.assertEquals(p1InitialMoney + expectedRent, p1.money());
        Assertions.assertEquals(p2InitialMoney - expectedRent, p2.money());
    }

    private void payRentForPropertyWithSet(int expectedRent, int propertyIndex, String propertyName, Game g) {
        Board b = g.board();
        Player p1 = g.players().getFirst();

        Property target = b.board[propertyIndex];
        if (target == null || target.info == null || target.info.set == null) {
            throw new IllegalArgumentException("Target is not ownable at index " + propertyIndex);
        }

        // Own the entire set
        PropertySet set = target.info.set;
        for (Property p : b.board) {
            if (p != null && p.info != null && p.info.set != null && p.info.set.equals(set) && !p.equals(target)) {
                p1.addProperty(p);
            }
        }

        // Delegate to your existing assertion helper
        payRentForProperty(expectedRent, 0, propertyIndex, propertyName, g);
    }

    @Test
    public void buyProperty(){
        Object[][] properties = new Object[][] {
                {"Old Kent Road", 1},
                {"Whitechapel Road", 3},
                {"The Angel Islington", 6},
                {"Euston Road", 8},
                {"Pentonville Road", 9},
                {"Pall Mall", 11},
                {"Whitehall", 13},
                {"Northumberland Avenue", 14},
                {"Bow Street", 16},
                {"Marlborough Street", 18},
                {"Vine Street", 19},
                {"Strand", 21},
                {"Fleet Street", 23},
                {"Trafalgar Square", 24},
                {"Leicester Square", 26},
                {"Coventry Street", 27},
                {"Piccadilly", 29},
                {"Regent Street", 31},
                {"Oxford Street", 32},
                {"Bond Street", 34},
                {"Park Lane", 37},
                {"Mayfair", 39}
        };

        for (Object[] prop : properties) {
            Game g = new Game();
            int index = (int) prop[1];
            Player p = g.players().getFirst();
            Property property = g.board().get(index);

            Assertions.assertEquals(prop[0], property.name); //testing we're getting the right property

            Assertions.assertEquals(1500, p.money());

            p.buyProperty(g.board().get(index));

            Assertions.assertEquals(1, p.properties().size());
            Assertions.assertTrue(p.properties().contains(g.board().get(index)));

            Assertions.assertEquals(1500 - property.price, p.money());
        }
    }

    @Test
    public void mortgageProperty(){
        Object[][] properties = new Object[][] {
                {"Old Kent Road", 1},
                {"Whitechapel Road", 3},
                {"The Angel Islington", 6},
                {"Euston Road", 8},
                {"Pentonville Road", 9},
                {"Pall Mall", 11},
                {"Whitehall", 13},
                {"Northumberland Avenue", 14},
                {"Bow Street", 16},
                {"Marlborough Street", 18},
                {"Vine Street", 19},
                {"Strand", 21},
                {"Fleet Street", 23},
                {"Trafalgar Square", 24},
                {"Leicester Square", 26},
                {"Coventry Street", 27},
                {"Piccadilly", 29},
                {"Regent Street", 31},
                {"Oxford Street", 32},
                {"Bond Street", 34},
                {"Park Lane", 37},
                {"Mayfair", 39}
        };

        for (Object[] prop : properties) {
            Game g = new Game();
            int index = (int) prop[1];
            Player p = g.players().getFirst();
            Property property = g.board().get(index);

            Assertions.assertEquals(prop[0], property.name); //testing we're getting the right property

            Assertions.assertEquals(1500, p.money());

            //First Buy Property
            p.buyProperty(property);

            Assertions.assertEquals(1, p.properties().size());
            Assertions.assertTrue(p.properties().contains(property));

            Assertions.assertEquals(1500 - property.price, p.money());

            Assertions.assertFalse(property.info.mortgaged());

            //Now Mortgage Property
            p.mortgageProperty(property);

            Assertions.assertEquals(1500 - property.price/2, p.money());

            Assertions.assertTrue(property.info.mortgaged());

            p.unmortgageProperty(property);

            Assertions.assertEquals(1500 - property.price + property.price/2 - property.price/2 - property.price/20, p.money());

            Assertions.assertFalse(property.info.mortgaged());
        }
    }

    @Test
    public void passGo(){
        Game g = new Game();
        Player p = g.players().getFirst();
        Assertions.assertEquals(1500, p.money());
        p.move(40, g.board().board);
        Assertions.assertEquals(1700, p.money());
    }

    @Test
    public void chanceAdvanceToGo(){
        ChanceCard card = ChanceCard.values()[0];
        Game game = new Game();
        Player player = game.players().getFirst();
        player.move(20, game.board().board); //moves to free parking where I don't have to worry about an effect on the player landing there
        int playerMoney = player.money();
        card.apply(game, player);
        assert player.pos() == 0;
        System.out.println(player.money() + " " + (playerMoney + 200));
        assert player.money() == playerMoney + 200;
    }

    @Test
    public void communityChestAdvanceToGo(){
        CommunityChestCard card = CommunityChestCard.values()[0];
        Game game = new Game();
        Player player = game.players().getFirst();
        //Doing this because it won't work if the player draws a chance card on G0 and this is easier than making another method specifically for this
        player.move(1, game.board().board);
        int playerMoney = player.money();
        card.apply(game, player);
        assert player.pos() == 0;
        assert player.money() == playerMoney + 200;
    }

    @Test
    public void CommunityChestBankError(){
        CommunityChestCard card = CommunityChestCard.values()[1];
        Game game = new Game();
        Player player = game.players().getFirst();
        int playerMoney = player.money();
        card.apply(game, player);
        assert player.money() == playerMoney + 200;
    }

    @Test
    public void CommunityChestDoctersFee(){
        CommunityChestCard card = CommunityChestCard.values()[2];
        Game game = new Game();
        Player player = game.players().getFirst();
        int playerMoney = player.money();
        card.apply(game, player);
        assert player.money() + 50 == playerMoney;
    }

    @Test
    public void CommunityChestSaleOfStock(){
        CommunityChestCard card = CommunityChestCard.values()[3];
        Game game = new Game();
        Player player = game.players().getFirst();
        int playerMoney = player.money();
        card.apply(game, player);
        assert player.money() == playerMoney + 50;
    }

    @Test
    public void CommunityChestGoToJail(){
        CommunityChestCard card = CommunityChestCard.values()[5];
        Game game = new Game();
        Player player = game.players().getFirst();
        int playerMoney = player.money();
        card.apply(game, player);
        assert player.pos() == 10;
        assert player.money() == playerMoney;
    }

    @Test
    public void communityChestCards(){
        //TODO
    }

    @Test
    public void boardIndex(){
        Game g = new Game();
        for(int i = 0; i < Board.SIZE; i++){
            Assertions.assertEquals(i, g.board().board[i].boardIndex);
        }
    }
}
