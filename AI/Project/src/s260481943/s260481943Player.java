package s260481943;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import halma.CCBoard;
import halma.CCMove;
import java.awt.Point;
import java.util.ArrayList;

public class s260481943Player extends Player {

    // Destination corner
    private Point destinationPoint[];

    // The 4 corners of the game where players start
    private final Point BL;
    private final Point BR;
    private final Point TL;
    private final Point TR;

    // Last played move
    private CCMove lastMove;

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
        this.TR = new Point(0, 15);
        this.BL = new Point(15, 0);
        this.BR = new Point(15, 15);
        this.TL = new Point(0, 0);
        this.destinationPoint = new Point[]{this.BR, this.TR, this.BL, this.TL};
        this.lastMove = new CCMove(0,new Point(0,0), new Point(0,0));
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
     * Chooses the best move to make based on a Best-First search.
     */
    public Move chooseMove(Board theboard) {

        // Cast the board to correct type
        CCBoard board = (CCBoard) theboard;

        CCMove bestMove = getBestMove(board);
        lastMove = bestMove;
        return bestMove;

    }

    /**
     * Returns the best possible move for the board state.  Based on
     * a Best-First Search algorithm with a custom heuristic.
     * @param board The game board.
     * @return The best move to take.
     */
    private CCMove getBestMove(CCBoard board) {
        // Init data structure, get legal moves
        ResultList evaluations = new ResultList();
        ArrayList<CCMove> moves = board.getLegalMoves();

        // Look at all possible moves, evaluate them, keep track of their scores
        for (CCMove move : moves) {
            evaluations.addMove(move, scoreMove(move));
        }
        return evaluations.getBest();
    }

    /**
     * The heuristic for Best-First search.  Evaluates primarily based on distance
     * but also includes parameters such as whether or not the move is a hop.
     * @param move The move to consider.
     * @return A score for the move in question.
     */
    private int scoreMove(CCMove move) {
        int score = 0;
        // Evaluate only if move is not end turn (if it is, the evaluation is 0)
        if (move.getTo() != null) {
            // Add a score based on the distance reduction a move acheives
            // This will give points to moves that go forward and penalize
            // those that go backward
            score += (move.getFrom().distance(destinationPoint[move.getPlayerID()]) - move.getTo().distance(destinationPoint[move.getPlayerID()]));
            //  Penalize moves that undo the last move
            if (move.getTo().equals(lastMove.getFrom())) {
                score -= 2;
            }

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
