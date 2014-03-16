
import s260481943.s260481943Player;
import boardgame.Player;
import boardgame.Client;
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
        client.run();
    }
    
}
