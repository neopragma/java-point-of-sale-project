package pos.store;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;

public class PosManager implements Runnable {

	static Logger log;
   	
    private static ServerSocket ssock;

    public static void main(String args[]) throws Exception {
   	    System.setProperty("logfilename", "logs/pos.store.log");
   	    log = Logger.getLogger("PosManager");
        ssock = new ServerSocket(1234);
        System.out.println("Listening");
        log.info("PosManager listening on port 1234");
        while (true) {
            Socket sock = ssock.accept();
            System.out.println("Connected");
            new Thread(new RegisterHandler(sock)).start();
        }
    }
    
    public void run() { }
   
}
