//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        //playSingleGame();
        System.out.println("enieurv");
        playTournament();
    }

    public static void playSingleGame() {
        Game game = new Game();
        MonopolyBoardDrawer.createAndShow(game);
        int roundNum = 0;
        while (!game.gameOver() && roundNum < 1000) {
            game.playRound();
            roundNum++;
            System.out.println("It is round " + roundNum);
            System.out.println("There are " + game.players().size() + " players left");
        }
        System.out.println("Player " + game.players().getFirst().number + " won.");
        System.out.println("Played " + roundNum);
    }

    public static void playTournament(){
        Tournament t = new Tournament();
        for(int i = 0; i < 100; i++){
            t.run();
            t.sortByWinRate();  
            System.out.println("Tournament " + i);
            System.out.println("----------------------------------");
            System.out.println("Winner Win Rate is " + t.winRate(t.playerStrats.getFirst()));
            System.out.println("Average Win Rate is " + t.averageWinRate());
            System.out.println("Winner Safe Threshold " + t.playerStrats.getFirst().playSafeThreshold());
            System.out.println("Number of Drawn Games is " + t.getNumGamesDrawn());
            System.out.println("The Evaluation is : " + t.evaluateWinner());
            System.out.println();
            System.out.println("Values Properties : ");
            System.out.println("----------------------------------");
            for(PropertyValue value : t.playerStrats.getFirst().propertyValues()){
                if(Board.PRICES[value.index()] == 0) continue;
                System.out.println("Property " + value.name() + " : " + value.normalValue() + " : " + value.value() + " : " + ((float)value.value() / value.normalValue()));
            }
            System.out.println("----------------------------------");
            System.out.println();
            System.out.println("Values Sets : ");
            System.out.println("----------------------------------");
            for(PropertySet set : t.playerStrats.getFirst().setValues().keySet()){
                System.out.println("Set " + set.name + " : " + t.playerStrats.getFirst().setValues().get(set));
            }
            System.out.println("----------------------------------");
            System.out.println();
            t.evolve();
        }
    }
}