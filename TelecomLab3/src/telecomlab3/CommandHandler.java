package telecomlab3;

import java.util.HashMap;

public class CommandHandler {
    private static CommandHandler instance;
    private final HashMap<String, CommandEntry> clientCommandMap;

    private CommandHandler() {
        clientCommandMap = new HashMap<String, CommandEntry>();
    }

    public void registerCommand(String cmdString, int argc, Command handler) {
        if (!cmdString.isEmpty() && handler != null) {
            CommandEntry entry = new CommandEntry(handler, argc);
            clientCommandMap.put(cmdString, entry);
        }
    }

    public void parseCommand(String cmd) {
        // Some String pre-processing
        cmd = cmd.trim();

        // Only process if the command begins with /
        if (cmd.startsWith("/")) {
            String[] cmdComponents = cmd.split(" ", 2); // split only first occurence
            String cmdName = cmdComponents[0];
            cmdName = cmdName.substring(1);

            // Treat the rest of the input as arguments
            String args = new String();
            if (cmdComponents.length > 1) {
                args = cmdComponents[1];
            }

            CommandEntry cmdEntry = clientCommandMap.get(cmdName);
            if (cmdEntry != null) {
                String[] cmdArgs = null;
                int numArgs = 0;
                if (!args.isEmpty()) {
                    cmdArgs = args.split(",");
                    numArgs = cmdArgs.length;
                }

                int argc = cmdEntry.getArgCount();
                Command handler = cmdEntry.getCommand();

                // Handle arguments
                if (numArgs < argc) { // number of supplied args < required args
                    System.out.println("Error: " + cmdName + " must take " +
                        argc + " argument(s).");
                }
                else {
                    handler.execute(cmdArgs);
                }
            }
            else {
                // TODO: replace with exception?
                System.out.println("Unknown command: " + cmdName);
            }
        }
    }

    public static CommandHandler getInstance() {
        if (instance == null) {
            instance = new CommandHandler();
        }
        return instance;
    }

    class CommandEntry {
        private final Command command;
        private final int argc;

        public CommandEntry(Command command, int argc) {
            this.command = command;
            this.argc = argc;
        }

        public Command getCommand() {
            return command;
        }

        public int getArgCount() {
            return argc;
        }
    }
}
