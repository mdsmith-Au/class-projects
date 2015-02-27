package telecomlab3.commands;

import telecomlab3.CommHandler;
import telecomlab3.Command;

public class ExitCommand implements Command {
    private final String name = "exit";
    private final int argCount = 0;

    CommHandler comm;
    
    public ExitCommand(CommHandler comm) {
        this.comm = comm;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String[] arguments) {
        // TODO
        /*  This command requests that the
            current connection to the server
            be terminated and the current user
            logged out. This command has no
            corresponding message data. If
            data is sent to the server, it is
            ignored.
        */
        System.exit(0);
    }

    @Override
    public int getArgCount() {
        return argCount;
    }

}
