public interface Card {
    String getText();

    /** Each card defines its own effect */
    void apply(Game game, Player player);

    default void advanceToSquare(Game game, Player player, int targetIndex){
        if(player.pos() == targetIndex) return;
        player.move((targetIndex - player.pos() + Board.SIZE) % Board.SIZE, game.board().board);
    }

    default void advanceToNearestSet(Game game, Player player, PropertySet set){
        int moveAmount = 0;
        while(!game.board().board[(player.pos() + moveAmount) % Board.SIZE].info.set.equals(set)){
            if(moveAmount >= Board.SIZE){throw new IllegalStateException("can't find property of set : " + set.name + " on the board");}
            moveAmount++;
        }
        player.move(moveAmount, game.board().board);
    }
}
