import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game {
    private final Board board = new Board(this);
    private final List<Player> players;

    public Game(){
        int numPlayers = 4;
        this.players = initPlayers(numPlayers, Tournament.initPlayerStrats(numPlayers));
    }

    public Game(List<PlayerStrat> strats){
        this.players = initPlayers(strats.size(), strats);
    }

    private ArrayList<Player> initPlayers(int numPlayers, List<PlayerStrat> strats) {
        assert numPlayers == strats.size();
        ArrayList<Player> playersList = new ArrayList<Player>();
        for(int i = 1; i <= numPlayers; i++) {
            playersList.add(new Player(i, board, strats.get(i-1)));
        }
        return playersList;
    }

    private boolean gameOver = false;

    public void playRound() {
        ArrayList<Player> toRemove = new ArrayList<Player>();
        for(Player player : players){
            playTurn(player, 0);
            if(player.bankrupt()){toRemove.add(player);};
        }
        for(Player player : toRemove){
            player.clearProperty();
            players.remove(player);
        }
    }

    public void playTurn(Player player, int doublesRolled){
        if(gameOver() || player.money() < 0){return;}
        if(doublesRolled == 3){
            player.goToJail();
            return;
        }

        if(player.money() < 0){throw new IllegalStateException("1 " + player.money());}
        tryUnmortgageProperty(player);

        if(player.money() < 0){throw new IllegalStateException("2 " + player.money());}
        tryTradeProperty(player);

        if(player.money() < 0){throw new IllegalStateException("3 " + player.money());}
        tryAddHouses(player);
        if(player.money() < 0){throw new IllegalStateException("4 " + player.money());}

        DiceRoll roll = new DiceRoll();
        player.move(roll.rollTotal, board.board);

        if(player.money() < 0){
            //only removing player once the round is done. Just ending their turn here for now
            return;
        }

        if(roll.rolledDouble){playTurn(player, doublesRolled + 1);}
    }

    public List<Player> players(){
        return players;
    }

    public boolean gameOver(){
        gameOver = players.size() < 2;
        return gameOver;
    }

    public Board board(){
        return board;
    }

    public void tryAddHouses(Player player){
        ArrayList<PropertySet> setsPlayerOwnsPartOf = new ArrayList<PropertySet>();
        for(Property property : player.properties()){
            if(!setsPlayerOwnsPartOf.contains(property.info.set)
                    && !property.info.set.equals(PropertySet.STATIONS)
                    && !property.info.set.equals(PropertySet.UTILITIES)
                    && property.info.houses() < 5) {
                setsPlayerOwnsPartOf.add(property.info.set);
            }
        }
        for(PropertySet set : setsPlayerOwnsPartOf){
            if(!player.ownSet(set))continue;
            if(set.properties().stream().anyMatch(p -> p.info.mortgaged())){
                continue;
            }
            int numToAdd = player.decideBuyHouses(set);
            if(numToAdd < 0) throw new IllegalStateException("");
            player.buyHouses(set, numToAdd);
        }
    }

    public void tryTradeProperty(Player player){
        float calculatedAverageCost = Board.calcAverageCost(board.board);
        for(Player targetPlayer : players){
            if(targetPlayer == player) continue;
            HashMap<Property, Integer> toTrade = new HashMap<Property, Integer>();
            for(Property property : targetPlayer.properties()){
                int decidedPrice = player.decideTradePrice(property);
                if(decidedPrice < 0){throw new IllegalStateException("Player is tyring to sell property for negative price " + decidedPrice);}
                if(targetPlayer.decideAcceptTrade(property, decidedPrice, calculatedAverageCost)){
                    toTrade.put(property, decidedPrice);
                }
            }
            for(Property property : toTrade.keySet()){
                int price = toTrade.get(property);
                if(price <= player.money()) player.trade(property, targetPlayer, price);
            }
        }
    }

    private void tryUnmortgageProperty(Player player) {
        float averageCost = Board.calcAverageCost(board.board);
        for(Property property : player.properties()){
            if(property.info.mortgaged() && player.decideUnmortgage(property, averageCost)){
                player.unmortgageProperty(property);
            }
        }
    }

    public Player getPlayerWithMostMoney(){
        Player mostMoney = players.getFirst();
        for(Player player : players){
            if(player.money() > mostMoney.money()){
                mostMoney = player;
            }
        }
        return mostMoney;
    }
}
