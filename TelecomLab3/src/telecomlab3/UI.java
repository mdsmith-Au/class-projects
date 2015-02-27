/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Michael
 */
public class UI {
    
    public UI(ExecutorService executorService) {
        uiProcess ui = new uiProcess();
        executorService.submit(ui);
    }
    
    private static class uiProcess implements Runnable {
        public void run() {
            Scanner scanner = new Scanner(System.in).useDelimiter("\n");
            while (true) {
                // Execute command here
                String userInput = scanner.next();
                if (userInput.equals("exit") || userInput.equals("quit")) {
                    System.exit(0);
                }
                else {
                    System.out.println("Unknown command " + userInput);
                }
            }
        }
        
    }
}
