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

            CommandEntry cmdEntry = clientCommandMap.get(cmdName);
            if (cmdEntry != null) {
                int argc = cmdEntry.getArgCount();
                Command handler = cmdEntry.getCommand();

                // Handle arguments
                if (argc > 0) {
                    String[] cmdArgs = cmdComponents[1].split(",");
                    handler.execute(cmdArgs);
                }
                else if (argc == 0) {
                    handler.execute(null);
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
