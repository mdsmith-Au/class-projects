package telecomlab3;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import telecomlab3.commands.*;

/**
 * Handles all User Input.
 *
 * @author Kevin Dam
 */
public class UI {

    private final User user;
    private final CommHandler comm;

    /**
     * Creates the thread to listen for user input and registers all commands.
     *
     * @param executorService The {@link ExecutorService ExecutorService} to use
     * to create the thread.
     * @param comm The {@link CommHandler CommHanlder} object to pass to all
     * {@link CommandHandler CommandHandler} classes.
     * @param user The {@link User User} object to pass to all
     * {@link CommandHandler CommandHandler} classes.
     */
    public UI(ExecutorService executorService, CommHandler comm, User user) {
        UIProcess ui = new UIProcess();
        executorService.submit(ui);

        this.user = user;
        this.comm = comm;

        registerAllCommands();
    }

    // The thread that runs the listener
    private static class UIProcess implements Runnable {

        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            thread.setName("UI Process");

            System.out.println("Welcome to the Telecom Chat Client.");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                // Wait for user input (this blocks unless they hit Enter)
                String userInput = scanner.nextLine();

                // Parse the command
                CommandHandler cmdHandler = CommandHandler.getInstance();
                cmdHandler.parseCommand(userInput);

            }
        }
    }

    /**
     * Registers all known commands with the
     * {@link CommandHandler CommandHandler}.
     */
    private void registerAllCommands() {
        Command[] knownCmds = new Command[]{
            new ExitCommand(comm),
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
