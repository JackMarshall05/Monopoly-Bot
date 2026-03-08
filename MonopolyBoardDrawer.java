import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;

public class MonopolyBoardDrawer extends JFrame {
    public static void createAndShow(Game g) {
        SwingUtilities.invokeLater(() -> { var f = new MonopolyBoardDrawer(g); f.setVisible(true); });
    }

    private final Game game;
    private final Timer timer;

    private static final Color[] PLAYERCOLOR = {
            new Color(220,20,60), new Color(0,102,204), new Color(0,155,72),
            new Color(255,165,0), new Color(153,102,204), new Color(60,60,60)
    };

    public MonopolyBoardDrawer(Game g) {
        super("Monopoly Board");
        this.game = g;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(new BoardPanel());
        setSize(1024, 1024);
        setLocationRelativeTo(null);

        // Refresh
        timer = new Timer(1, e -> { repaint(); });
        timer.start();
    }

    /* ======================= Drawing ======================= */
    private class BoardPanel extends JPanel {
        final int GRID=11, PAD=12;

        BoardPanel(){ setBackground(new Color(245,245,245)); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Live snapshot (unused here, but shows how you got them)
            Property[] board = game.board().board;
            java.util.List<Player> players = game.players();

            final int GRID = 11, PAD = 12;
            int size = Math.min(getWidth(), getHeight());
            int cell = size / GRID;
            int ox = (getWidth() - size) / 2;
            int oy = (getHeight() - size) / 2;

            // Outer frame
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect(ox, oy, cell * GRID, cell * GRID);

            // Perimeter tiles (40 cells)
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i < 40; i++) {
                g2.setColor(Color.BLACK);
                if(board[i].info != null && board[i].info.owner() != null){
                    g2.setColor(PLAYERCOLOR[board[i].info.owner().number-1]);
                }
                Point p = indexToGrid(i, GRID);
                int x = ox + p.x * cell;
                int y = oy + p.y * cell;
                g2.drawRect(x, y, cell, cell);
                g2.drawString(board[i].name, x, y + cell/6);
                if(board[i].info != null && board[i].info.owner() != null){
                    g2.drawString(board[i].info.houses() + "", x, y + cell/3);
                }
            }

            for(Player player : players){
                Point p = indexToGrid(player.pos(), GRID);

                int playerWidth = 20;
                int playerHeight = 20;
                int playerX = ox + p.x * cell + cell/2 - playerWidth/2;
                int playerY = oy + p.y * cell + cell/2 - playerHeight/2;

                g2.setColor(PLAYERCOLOR[player.number-1]);
                g2.fillOval(playerX, playerY-5, playerWidth, playerHeight);
            }

            g2.dispose();
        }

        /** Map tile index (0..39) to perimeter grid coord (0..10,0..10). */
        private Point indexToGrid(int idx, int GRID) {
            int last = GRID - 1;
            if (idx == 0) return new Point(last, last);                 // GO
            if (idx > 0 && idx < 10) return new Point(last - idx, last);// bottom row → left
            if (idx == 10) return new Point(0, last);                   // Jail
            if (idx > 10 && idx < 20) return new Point(0, last - (idx - 10)); // left col → up
            if (idx == 20) return new Point(0, 0);                      // Free Parking
            if (idx > 20 && idx < 30) return new Point(idx - 20, 0);    // top row → right
            if (idx == 30) return new Point(last, 0);                   // Go To Jail
            return new Point(last, idx - 30);                           // right col → down
        }

        private Color setColor(PropertySet s){
            return switch (s){
                case BROWN -> new Color(113,63,18);
                case LIGHT_BLUE -> new Color(153,204,255);
                case PINK -> new Color(255,105,180);
                case ORANGE -> new Color(255,165,0);
                case RED -> new Color(220,20,60);
                case YELLOW -> new Color(255,215,0);
                case GREEN -> new Color(0,155,72);
                case DARK_BLUE -> new Color(0,51,153);
                case STATIONS -> new Color(60,60,60);
                case UTILITIES -> new Color(180,180,180);
                default -> new Color(220,220,220);
            };
        }
    }
}
