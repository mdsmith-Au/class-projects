package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.Callback;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;
import telecomlab3.User;

public class RegisterCommand implements Command, Callback {

    private final String name = "register";
    private final int argCount = 2;

    private static final Logger logger = Logger.getLogger(RegisterCommand.class.getName());

    private final CommHandler comm;
    private User user;

    public RegisterCommand(CommHandler comm, User user) {
        this.comm = comm;
        this.user = user;
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
        } else {
            try {
                // Register user
                user = new User(arguments[0], arguments[1]);
                comm.sendMessage(new Message(Message.TYPE_CREATE_USER, arguments[0] + ',' + arguments[1]), this);

            } catch (UnsupportedEncodingException ex) {
                logger.log(Level.SEVERE, "Unable to register new user with username {0}", arguments[0]);
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
                System.out.println("User successfully created.");
                login();
            } else if (msg.getSubType() == Message.SUBTYPE_CREATE_USER_EXISTS) {
                System.out.println("User already exists.");
            } else if (msg.getSubType() == Message.SUBTYPE_CREATE_USER_LOGGED_IN) {
                System.out.println("User already logged in when creating user.");
                createStore();
            } else if (msg.getSubType() == Message.SUBTYPE_CREATE_USER_BAD_FORMAT) {
                System.out.println("Bad format for user create message.");
            }
        } else if (msg.getType() == Message.TYPE_LOGIN) {
            if (msg.getSubType() == Message.SUBTYPE_LOGIN_SUCCESS) {
                System.out.println("User successfully logged in.");
                createStore();
            } else if (msg.getSubType() == Message.SUBTYPE_LOGIN_ALREADY_LOG_IN) {
                System.out.println("User already logged in.");
            } else if (msg.getSubType() == Message.SUBTYPE_LOGIN_BAD_CREDENTIAL) {
                System.out.println("Bad user credentials.");
            } else if (msg.getSubType() == Message.SUBTYPE_LOGIN_BAD_FORMAT) {
                System.out.println("Bad format for user login message.");
            }
        } else if (msg.getType() == Message.TYPE_CREATE_STORE) {
            if (msg.getSubType() == Message.SUBTYPE_CREATE_STORE_SUCCESS) {
                System.out.println("User store succesfully created.");
                success();
            } else if (msg.getSubType() == Message.SUBTYPE_CREATE_STORE_NOT_LOG_IN) {
                System.out.println("User not logged in for creating store.");
            } else if (msg.getSubType() == Message.SUBTYPE_CREATE_STORE_EXISTS) {
                System.out.println("User store already exists.");
            }
        }
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
            comm.sendMessage(new Message(Message.TYPE_LOGIN, ""), this);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void success() {
        System.out.println("User successfully created or already exists.");
    }
}
