package telecomlab3;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;

public class UI {

    public UI(ExecutorService executorService) {
        UIProcess ui = new UIProcess();
        executorService.submit(ui);
    }

    private static class UIProcess implements Runnable {
        @Override
        public void run() {
            System.out.println("Welcome to the Telecom Chat Client.");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                // Execute command here
                String userInput = scanner.nextLine();

                if (userInput.equals("exit") || userInput.equals("quit")) {
                    System.out.println("Bye!");
                    System.exit(0);
                }
                else {
                    System.out.println("Unknown command " + userInput);
                }
            }
        }
    }
}
