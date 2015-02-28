package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;
import telecomlab3.User;

public class ExitCommand implements Command {

    private final String name = "exit";
    private final int argCount = 0;

    private static final Logger logger = Logger.getLogger(ExitCommand.class.getName());

    private CommHandler comm;
    private User user;

    public ExitCommand(CommHandler comm, User user) {
        this.comm = comm;
        this.user = user;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String[] arguments) {

        try {
            comm.sendMessage(new Message(Message.TYPE_EXIT, " "), null);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }

    @Override
    public int getArgCount() {
        return argCount;
    }

}
