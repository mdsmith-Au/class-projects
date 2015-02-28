package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.Callback;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;
import telecomlab3.User;

// **************************************************
// NOTE : FOR DEBUG ONLY. NORMAL USE WILL POLL
public class QueryMessageCommand implements Command, Callback {

    private final String name = "queryMsg";
    private final int argCount = 0;

    private static final Logger logger = Logger.getLogger(QueryMessageCommand.class.getName());

    private final CommHandler comm;
    private User user;

    public QueryMessageCommand(CommHandler comm, User user) {
        this.comm = comm;
        this.user = user;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String[] arguments) {

            if (user != null && user.getLoginState()) {
                queryMessages();
            } else {
                System.out.println("Error: user not logged in.");
            }
    }

    @Override
    public int getArgCount() {
        return argCount;
    }

    @Override
    public void handleResponse(Message msg) {
        if (msg.getType() == Message.TYPE_QUERY_MSG) {
            if (msg.getSubType() == Message.SUBTYPE_QUERY_MSG_MESSAGES) {
                System.out.println("Message received:");
                //TODO: Parse thing and print nicely?
                System.out.println(msg.getDataAsString());
            } else if (msg.getSubType() == Message.SUBTYPE_QUERY_MSG_NO_MSG) {
                System.out.println("No messages available.");
            } else if (msg.getSubType() == Message.SUBTYPE_QUERY_MSG_NOT_LOG_IN) {
                System.out.println("Error: cannot query messages; user not logged in.");
            }
        }
    }
    
    private void queryMessages() {
        try {
            comm.sendMessage(new Message(Message.TYPE_QUERY_MSG, " "), this);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

}
