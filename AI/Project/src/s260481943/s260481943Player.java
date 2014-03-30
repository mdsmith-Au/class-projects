package s260481943;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import halma.CCBoard;
import halma.CCMove;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Random;

/**
 * Minimax halma player.
 * 
 */

public class s260481943Player extends Player{

    
    private Polygon home;
    private Point destinationPoint;
//    private Point destinationPointEnemy1;
//    private Point destinationPointEnemy2;

    private int[] xCoord_BL = {0, 0, 1, 2, 3, 3};
    private int[] yCoord_BL = {15, 12, 12, 13, 14, 15};
    private final Point BL;

    private int[] xCoord_BR = {15, 12, 12, 13, 14, 15};
    private int[] yCoord_BR = {15, 15, 14, 13, 12, 12};
    private final Point BR;

    private int[] xCoord_TL = {0, 3, 3, 2, 1, 0};
    private int[] yCoord_TL = {0, 0, 1, 2, 3, 3};
    private final Point TL;

    private int[] xCoord_TR = {15, 15, 14, 13, 12, 12};
    private int[] yCoord_TR = {0, 3, 3, 2, 1, 0};
    private final Point TR;
    
//    private int[] enemiesP0 = {1,2};
//    private int[] enemiesP1 = {0,3};
//    private int[] enemiesP2 = {0,3};
//    private int[] enemiesP3 = {1,2};
//    
//    private int[] enemies;
//    
//    private int allyP0 = 3;
//    private int allyP1 = 2;
//    private int allyP2 = 1;
//    private int allyP3 = 0;
//    
//    private int ally;
    
    private final int maxSimulations = 2000;
    private final long Timeout = 900;
    
    private Random rand;
    private boolean initialize = false;
    
    static class theLock extends Object {
    }
    static private final theLock lockObject = new theLock();

    /**
     * Provide a default public constructor
     */
    public s260481943Player() {
        this("260481943");
    }

    public s260481943Player(String s) {
        super(s);
        this.rand = new Random();
        this.TR = new Point(0,15);
        this.BL = new Point(15,0);
        this.BR = new Point(15,15);
        this.TL = new Point(0,0);

    }

    @Override
    public Board createBoard() { return new CCBoard(); }

    @Override
    public Move chooseMove(Board theboard) {
        final ResultList evaluations = new ResultList();
        final long time = System.currentTimeMillis();
        if (!initialize) {
            if (this.playerID == 0) {
                home = new Polygon(xCoord_TL, yCoord_TL, xCoord_TL.length);
//                ally = allyP0;
//                enemies = enemiesP0;
                destinationPoint = BR;
//                destinationPointEnemy1 = TR;
//                destinationPointEnemy2 = BL;
            } else if (this.playerID == 1) {
                home = new Polygon(xCoord_BL, yCoord_BL, xCoord_BL.length);
//                ally = allyP1;
//                enemies = enemiesP1;
                destinationPoint = TR;
//                destinationPointEnemy1 = BR;
//                destinationPointEnemy2 = TL;
            } else if (this.playerID == 2) {
                home = new Polygon(xCoord_TR, yCoord_TR, xCoord_TR.length);
//                ally = allyP2;
//                enemies = enemiesP2;
                destinationPoint = BL;
//                destinationPointEnemy1 = BR;
//                destinationPointEnemy2 = TL;
            } else if (this.playerID == 3) {
                home = new Polygon(xCoord_BL, yCoord_BL, xCoord_BL.length);
//                ally = allyP3;
//                enemies = enemiesP3;
                destinationPoint = TL;
//                destinationPointEnemy1 = TR;
//                destinationPointEnemy2 = BL;
            }
            initialize = true;
        }
        
        final CCBoard board = (CCBoard) theboard;
        final ArrayList<CCMove> moves = getMovesForThisPlayerOnly(board);
        
        class monteCarloThread implements Runnable{

            @Override
            public void run() {
                monteCarlo(board, evaluations, moves, time);
            }
            
        };
        
        Thread thread1 = new Thread(new monteCarloThread());
        Thread thread2 = new Thread(new monteCarloThread());
        Thread thread3 = new Thread(new monteCarloThread());
        Thread thread4 = new Thread(new monteCarloThread());
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.run();

        return evaluations.getBest();
        
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
    
    private void monteCarlo(CCBoard board, ResultList evaluations, ArrayList<CCMove> legalMoves, long startTime) { 
        for (int i = 0; i < maxSimulations; i++) {

            CCBoard board2 = (CCBoard) board.clone();
            CCMove randomMove = null;
            // Chose random move
            synchronized (lockObject) {
                randomMove = legalMoves.get(rand.nextInt(legalMoves.size()));
                while (!validateMove(randomMove)) {
                    legalMoves.remove(randomMove);
                    randomMove = legalMoves.get(rand.nextInt(legalMoves.size()));
                }

            }
            
            board2.move(randomMove);
            
            // play out game until end
            while(board2.getWinner() == CCBoard.NOBODY) {
                ArrayList<CCMove> moveList = board2.getLegalMoves();
                board2.move(moveList.get(rand.nextInt(moveList.size())));
            }
            int eval = 0;
            if (randomMove.isHop()) {
                eval = 1;
            }
            if (randomMove.getFrom() != null && randomMove.getFrom().distance(destinationPoint) > 15) {
                eval+=2;
            }
            // game over; we won
            if (board2.getWinner() == this.playerID) {
                evaluations.addMove(randomMove, eval + 3);
            }
            // we lost
            else {
                evaluations.addMove(randomMove, eval - 3);
            }
            if (System.currentTimeMillis() - startTime > Timeout){
                break;
            }
        }
    }
    
    
    private boolean validateMove(CCMove move) {
        if (move.getFrom() == null || move.getTo() == null) {
            return true;
        } else return move.getFrom().distance(destinationPoint) > (move.getTo().distance(destinationPoint));
    }


    /*
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
    }*/

}
