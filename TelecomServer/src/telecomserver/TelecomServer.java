/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomserver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michael
 */
public class TelecomServer {

    public static String test = "NO REALLY";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean equals = test.equals( "fals");
        // TODO code application logic here
        System.out.println("Class : TelecomServer");
        try {
            Thread.sleep(30000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TelecomServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Done with TelecomServer");
    }

}
