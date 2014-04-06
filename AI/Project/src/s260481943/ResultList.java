package s260481943;

import halma.CCMove;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for storing moves and their result from MonteCarlo.
 *
 * @author Michael Smith
 */
public class ResultList {

    // Hashmap acts as storage
    private final HashMap<CCMove, int[]> storage;

    /**
     * Constructor creates the list
     */
    public ResultList() {
        storage = new HashMap<>();
    }

    /**
     * Add a move to the list with its corresponding value. If the move already
     * exists, the value will be added to the existing entry.
     *
     * @param move The move to be added or modified.
     * @param status The value associated with the given move.
     */
    public void addMove(CCMove move, int status) {

        int[] data = storage.get(move);

        // Item already exists, modify entry
        if (data != null) {
            data[0] = data[0] + status;
            data[1] = data[1]++;
            storage.put(move, data);
        } // New entry
        else {
            storage.put(move, new int[]{status, 1});
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
        Set<Map.Entry<CCMove, int[]>> set = storage.entrySet();
        // Check the value of each entry and compare to the max. so far
        for (Map.Entry<CCMove, int[]> map : set) {
            int[] data = map.getValue();
            int value = data[0] / data[1];
            if (value > maxValue) {
                // This value is better; we have a new best move
                maxValue = value;
                bestMove = map.getKey();
            }
        }
        return bestMove;
    }
}
