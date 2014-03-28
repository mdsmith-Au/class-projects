/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s260481943;

import halma.CCMove;
import java.util.HashMap;

/**
 *
 * @author michael
 */



public class evalList {
    
    //private HashMap storage;
    private double bestEvaluation;
    private double worstEvaluation;
    private CCMove bestMove;
    private CCMove worstMove;
    
    public evalList() {
        //storage = new HashMap();
        bestEvaluation = Double.NEGATIVE_INFINITY;
        worstEvaluation = Double.POSITIVE_INFINITY;
        bestMove = null;
        worstMove = null;
    }
    
    public void addMove(double evaluation, CCMove move) {
        //storage.put((Double)evaluation, move);
        if (evaluation > bestEvaluation) {
            bestEvaluation = evaluation;
            bestMove = move;
        }
        if (evaluation < worstEvaluation) {
            worstEvaluation = evaluation;
            worstMove = move;
        }
    }
    
    public CCMove getBestMove() {
        return bestMove;
    }
    
    public double getBestEval() {
        return bestEvaluation;
    }
    
    public CCMove getWorstMove() {
        return worstMove;
    }
    
    public double getWorstEval() {
        return worstEvaluation;
    }
}
