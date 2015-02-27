package telecomlab3;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelecomLab3 {
    private static ExecutorService execServ = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        CommHandler comm = new CommHandler(execServ, "ecse-489.ece.mcgill.ca", 5000);
        UI ui = new UI(execServ, comm);

    }
}
