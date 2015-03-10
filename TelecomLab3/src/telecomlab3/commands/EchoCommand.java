package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.Callback;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;

/**
 * Represents the echo command. Server will respond with contents of message
 * sent.
 */
public class EchoCommand implements Command, Callback {

    private final String name = "echo";
    private final int argCount = 1;

    private static final Logger logger = Logger.getLogger(EchoCommand.class.getName());

    private final CommHandler comm;

    /**
     * Initializes the command.
     *
     * @param comm The {@link CommHandler CommHanlder} to use when sending
     * messages.
     */
    public EchoCommand(CommHandler comm) {
        this.comm = comm;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    // Message always of type echo
    public void execute(String arguments) {
        String[] parsedArgs = arguments.split(",");

        if (parsedArgs.length != argCount) {
            System.out.println("Error: bad number of arguments.");
        } else {
            try {
                comm.sendMessage(new Message(Message.TYPE_ECHO, parsedArgs[0]), this);
            } catch (UnsupportedEncodingException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public int getArgCount() {
        return argCount;
    }

    @Override
    public void handleResponse(Message msg) {
        System.out.println(msg.toString());
    }
}
