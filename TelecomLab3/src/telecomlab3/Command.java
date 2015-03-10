package telecomlab3;

/**
 * Interface defining methods all UI commands should implement.
 */
public interface Command {

    /**
     * Returns the name of the command.
     *
     * @return Command name.
     */
    public String getName();

    /**
     * Returns the number of arguments required by the command.
     *
     * @return Number of arguments.
     */
    public int getArgCount();

    /**
     * Executes the command with given arguments.
     *
     * @param arguments
     */
    public void execute(String arguments);
}
