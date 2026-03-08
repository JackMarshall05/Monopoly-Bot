import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Tournament {
    private final int concurrentStrats = 200;
    ArrayList<PlayerStrat> playerStrats = initPlayerStrats(concurrentStrats);;
    private final int runNum = 10000;
    private int numGamesDrawn = 0;
    private HashMap<PlayerStrat, Integer> gamesPlayed = new HashMap<PlayerStrat, Integer>();
    private HashMap<PlayerStrat, Integer> gamesWon = new HashMap<PlayerStrat, Integer>();

    public void run(){
        long start = System.currentTimeMillis();
        for(int i = 0; i < runNum; i++){
            runGameWithRandomStrats();
            //System.out.println("Played Games " + i);
        }
        System.out.println("It has been " + ((float)(System.currentTimeMillis()-start))/1000 + " seconds");
    }

    public void sortByWinRate(){
        playerStrats.sort(Comparator.comparingDouble(this::winRate).reversed());
    }

    public void evolve() {
        if (playerStrats.size() <= 1) throw new IllegalStateException("There is less than 1 playerStrats");

        // Sort best → worst
        playerStrats.sort(Comparator.comparingDouble(this::winRate).reversed());

        // Keep all but the bottom 10 (or at least 1)
        int replaceCount = Math.min(10, playerStrats.size() - 1);
        List<PlayerStrat> survivors = new ArrayList<>(playerStrats.subList(0, playerStrats.size() - replaceCount));

        Random rng = ThreadLocalRandom.current();
        ArrayList<PlayerStrat> nextGen = new ArrayList<>(playerStrats.size());

        // Add survivors unchanged
        nextGen.addAll(survivors);

        // Replace the bottom N with mutated random survivors
        for (int i = 0; i < replaceCount; i++) {
            PlayerStrat parent = survivors.get(i);
            nextGen.add(mutate(parent));
        }

        playerStrats = nextGen;
        gamesPlayed.clear();
        gamesWon.clear();
        assert playerStrats.size() == concurrentStrats;
    }



    private PlayerStrat mutate(PlayerStrat parent) {
        var rnd = ThreadLocalRandom.current();

        // mutate propertyValues
        var newProps = new ArrayList<PropertyValue>(parent.propertyValues().size());
        for (var pv : parent.propertyValues()) {
            int delta  = rnd.nextInt(-20, 21);              // [-20, 20]
            int newVal = Math.max(0, pv.value() + delta);   // keep ≥ 0
            newProps.add(new PropertyValue(pv.name(), pv.index(), newVal, pv.normalValue()));
        }

        // mutate setValues (Gaussian step around current value, clamp at 0)
        var newSets = new HashMap<PropertySet, Float>();
        parent.setValues().forEach((set, base) -> {
            float delta   = (float) (rnd.nextGaussian() * 0.1);  // small symmetric wobble
            float mutated = base + delta;
            if (mutated < 0f) mutated = 0f;
            newSets.put(set, mutated);
        });

        // mutate playSafeThreshold a bit, clamp at 0
        float newThresh = (float) (parent.playSafeThreshold() + rnd.nextGaussian() * 0.05);
        if (newThresh < 0f) newThresh = 0f;

        return new PlayerStrat(newProps, newSets, newThresh);
    }


    public double winRate(PlayerStrat ps) {
        double played = gamesPlayed.getOrDefault(ps, 0);
        double won    = gamesWon.getOrDefault(ps, 0);

        if (played == 0) return 0.0;   // avoid divide-by-zero
        return (double) won / played;  // proper double division
    }

    public double averageWinRate(){
        sortByWinRate();
        return winRate(playerStrats.get(playerStrats.size()/2));
    }

    public void runGameWithRandomStrats(){
        List<PlayerStrat> currentGameStrats = new ArrayList<PlayerStrat>(playerStrats);
        Collections.shuffle(currentGameStrats);
        currentGameStrats = currentGameStrats.subList(0, 4);
        currentGameStrats.forEach(ps ->
                gamesPlayed.merge(ps, 1, Integer::sum)
        );
        Game g = runGame(currentGameStrats);
        if(g.players().size() > 1) numGamesDrawn++;
        Player winner = g.getPlayerWithMostMoney();
        gamesWon.merge(winner.getStrat(), 1, Integer::sum);
    }

    public Game runGame(List<PlayerStrat> currentGameStrats) {
        Game game = new Game(currentGameStrats);
        int roundNum = 0;
        while (!game.gameOver() && roundNum < 1000) {
            game.playRound();
            roundNum++;
        }
        return game;
    }

    public static ArrayList<PlayerStrat> initPlayerStrats(int num){
        ArrayList<PlayerStrat> strats = new ArrayList<PlayerStrat>();
        for(int i = 0; i < num; i++){
            strats.add(new PlayerStrat(initRandomPropertyValues(), initRandomSetValues(), 1));
        }
        return strats;
    }

    private static ArrayList<PropertyValue> initRandomPropertyValues() {
        ArrayList<PropertyValue> list = new ArrayList<PropertyValue>();
        for(int i = 0; i < Board.SIZE; i++) {
            int value = Math.max(0, Board.PRICES[i] + (int) (Math.random() * 100 - 25));
            list.add(new PropertyValue(Board.NAMES[i], i, value, Board.PRICES[i]));
        }
        return list;
    }

    private static HashMap<PropertySet, Float> initRandomSetValues() {
        var rnd = ThreadLocalRandom.current();
        var map = new HashMap<PropertySet, Float>();

        for (PropertySet set : PropertySet.values()) {
            // start near 1 with some spread, truncated at 0
            float value = (float) Math.max(0.0, rnd.nextGaussian() * 0.2 + 1);
            map.put(set, value);
        }
        return map;
    }

    public HashMap<PlayerStrat, Integer> getGamesPlayed() {
        return gamesPlayed;
    }

    public HashMap<PlayerStrat, Integer> getGamesWon() {
        return gamesWon;
    }

    public int getNumGamesDrawn(){
        return numGamesDrawn;
    }

    public float evaluateWinner(){
        int testNumber = 100;
        int wins = 0;
        sortByWinRate();
        PlayerStrat currentBestStrat = playerStrats.getFirst();
        for(int i = 0; i < testNumber; i++) {
            Game g = runGame(List.of(currentBestStrat,
                    new PlayerStrat(initRandomPropertyValues(), initRandomSetValues(), Math.max(0, (int) (ThreadLocalRandom.current().nextGaussian()*0.1 + 1))),
                    new PlayerStrat(initRandomPropertyValues(), initRandomSetValues(), Math.max(0, (int) (ThreadLocalRandom.current().nextGaussian()*0.1 + 1))),
                    new PlayerStrat(initRandomPropertyValues(), initRandomSetValues(), Math.max(0, (int) (ThreadLocalRandom.current().nextGaussian()*0.1 + 1)))));
            if(g.players().getFirst().getStrat().equals(currentBestStrat)) wins++;
            if(g.players().size() != 1){
                wins--;
                testNumber--;
            }
        }
        return ((float)wins)/testNumber;
    }
}
