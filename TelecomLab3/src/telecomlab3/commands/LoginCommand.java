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
 * Handles the login command.
 */
public class LoginCommand implements Command, Callback {

    private final String name = "login";
    private final int argCount = 2;

    private static final Logger logger = Logger.getLogger(LoginCommand.class.getName());

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
    public LoginCommand(CommHandler comm, User user) {
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
            if (user != null && user.getLoginState()) {
                System.out.println("User already logged in.");
            } else {
                user.setUsername(parsedArgs[0]);
                user.setPassword(parsedArgs[1]);
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
    // Message is always of type login
    public void handleResponse(Message msg) {
        if (msg.getSubType() == Message.SUBTYPE_LOGIN_SUCCESS) {
            user.setLogin(true);
        } else if (msg.getSubType() == Message.SUBTYPE_LOGIN_ALREADY_LOG_IN) {
            user.setLogin(true);
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
}
