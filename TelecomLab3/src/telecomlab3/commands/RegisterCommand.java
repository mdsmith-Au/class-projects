package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.Callback;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;
import telecomlab3.User;

/**
 * Handles the register command. Creates a new user, logs in, and creates the
 * data store in that order.
 */
public class RegisterCommand implements Command, Callback {

    private final String name = "register";
    private final int argCount = 2;

    private static final Logger logger = Logger.getLogger(RegisterCommand.class.getName());

    private final CommHandler comm;
    private final User user;

    /**
     * Initializes the command.
     *
     * @param comm The {@link CommHandler CommHanlder} to use when sending
     * messages.
     * @param user The {@link User User} to use for representing the current
     * user.
     */
    public RegisterCommand(CommHandler comm, User user) {
        this.comm = comm;
        this.user = user;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String arguments) {
        String[] parsedArgs = arguments.split(",");
        if (parsedArgs.length != argCount) {
            System.out.println("Error: bad number of arguments.");
        } else {
            try {
                if (user != null && user.getLoginState()) {
                    System.out.println("Error: user already logged in.");
                } else {
                    user.setUsername(parsedArgs[0]);
                    user.setPassword(parsedArgs[1]);
                    comm.sendMessage(new Message(Message.TYPE_CREATE_USER, user.getUsername() + ',' + user.getPassword()), this);
                }

            } catch (UnsupportedEncodingException ex) {
                logger.log(Level.SEVERE, "Unable to register new user with username {0}", parsedArgs[0]);
            }

        }

    }

    @Override
    public int getArgCount() {
        return argCount;
    }

    @Override
    public void handleResponse(Message msg) {
        if (msg.getType() == Message.TYPE_CREATE_USER) {
            if (msg.getSubType() == Message.SUBTYPE_CREATE_USER_SUCCESS) {
                login();
            } else if (msg.getSubType() == Message.SUBTYPE_CREATE_USER_LOGGED_IN) {
                createStore();
            }
        } else if (msg.getType() == Message.TYPE_LOGIN) {
            if (msg.getSubType() == Message.SUBTYPE_LOGIN_SUCCESS) {
                user.setLogin(true);
                createStore();
            }
        } else if (msg.getType() == Message.TYPE_CREATE_STORE) {
            if (msg.getSubType() == Message.SUBTYPE_CREATE_STORE_SUCCESS) {
                System.out.println("Registration successful.");
            }
        }
        System.out.println(msg.getDataAsString());
    }

    private void login() {
        try {
            comm.sendMessage(new Message(Message.TYPE_LOGIN, user.getUsername() + ',' + user.getPassword()), this);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void createStore() {
        try {
            // Create store
            // We send a string with a space (not an emtpy string) because
            // the server never responds if we send an empty string
            comm.sendMessage(new Message(Message.TYPE_CREATE_STORE, " "), this);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

}
