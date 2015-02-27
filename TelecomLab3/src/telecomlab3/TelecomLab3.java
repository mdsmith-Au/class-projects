/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author ECSE456
 */
public class TelecomLab3 {

    private static ExecutorService execServ = Executors.newCachedThreadPool();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UI ui = new UI(execServ);
        CommHandler comm = new CommHandler(execServ, "ecse-489.ece.mcgill.ca", 5000);
        comm.test();
    }
    
}
