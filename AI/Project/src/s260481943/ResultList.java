package s260481943;

import halma.CCMove;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for storing moves and their result from MonteCarlo.
 * @author Michael Smith
 */
public class ResultList {
    
    // Hashmap acts as storage
    private final HashMap<CCMove, Integer> storage;

    // A lock to control concurrent access
    static class theLock extends Object {
    }
    static private final theLock lockObject = new theLock();

    /**
     * Constructor creates the list
     */
    public ResultList() {
        storage = new HashMap<>();
    }

    /**
     * Add a move to the list with its corresponding value.  If the move 
     * already exists, the value will be added to the existing entry.
     * @param move The move to be added or modified.
     * @param status The value associated with the given move.
     */
    public void addMove(CCMove move, int status) {

        Integer statusObj = (Integer) status;
        
        // Synchronized is used to prevent concurrent access issues
        synchronized (lockObject) {
            // Item already exists, modify entry
            if (storage.containsKey(move)) {
                storage.put(move, storage.get(move) + status);
            } 
            // New entry
            else {
                storage.put(move, statusObj);
            }
        }
    }

    /**
     * Get the best move so far.
     * @return The move with the greatest value.
     */
    public CCMove getBest() {
        int maxValue = Integer.MIN_VALUE;
        CCMove bestMove = null;
        
        // Prevent concurrent access
        synchronized (lockObject) {
            // Get the hashmap as a set so we can work with it
            Set<Map.Entry<CCMove, Integer>> set = storage.entrySet();
            // Check the value of each entry and compare to the max. so far
            for (Map.Entry<CCMove, Integer> map : set) {
                if (map.getValue() > maxValue) {
                    // This value is better; we have a new best move
                    maxValue = map.getValue();
                    bestMove = map.getKey();
                }
            }
            return bestMove;
        }
    }

}
