package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.Callback;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;
import telecomlab3.User;

public class LoginCommand implements Command, Callback {

    private final String name = "login";
    private final int argCount = 2;

    private static final Logger logger = Logger.getLogger(LoginCommand.class.getName());

    private final CommHandler comm;
    private User user;

    public LoginCommand(CommHandler comm, User user) {
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
            if (user != null && user.getLoginState()) {
                System.out.println("User already logged in.");
            } else {
                user.setUsername(arguments[0]);
                user.setPassword(arguments[1]);
                user.setLogin(false);
                login();
            }

        }

    }

    @Override
    public int getArgCount() {
        return argCount;
    }

    @Override
    public void handleResponse(Message msg) {
        if (msg.getType() == Message.TYPE_LOGIN) {
            if (msg.getSubType() == Message.SUBTYPE_LOGIN_SUCCESS) {
                System.out.println("User successfully logged in.");
                user.setLogin(true);
            } else if (msg.getSubType() == Message.SUBTYPE_LOGIN_ALREADY_LOG_IN) {
                System.out.println("User already logged in.");
                user.setLogin(true);
            } else if (msg.getSubType() == Message.SUBTYPE_LOGIN_BAD_CREDENTIAL) {
                System.out.println("Bad user credentials.");
            } else if (msg.getSubType() == Message.SUBTYPE_LOGIN_BAD_FORMAT) {
                System.out.println("Bad format for user login message.");
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
}
