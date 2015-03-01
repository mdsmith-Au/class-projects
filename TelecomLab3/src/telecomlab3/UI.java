package telecomlab3;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import telecomlab3.commands.*;

public class UI {

    private User user;

    public UI(ExecutorService executorService, CommHandler comm, User user) {
        UIProcess ui = new UIProcess();
        executorService.submit(ui);

        this.user = user;

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
            }
        }
    }

    private void registerAllCommands(CommHandler comm) {
        Command[] knownCmds = new Command[] {
            new ExitCommand(comm, user),
            new EchoCommand(comm),
            new RegisterCommand(comm, user),
            new LoginCommand(comm, user),
            new DeleteCommand(comm, user),
            new LogoffCommand(comm, user),
            new SendMessageCommand(comm, user)
        };

        CommandHandler cmdHandler = CommandHandler.getInstance();

        for (Command cmd : knownCmds) {
            cmdHandler.registerCommand(cmd.getName(), cmd.getArgCount(), cmd);
        }

    }
}
