/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s260481943;

import halma.CCMove;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author michael
 */



public class ResultList {
    
    private final HashMap<CCMove, Integer> storage;

    static class theLock extends Object {
    }
    static private final theLock lockObject = new theLock();

    public ResultList() {
        storage = new HashMap<>();
    }

    public void addMove(CCMove move, int status) {

        Integer statusObj = (Integer) status;
        // Item already exists, modify entry
        synchronized (lockObject) {
            if (storage.containsKey(move)) {
                storage.put(move, storage.get(move) + status);
            } else {
                storage.put(move, statusObj);
            }
        }
    }

    public CCMove getBest() {
        int maxValue = Integer.MIN_VALUE;
        CCMove bestMove = null;
        synchronized (lockObject) {
            Set<Map.Entry<CCMove, Integer>> set = storage.entrySet();
            for (Map.Entry<CCMove, Integer> map : set) {
                if (map.getValue() > maxValue) {
                    maxValue = map.getValue();
                    bestMove = map.getKey();
                }
            }
            return bestMove;
        }
    }

}
