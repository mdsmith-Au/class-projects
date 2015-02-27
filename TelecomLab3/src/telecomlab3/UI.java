package telecomlab3;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import telecomlab3.commands.*;

public class UI {

    private User user;
    
    public UI(ExecutorService executorService, CommHandler comm) {
        UIProcess ui = new UIProcess();
        executorService.submit(ui);

        user = new User();
        
        registerAllCommands(comm);
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

    private void registerAllCommands(CommHandler comm) {
        Command[] knownCmds = new Command[] {
            new ExitCommand(comm),
            new EchoCommand(comm),
            new RegisterCommand(comm, user),
            new LoginCommand(comm, user)
        };

        CommandHandler cmdHandler = CommandHandler.getInstance();

        for (Command cmd : knownCmds) {
            cmdHandler.registerCommand(cmd.getName(), cmd.getArgCount(), cmd);
        }

    }
}
