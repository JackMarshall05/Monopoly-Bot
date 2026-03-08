public enum ChanceCard implements Card{
    ADVANCE_TO_GO("Advance to Go. Collect £200.") {
        @Override
        public void apply(Game game, Player player) { advanceToSquare(game, player, 0);
        }
    },

    ADVANCE_TO_TRAFALGAR_SQUARE("Advance to Trafalgar Square. If you pass Go, collect £200.") {
        @Override
        public void apply(Game game, Player player) {
            advanceToSquare(game, player, 24);
        }
    },

    ADVANCE_TO_MAYFAIR("Advance to Mayfair.") {
        @Override
        public void apply(Game game, Player player) {
            advanceToSquare(game, player, 39);
        }
    },

    ADVANCE_TO_PALL_MALL("Advance to Pall Mall. If you pass Go, collect £200.") {
        @Override
        public void apply(Game game, Player player) {
            advanceToSquare(game, player, 11);
        }
    },

    ADVANCE_TO_NEAREST_STATION("Advance to the nearest Station...") {
        @Override
        public void apply(Game game, Player player) {
            advanceToNearestSet(game, player, PropertySet.STATIONS);
        }
    },

    ADVANCE_TO_NEAREST_STATION_2("Advance to the nearest Station...") {
        @Override
        public void apply(Game game, Player player) {
            advanceToNearestSet(game, player, PropertySet.STATIONS);
        }
    },

    ADVANCE_TO_NEAREST_UTILITY("Advance token to the nearest Utility...") {
        @Override
        public void apply(Game game, Player player) {
            advanceToNearestSet(game, player, PropertySet.UTILITIES);
        }
    },

    BANK_PAYS_DIVIDEND("Bank pays you dividend of £50.") {
        @Override
        public void apply(Game game, Player player) {
            player.receiveAmount(50);
        }
    },

    GET_OUT_OF_JAIL_FREE("Get Out of Jail Free.") {
        @Override
        public void apply(Game game, Player player) {
            //TODO implement get out jail free card
        }
    },

    GO_BACK_THREE_SPACES("Go Back three spaces.") {
        @Override
        public void apply(Game game, Player player) {
            player.move(-3, game.board().board);
        }
    },

    GO_TO_JAIL("Go to Jail. Do not pass Go. Do not collect £200.") {
        @Override
        public void apply(Game game, Player player) {
            player.goToJail();
        }
    },

    GENERAL_REPAIRS("Make general repairs on all your property.") {
        @Override
        public void apply(Game game, Player player) {
            //ToDo implement general repairs card
        }
    },

    SPEEDING_FINE("Speeding fine £15.") {
        @Override
        public void apply(Game game, Player player) {
            player.payAmount(15);
        }
    },

    TAKE_A_TRIP_TO_KINGS_CROSS("Take a trip to King's Cross Station.") {
        @Override
        public void apply(Game game, Player player) {
            advanceToSquare(game, player, 5);
        }
    },

    ELECTED_CHAIRMAN_OF_THE_BOARD("You have been elected Chairman of the Board.") {
        @Override
        public void apply(Game game, Player player) {
            for (Player p : game.players()) {
                if (p != player) {
                    player.receiveAmount(50);
                    p.payAmount(50);
                }
            }
        }
    },

    BUILDING_LOAN_MATURES("Your building loan matures. Collect £150.") {
        @Override
        public void apply(Game game, Player player) {
            player.receiveAmount(150);
        }
    };

    private final String text;

    ChanceCard(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    /** Each card defines its own effect */
    public abstract void apply(Game game, Player player);

    public void advanceToSquare(Game game, Player player, int targetIndex){
        if(player.pos() == targetIndex) return;
        player.move((targetIndex - player.pos() + Board.SIZE) % Board.SIZE, game.board().board);
        assert player.pos() == targetIndex;
    }

    public void advanceToNearestSet(Game game, Player player, PropertySet set){
        int moveAmount = 0;
        while(true){
            if(game.board().board[(player.pos() + moveAmount) % Board.SIZE].info != null
                    && game.board().board[(player.pos() + moveAmount) % Board.SIZE].info.set != null
                    && game.board().board[(player.pos() + moveAmount) % Board.SIZE].info.set.equals(set)){
                break;
            }
            if(moveAmount >= Board.SIZE){throw new IllegalStateException("can't find property of set : " + set.name + " on the board");}
            moveAmount++;
        }
        player.move(moveAmount, game.board().board);
        assert game.board().board[player.pos()].info.set.equals(set);
    }
}

