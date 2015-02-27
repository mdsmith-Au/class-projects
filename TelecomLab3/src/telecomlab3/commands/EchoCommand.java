package telecomlab3.commands;

import telecomlab3.Command;

public class EchoCommand implements Command {
    private final String name = "echo";
    private final int argCount = 1;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length != argCount) {
            // TODO
        }

        // TODO will need to pass the arguments to create a message, which can
        // then be passed to the CommHandler
        System.out.println(arguments[0]);
    }

    @Override
    public int getArgCount() {
        return argCount;
    }
}
