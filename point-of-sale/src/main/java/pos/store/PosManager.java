package pos.store;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Locale;

import org.apache.log4j.Logger;

import pos.common.Messages;
import static pos.common.Utils.loggerName;
import static pos.common.Utils.posPort;

public class PosManager {

	private static Logger log;
    private ServerSocket ssock;
    private Messages messages = null;
    
    void run() throws Exception {
   	    log = Logger.getLogger(loggerName());
        ssock = new ServerSocket(posPort());
//        messages().setLocale(Locale.FRANCE);
        log.info(MessageFormat.format(messages().message("listening"), String.valueOf(posPort())));
        while (true) {
            Socket sock = ssock.accept();
            log.info("connection accepted");
            new Thread(new RegisterHandler(sock)).start();
        }
    }
    
    private Messages messages() {
    	if (messages == null) {
    		messages = new Messages();
    	}
    	return messages;
    }

    public static void main(String args[]) throws Exception {
    	PosManager pos = new PosManager();
    	pos.run();
    	System.exit(0);
    }
   
}
