package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;

public class EchoCommand implements Command {
    private final String name = "echo";
    private final int argCount = 1;

    private static final Logger logger = Logger.getLogger(EchoCommand.class.getName());
    
    CommHandler comm;
    
    public EchoCommand(CommHandler comm) {
        this.comm = comm;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length != argCount) {
            // TODO
            System.out.println("Error: bad number of arguments.");
        }
        else {
            try {
                comm.sendMessage(new Message(Message.TYPE_ECHO, arguments[0]));
            } catch (UnsupportedEncodingException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

        // TODO will need to pass the arguments to create a message, which can
        // then be passed to the CommHandler
        
    }

    @Override
    public int getArgCount() {
        return argCount;
    }
}
