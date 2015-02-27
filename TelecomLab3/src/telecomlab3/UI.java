package telecomlab3;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import telecomlab3.commands.*;

public class UI {

    public UI(ExecutorService executorService) {
        UIProcess ui = new UIProcess();
        executorService.submit(ui);

        registerAllCommands();
    }

    private static class UIProcess implements Runnable {
        @Override
        public void run() {
            System.out.println("Welcome to the Telecom Chat Client.");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                // Execute command here
                String userInput = scanner.nextLine();

                CommandHandler cmdHandler = CommandHandler.getInstance();
                cmdHandler.parseCommand(userInput);

                /*
                if (userInput.equals("exit") || userInput.equals("quit")) {
                    System.out.println("Bye!");
                    System.exit(0);
                }
                else {
                    System.out.println("Unknown command " + userInput);
                }*/
            }
        }
    }

    private static void registerAllCommands() {
        Command[] knownCmds = new Command[] {
            new ExitCommand(),
            new EchoCommand(),
        };

        CommandHandler cmdHandler = CommandHandler.getInstance();

        for (Command cmd : knownCmds) {
            cmdHandler.registerCommand(cmd.getName(), cmd.getArgCount(), cmd);
        }

    }
}
