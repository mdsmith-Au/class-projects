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

/**
 * MonteCarlo-based Halma player. Uses a simplified version of the algorithm
 * that does not use a tree or minimax but instead uses randomness to play out
 * the game and get results for different moves. Implements a custom evaluation
 * function to reduce the number of moves that need to be checked as well as
 * determine their value.
 *
 * @author Michael Smith
 *
 */
public class s260481943Player extends Player {

    // Destination corner
    private Point destinationPoint[];

    // The 4 corners of the game where players start
    private final Point BL;
    private final Point BR;
    private final Point TL;
    private final Point TR;

    // Montecarlo parameters: maximum number of simulations and
    // approx. timeout in milliseconds if the maximum number of simulations
    // can't be reached
    private final int maxSimulations = 10000;
    private final long Timeout = 900;

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
        this.destinationPoint = new Point[] {this.BR, this.TR, this.BL, this.TL};

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
        final long time = System.currentTimeMillis();
        // Create a data structure to keep data from our MonteCarlo evaluations.
        final ResultList evaluations = new ResultList();
        // Get the current time to ensure we don't exceed one second.
        
        // Cast the board to a datatype we can work with
        CCBoard board = (CCBoard) theboard;
        // Get the list of moves (only) we can make
        ArrayList<CCMove> moves = board.getLegalMoves();

//        for (CCMove move: board.getLegalMoves()) {
//            System.out.println("Possib. Move: " + move.toPrettyString());
//            
//        }
        System.out.println("Turns: " + board.getTurnsPlayed());
        monteCarlo(board, evaluations, moves, time);
        // Start four MonteCarlo threads
        // The last thread blocks until its done
        // Otherwise, we risk returning a result before the algorithms have completed
        // Note that at this point some of the other threads may still be running
        // but they will at least have nearly finished so its not an issue
        CCMove move = evaluations.getBest();
        return move;

    }

    /**
     * Simplified MonteCarlo algorithm implementation. Uses randomness to play
     * out the game and get results for different moves instead of a minimax
     * tree with MonteCarlo at the end. Implements a custom evaluation function
     * to reduce the number of moves that need to be checked as well as
     * determine their value.
     *
     * @param board The game board.
     * @param evaluations Data structure where results are stored.
     * @param legalMoves List of legal moves for the current player.
     * @param startTime Time to use (in milliseconds) as the start of the
     * timeout.
     */
    private void monteCarlo(CCBoard board, ResultList evaluations, ArrayList<CCMove> legalMoves, long startTime) {

        // Execute a maximum number of simulations
        for (int i = 0; i < maxSimulations; i++) {

            // Clone the board so we can work on it
            CCBoard board2 = (CCBoard) board.clone();
            CCMove randomMove = null;

            // Chose random move
            // Get a move
            randomMove = legalMoves.get(rand.nextInt(legalMoves.size()));

            // Execute the random move chosen above
            board2.move(randomMove);

            // Play out the game until it finsihes with random moves for 
            // everyone except ourselves
            while (board2.getWinner() == CCBoard.NOBODY) {
                // Get possible moves
                ArrayList<CCMove> moveList = board2.getLegalMoves();
                // Get a random move
                CCMove randomMove2 = moveList.get(rand.nextInt(moveList.size()));
                board2.move(moveList.get(rand.nextInt(moveList.size())));
            }

            // Custom evaluation function:
            // (at this point the game is over)
            // |     Initial Move     |  Score |
            // _________________________________
            // |         Hop          |   +2   |
            // |  Piece in our corner |   +2   |
            // |     Victory          |   +3   |
            // |     Loss             |   -4   |
            // _________________________________
            int eval = 0;
//            // Victory
//            if (board2.getWinner() == this.playerID) {
//                if (randomMove.isHop()) {
//                    eval += 2;
//                }
//                if (randomMove.getFrom() != null && randomMove.getFrom().distance(destinationPoint[this.playerID]) > 10) {
//                    eval += 2;
//                }
//                evaluations.addMove(randomMove, eval + 5);
//            } // Loss
//            else {
//                evaluations.addMove(randomMove, eval - 7);
//            }
            HashSet<Point> basePoint = CCBoard.bases[randomMove.getPlayerID()];
//            if (basePoint.contains(randomMove.getTo()) && !basePoint.contains(randomMove.getFrom())){
//                eval--;
//            }
            if (basePoint.contains(randomMove.getFrom()) && !basePoint.contains(randomMove.getTo())) {
                eval++;
            }
            if (board2.getWinner() == this.playerID) {
                Point destination = destinationPoint[randomMove.getPlayerID()];
                
                if (randomMove.getFrom().distance(destination) > (randomMove.getTo().distance(destination) - 0.5)) {
                    eval++;
                }
                evaluations.addMove(randomMove, eval + 1);
            }
            else {
                evaluations.addMove(randomMove, eval -1);
            }
            // Timeout (stop any further simulations) if necessary
            if (System.currentTimeMillis() - startTime >= Timeout) {
                break;
            }
        }
    }

    /**
     * Checks whether a move is valid. Moves are valid if they are for the
     * current player and reduce the distance to the goal (as opposed to
     * increasing it).
     *
     * @param move The move to check
     * @return Whether the move is valid or not.
     */
    private boolean validateMoveDir(CCMove move) {
        return true;
//        if (move.getFrom() == null || move.getTo() == null) {
//            return true;
//        }
//        else {
//            Point destination = destinationPoint[move.getPlayerID()];
//            return move.getFrom().distance(destination) > (move.getTo().distance(destination) - 0.5);
//        }
    }
    
    private boolean validateMove(CCMove move) {
//        HashSet<Point> basePoint = CCBoard.bases[move.getPlayerID()];
//        return !(basePoint.contains(move.getTo()) && !basePoint.contains(move.getFrom()));
        return true;
    }

}
