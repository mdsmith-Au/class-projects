
import boardgame.Client;
import boardgame.Player;
import halma.CCRandomPlayer;

import s260481943.s260481943Player;

/**
 *
 * @author michael
 */
public class Start {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Player player = new s260481943Player();
        Client client = new Client(player, "localhost", 8123);
        new Thread(client).start();
        
        for (int i = 0; i < 3; i++) {
            new Thread(new Client(new s260481943Player("Enemy " + i), "localhost", 8123)).start();
        }
        

        
    }
}



