package s260481943;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import halma.CCBoard;
import halma.CCMove;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class s260481943Player extends Player {

    // Destination corner
    private Point destinationPoint[];

    // The 4 corners of the game where players start
    private final Point BL;
    private final Point BR;
    private final Point TL;
    private final Point TR;

    // Random number generator
    private Random rand;

    /**
     * Initializes the player with the default name 260481943.
     */
    public s260481943Player() {
        this("260481943");
    }

    /**
     * Initializes the player.
     *
     * @param s Name of player
     */
    public s260481943Player(String s) {
        super(s);
        this.rand = new Random();
        this.TR = new Point(0, 15);
        this.BL = new Point(15, 0);
        this.BR = new Point(15, 15);
        this.TL = new Point(0, 0);
        this.destinationPoint = new Point[]{this.BR, this.TR, this.BL, this.TL};

    }

    @Override
    /**
     * Create the game board.
     */
    public Board createBoard() {
        return new CCBoard();
    }

    @Override
    /**
     * Chooses the best move to make based on MonteCarlo within at most one
     * second.
     */
    public Move chooseMove(Board theboard) {

        CCBoard board = (CCBoard) theboard;
        // Get the list of moves we can make

        System.out.println("# turns: " + board.getTurnsPlayed());
        return getBestMove(board);

    }

    private CCMove getBestMove(CCBoard board) {
        ResultList evaluations = new ResultList();
        ArrayList<CCMove> moves = board.getLegalMoves();

        // Look at all possible moves
        for (CCMove move : moves) {
            evaluations.addMove(move, scoreMove(move));
        }
        return evaluations.getBest();
    }

    private int scoreMove(CCMove move) {
        int score = 0;
        // Evaluate only if move is not end turn
        if (move.getTo() != null) {
            // Add a score based on the distance reduction a move acheives
            // This will give points to moves that go forward and penalize
            // those that go backward
            score += (move.getFrom().distance(destinationPoint[move.getPlayerID()]) - move.getTo().distance(destinationPoint[move.getPlayerID()]));
            // If moves *do* move forward...
            if (move.getFrom().distance(destinationPoint[move.getPlayerID()]) > move.getTo().distance(destinationPoint[move.getPlayerID()])) {
                // Add score if its a hop.  The code above will take care of
                // the fact that it reduces the distance by 2 instead of 1 tile
                // but often hops can be done multiple times so this is extra
                // incentive
                if (move.isHop()) {
                    score += 2;
                }
                // Add an incentive to move far away pieces (in the start zone)
                // as the game progresses, hopefully before 100 turns
                score += move.getTo().distance(destinationPoint[move.getPlayerID()]) / 8;
            }
        }
        return score;
    }
}
