package telecomclient;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main application start class for the client.
 * @author Kevin Dam
 */
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

        int numConnections = Integer.decode(clientConfig.getProperty("connections"));

        System.out.println("Opening " + numConnections + " connections");

        for (int i = 0; i < numConnections; i++) {
            ConnectionManager manager = new ConnectionManager(clientConfig, execService, request);
            execService.submit(manager);
        }
    }

    /**
     * Configures the default configuration for the client if a properties file is not provided.
     * @return the properties configuration file
     */
    private static Properties createDefaultConfig() {
        Properties defaultConfig = new Properties();
        defaultConfig.setProperty("server", "localhost");
        defaultConfig.setProperty("port", common.Common.SERVER_PORT);
        defaultConfig.setProperty("connections", "1");
        defaultConfig.setProperty("traffic", "constant");
        defaultConfig.setProperty("useleakybucket", "false");
        return defaultConfig;
    }

    /**
     * Reads the configuration property file, or uses the default if not found.
     * @return the properties configuration file
     */
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
