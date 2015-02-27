package telecomlab3;

public interface Command {
    public String getName();
    public int getArgCount();
    public void execute(String[] arguments);
}
