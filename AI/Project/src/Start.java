
import boardgame.Client;
import boardgame.Player;
import halma.CCRandomPlayer;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import s260481943.s260481943Player;

/**
 * Class responsible for creating 4 AI clients for a server.
 *
 * @author Michael Smith
 */
public class Start {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        
//        PrintStream originalStream = System.out;
//        PrintStream dummyStream = new PrintStream(new OutputStream() {
//            public void write(int b) {
//                //NO-OP
//            }
//        });
//        
//        System.setOut(dummyStream);
        new Thread(new Client(new CCRandomPlayer("Enemy 1"), "localhost", 8123)).start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        new Thread(new Client(new s260481943Player("Michael"), "localhost", 8123)).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        new Thread(new Client(new s260481943Player("Ally 1"), "localhost", 8123)).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        new Thread(new Client(new CCRandomPlayer("Enemy 2"), "localhost", 8123)).start();

    }
}
