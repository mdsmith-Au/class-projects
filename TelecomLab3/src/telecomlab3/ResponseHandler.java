/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3;

/**
 *
 * @author Michael
 */
public class ResponseHandler {
    
    public static void processResponse(Message msg) {
        System.out.println("Response: ");
        System.out.println(msg.toString());
    }
}
