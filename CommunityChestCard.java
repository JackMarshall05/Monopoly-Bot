public enum CommunityChestCard implements Card{
        ADVANCE_TO_GO("Advance to Go. Collect £200.") {
                @Override
                public void apply(Game game, Player player) {
                        // Move to index 0 ("Go"); award handled by player/board logic when passing/landing on Go
                        advanceToSquare(game, player, 0);
                }
        },

        BANK_ERROR("Bank error in your favour. Collect £200.") {
                @Override
                public void apply(Game game, Player player) {
                        player.receiveAmount(200);
                }
        },

        DOCTORS_FEE("Doctor’s fee. Pay £50.") {
                @Override
                public void apply(Game game, Player player) {
                        player.payAmount(50);
                }
        },

        SALE_OF_STOCK("From sale of stock you get £50.") {
                @Override
                public void apply(Game game, Player player) {
                        player.receiveAmount(50);
                }
        },

        GET_OUT_OF_JAIL_FREE("Get Out of Jail Free.") {
                @Override
                public void apply(Game game, Player player) {
                        //TODO implement get out of jail free card
                }
        },

        GO_TO_JAIL("Go to Jail. Go directly to Jail. Do not pass Go. Do not collect £200.") {
                @Override
                public void apply(Game game, Player player) {
                        player.goToJail();
                }
        },

        HOLIDAY_FUND_MATURES("Holiday fund matures. Receive £100.") {
                @Override
                public void apply(Game game, Player player) {
                        player.receiveAmount(100);
                }
        },

        INCOME_TAX_REFUND("Income tax refund. Collect £20.") {
                @Override
                public void apply(Game game, Player player) {
                        player.receiveAmount(20);
                }
        },

        ITS_YOUR_BIRTHDAY("It is your birthday. Collect £10 from every player.") {
                @Override
                public void apply(Game game, Player player) {
                        collectFromEachOtherPlayer(game, player, 10);
                }
        },

        LIFE_INSURANCE_MATURES("Life insurance matures. Collect £100.") {
                @Override
                public void apply(Game game, Player player) {
                        player.receiveAmount(100);
                }
        },

        PAY_HOSPITAL_FEES("Pay hospital fees of £100.") {
                @Override
                public void apply(Game game, Player player) {
                        player.payAmount(100);
                }
        },

        PAY_SCHOOL_FEES("Pay school fees of £50.") {
                @Override
                public void apply(Game game, Player player) {
                        player.payAmount(50);
                }
        },

        RECEIVE_CONSULTANCY_FEE("Receive £25 consultancy fee.") {
                @Override
                public void apply(Game game, Player player) {
                        player.receiveAmount(25);
                }
        },

        STREET_REPAIRS("You are assessed for street repairs. £40 per house. £115 per hotel.") {
                @Override
                public void apply(Game game, Player player) {
                        //TODO implement street repairs
                }
        },

        BEAUTY_CONTEST_PRIZE("You have won second prize in a beauty contest. Collect £10.") {
                @Override
                public void apply(Game game, Player player) {
                        player.receiveAmount(10);
                }
        },

        INHERITANCE("You inherit £100.") {
                @Override
                public void apply(Game game, Player player) {
                        player.receiveAmount(100);
                }
        };

        private final String text;

        CommunityChestCard(String text) {
                this.text = text;
        }

        public String getText() {
                return text;
        }

        /** Each card defines its own effect */
        public abstract void apply(Game game, Player player);

        // --- Helpers (mirroring ChanceCard style) ---

        public void advanceToSquare(Game game, Player player, int targetIndex) {
                if (player.pos() == targetIndex) return;
                // Assumes Board.SIZE is the number of squares; move() handles pass-Go effects
                player.move((targetIndex - player.pos() + Board.SIZE) % Board.SIZE, game.board().board);
        }

        public void collectFromEachOtherPlayer(Game game, Player collector, int amountPerPlayer) {
                for (Player p : game.players()) {
                        if (p != collector) {
                                p.payAmount(amountPerPlayer);
                                collector.receiveAmount(amountPerPlayer);
                        }
                }
        }
}
