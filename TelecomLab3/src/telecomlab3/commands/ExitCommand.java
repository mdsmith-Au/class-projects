package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;
import telecomlab3.User;

/**
 * Terminates the application.
 */
public class ExitCommand implements Command {

    private final String name = "exit";
    private final int argCount = 0;

    private static final Logger logger = Logger.getLogger(ExitCommand.class.getName());

    private final CommHandler comm;

    /**
     * Initializes the command.
     *
     * @param comm The {@link CommHandler CommHanlder} to use when sending
     * messages.
     */
    public ExitCommand(CommHandler comm) {
        this.comm = comm;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String arguments) {
        try {
            // We send a string with a space (not an emtpy string) because
            // the server never responds if we send an empty string
            comm.sendMessage(new Message(Message.TYPE_EXIT, " "), null);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }

    // Note that there is no handler here because we don't wait for any server response.
    @Override
    public int getArgCount() {
        return argCount;
    }

}
