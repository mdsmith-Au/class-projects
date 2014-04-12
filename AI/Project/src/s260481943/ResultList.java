package s260481943;

import halma.CCMove;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for storing moves and their results from an AI algorithm
 *
 * @author Michael Smith
 */
public class ResultList {

    // Hashmap acts as storage
    private final HashMap<CCMove, Integer> storage;

    /**
     * Constructor creates the list
     */
    public ResultList() {
        storage = new HashMap<>();
    }

    /**
     * Add a move to the list with its corresponding value. If the move already
     * exists, the value will be added to that of the existing entry.
     *
     * @param move The move to be added or modified.
     * @param status The value associated with the given move.
     */
    public void addMove(CCMove move, int status) {

        Integer data = storage.get(move);

        // Item already exists, modify entry
        if (data != null) {
            storage.put(move, data + status);
        } // New entry
        else {
            storage.put(move, (Integer)status);
        }
    }

    /**
     * Get the best move so far.
     *
     * @return The move with the greatest value.
     */
    public CCMove getBest() {
        int maxValue = Integer.MIN_VALUE;
        CCMove bestMove = null;

        // Get the hashmap as a set so we can work with it
        Set<Map.Entry<CCMove, Integer>> set = storage.entrySet();
        // Check the value of each entry and compare to the max. so far
        for (Map.Entry<CCMove, Integer> map : set) {
            int value = map.getValue();
            if (value > maxValue) {
                // This value is better; we have a new best move
                maxValue = value;
                bestMove = map.getKey();
            }
        }
        return bestMove;
    }
}
