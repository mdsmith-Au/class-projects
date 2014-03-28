package s260481943;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import halma.CCBoard;
import halma.CCMove;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

/**
 * Minimax halma player.
 * 
 */

public class s260481943Player extends Player {

    private Polygon destination;
    private Point destinationPoint;
    private Point destinationPointEnemy1;
    private Point destinationPointEnemy2;

    private int[] xCoord_BL = {0, 0, 1, 2, 3, 3};
    private int[] yCoord_BL = {15, 12, 12, 13, 14, 15};
    private Point BL = new Point(0,15);

    private int[] xCoord_BR = {15, 12, 12, 13, 14, 15};
    private int[] yCoord_BR = {15, 15, 14, 13, 12, 12};
    private Point BR = new Point(15,15);

    private int[] xCoord_TL = {0, 3, 3, 2, 1, 0};
    private int[] yCoord_TL = {0, 0, 1, 2, 3, 3};
    private Point TL = new Point(0,0);

    private int[] xCoord_TR = {15, 15, 14, 13, 12, 12};
    private int[] yCoord_TR = {0, 3, 3, 2, 1, 0};
    private Point TR = new Point(15,0);

    private boolean cornerCheckComplete = false;
    
    
    private int[] enemiesP0 = {1,2};
    private int[] enemiesP1 = {0,3};
    private int[] enemiesP2 = {0,3};
    private int[] enemiesP3 = {1,2};
    
    private int[] enemies;
    
    private int allyP0 = 3;
    private int allyP1 = 2;
    private int allyP2 = 1;
    private int allyP3 = 0;
    
    private int ally;
    
    private int maxTurns = 0;

    /**
     * Provide a default public constructor
     */
    public s260481943Player() {
        super("260481943");
        destination = new Polygon();
    }

    public s260481943Player(String s) {
        super(s);
        destination = new Polygon();
    }

    @Override
    public Board createBoard() { return new CCBoard(); }

    @Override
    public Move chooseMove(Board theboard) 
    {
        // Run a check (once) as to where we are
        if (!cornerCheckComplete) {
            if (this.playerID == 0) {
                destination = new Polygon(xCoord_BR, yCoord_BR, xCoord_BR.length);
                ally = allyP0;
                enemies = enemiesP0;
                destinationPoint = BR;
                destinationPointEnemy1 = TR;
                destinationPointEnemy2 = BL;
            } else if (this.playerID == 1) {
                destination = new Polygon(xCoord_TR, yCoord_TR, xCoord_TR.length);
                ally = allyP1;
                enemies = enemiesP1;
                destinationPoint = TR;
                destinationPointEnemy1 = BR;
                destinationPointEnemy2 = TL;
            } else if (this.playerID == 2) {
                destination = new Polygon(xCoord_BL, yCoord_BL, xCoord_BL.length);
                ally = allyP2;
                enemies = enemiesP2;
                destinationPoint = BL;
                destinationPointEnemy1 = BR;
                destinationPointEnemy2 = TL;
            } else if (this.playerID == 3) {
                destination = new Polygon(xCoord_TL, yCoord_TL, xCoord_TL.length);
                ally = allyP3;
                enemies = enemiesP3;
                destinationPoint = TL;
                destinationPointEnemy1 = TR;
                destinationPointEnemy2 = BL;
            }
            cornerCheckComplete = true;
        }

        // Cast the arguments to the objects we want to work with
        CCBoard board = (CCBoard) theboard;

        return minimaxDecision(board);
    }

    
    
    private CCMove minimaxDecision(CCBoard board) {

        evalList listOfValues = new evalList();
        ArrayList<CCMove> ourMoves = getMovesForThisPlayerOnly(board);

        for (CCMove move : ourMoves) {
            CCBoard board2 = (CCBoard)board.clone();
            board2.move(move);
            listOfValues.addMove(minimaxValue(board2), move);
        }
        return listOfValues.getBestMove();
    }
    
    private double minimaxValue(CCBoard board) {
        // if terminal, return utility
        if (isTerminal(board)) {
            // Give 1 for win, -1 for loss
            if (board.getWinner() == this.playerID) {
                return 1;
            }
            else if (board.getWinner() == CCBoard.DRAW) {
                return 0;
            }
            return -1;
        }
        //for each state s' in successors
        evalList listOfValues = new evalList();
        for (CCMove move : board.getLegalMoves()) {
            CCBoard board2 = (CCBoard)board.clone();
            board2.move(move);
            listOfValues.addMove(minimaxValue(board2), move);
        }
        // For player, max turn
        if (board.getTurn() == this.playerID || board.getTurn() == ally) {
            return listOfValues.getBestEval();
        }

        // Not us or ally : enemy
        return listOfValues.getWorstEval();
        
    }
 
    private ArrayList<CCMove> getMovesForThisPlayerOnly(CCBoard board) {
        // Make list of moves that are for us ONLY from list of legal moves
        ArrayList<CCMove> ourMoves = new ArrayList<>();
        for (CCMove entry : board.getLegalMoves()) {
            if (entry.getPlayerID() == this.playerID) {
                ourMoves.add(entry);
            }
        }
        return ourMoves;
    }

    private boolean isTerminal(CCBoard board) {
        if (board.getTurnsPlayed() > maxTurns) {
            // max turns hit; run a partial evaluation now to declare the winner
            evalPartialBoard(board);
            return true;
        } else {
            return board.getWinner() != CCBoard.NOBODY;
        }
    }

    
    private void evalPartialBoard(CCBoard board) {
        // Find winner of a partial board by finding distances of player pieces to dest
        ArrayList<Point> ourPieces = board.getPieces(this.playerID);
        ArrayList<Point> piecesEnemy1 = board.getPieces(enemies[0]);
        ArrayList<Point> piecesEnemy2 = board.getPieces(enemies[1]);
        
        int ourDist = 0;
        for (Point piece : ourPieces) {
            ourDist += piece.distance(destinationPoint);
        }
        
        int enemy1Dist = 0;
        for (Point piece : piecesEnemy1) {
            enemy1Dist += piece.distance(destinationPointEnemy1);
        }
        
        int enemy2Dist = 0;
        for (Point piece : piecesEnemy2) {
            enemy2Dist += piece.distance(destinationPointEnemy2);
        }

        // min distance to dest is us : good (count as win)
        if (Math.min(Math.min(ourDist, enemy1Dist), enemy2Dist) == ourDist) {
            board.forceWinner(this.playerID);
        } else {
            // Not us: loss
            // Note: force winner to enemy 1, but it doesn't really matter who
            board.forceWinner(enemies[0]);
        }
    }
}
