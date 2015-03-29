package telecomclient;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelecomClient {
    // Client configuration file
    private static final String PROPERTY_FILE = "client-config.properties";

    // Misc. Utilities
    private static final ExecutorService execService = Executors.newCachedThreadPool();
    private static final Logger logger = Logger.getLogger(TelecomClient.class.getName());

    public static void main(String[] args) throws IOException {
        // Load client configuration settings
        Properties clientConfig = loadClientConfig();

        // Create request packet
        String type = clientConfig.getProperty("traffic");
        String activate = clientConfig.getProperty("useleakybucket");
        RequestPacket request = new RequestPacket(type, activate);

        ConnectionManager manager = new ConnectionManager(clientConfig, execService);
        manager.sendAllPackets(request);

        
        /*
        // TODO code application logic here
        Socket socket = new Socket("localhost", 9912);
        BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream()), StandardCharsets.UTF_8));
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        byte type = 1;
        byte activate = 1;
        out.write(type);
        out.write(activate);
        out.flush();
        String l;
            while ((l = in.readLine()) != null) {
                System.out.println(l);
            }
            */
    }

    private static Properties createDefaultConfig() {
        Properties defaultConfig = new Properties();
        defaultConfig.setProperty("server", "localhost");
        defaultConfig.setProperty("port", common.Common.SERVER_PORT);
        defaultConfig.setProperty("connections", "1");
        defaultConfig.setProperty("traffic", "constant");
        defaultConfig.setProperty("useleakybucket", "false");
        return defaultConfig;
    }

    private static Properties loadClientConfig() {
        Properties defaultConfig = createDefaultConfig();
        Properties clientConfig = new Properties(defaultConfig);
        try {
            try (FileInputStream in = new FileInputStream(PROPERTY_FILE)) {
                clientConfig.load(in);
                in.close();
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.WARNING, "Unable to load properties file {0}", ex.getLocalizedMessage());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Unable to load properties file {0}", ex.getLocalizedMessage());
        }

        return clientConfig;
    }
}
