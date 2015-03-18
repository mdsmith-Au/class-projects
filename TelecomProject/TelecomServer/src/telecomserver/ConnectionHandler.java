/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomserver;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles connections from clients.
 * @author michael
 */
public class ConnectionHandler implements Runnable {
    
    private final Socket connection;
    private final String data;
    
    private static final Logger log = Logger.getLogger(ConnectionHandler.class.getName());
    
    // For debugging via web browser
    private static final String CRLF = "\r\n";
    private static final String HTTP_HEADER = "HTTP/1.1 200" + CRLF + "Content-type: " + "text; charset=UTF-8" + CRLF + CRLF;
    
    public ConnectionHandler(String data, Socket connection) {
        this.connection = connection;
        this.data = data;
    }

    @Override
    public void run() {
        // Give thread a useful name based on connection
        // Note that Netbeans may continue to show this name even if it changes
        Thread thr = Thread.currentThread();
        thr.setName("connection-" + connection.getPort());
        System.out.println("Connected!");
        try {
            try (BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream())) {
                out.write(HTTP_HEADER.getBytes(StandardCharsets.US_ASCII));
                out.write(data.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
    }
}
