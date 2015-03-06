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

            Scanner scanner = new Scanner(System.in);
            System.out.println("Program running. Awaiting user input.");

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

        System.out.println("All commands be called by specifying a '/' before the command."
                + "\nArguments for commands are seperated with a space from the command itself."
                + "\nMultiple arguments are delimited by commas.  As an example, to login with"
                + "\nthe user bob and password test, the command is as follows:"
                + "\n/login bob,test "
                + "\nBelow is a list of all commands and their number of arguments.");
        for (Command cmd : knownCmds) {
            cmdHandler.registerCommand(cmd.getName(), cmd.getArgCount(), cmd);
            System.out.println("Command: " + cmd.getName() + " || # Arguments: " + cmd.getArgCount());
        }

    }
}
