
import boardgame.Client;
import boardgame.Player;
import halma.CCRandomPlayer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        
        new Thread(new boardgame.Server(new halma.CCBoard(), true, true, 8123, 1000)).start();
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Player player = new s260481943Player();
        Client client = new Client(player, "localhost", 8123);
        new Thread(client).start();
        
        for (int i = 0; i < 3; i++) {
            new Thread(new Client(new CCRandomPlayer("Rand " + i), "localhost", 8123)).start();
        }
        

        
    }
}



