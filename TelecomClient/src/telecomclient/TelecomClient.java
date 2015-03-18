/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomclient;

import java.util.logging.Level;
import java.util.logging.Logger;
import telecomserver.TelecomServer;
/**
 *
 * @author michael
 */
public class TelecomClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // TODO code application logic here
        System.out.println("Class : TelecomClient");
        System.out.println("Telecom server var: " + TelecomServer.test);
        try {
            Thread.sleep(30000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TelecomClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Done with TelecomClient");
    }

}
