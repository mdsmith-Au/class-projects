/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author michael
 */
public class TelecomClient {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        // TODO code application logic here
        Socket socket = new Socket("localhost", 9912);
        BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream()), StandardCharsets.UTF_8));
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        byte type = 1;
        byte activate = 0;
        out.write(type);
        out.write(activate);
        out.flush();
        String l;
            while ((l = in.readLine()) != null) {
                System.out.println(l);
            }
    }

}
