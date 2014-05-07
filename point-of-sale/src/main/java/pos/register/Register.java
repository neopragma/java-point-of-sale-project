package pos.register;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import static pos.common.Utils.posHost;
import static pos.common.Utils.posPort;
import static pos.common.Utils.posConnectionTimeoutMillis;

public class Register {
	
	private Socket posManagerSocket;
	private PrintStream toPosManager;
	private InputStream fromPosManager;
	private byte[] buffer;
	private boolean running;
	private String registerIdentifier;
	private static Logger log;
	
	public Register(String registerIdentifier) {
		this.registerIdentifier = registerIdentifier;
	}
	
	public void init() throws UnknownHostException, IOException {
//	    log = Logger.getLogger("Register");		
		posManagerSocket = new Socket();
		posManagerSocket.connect(
			new InetSocketAddress(posHost(), posPort()), posConnectionTimeoutMillis());		
		toPosManager = new PrintStream(posManagerSocket.getOutputStream());
	    fromPosManager = posManagerSocket.getInputStream();
		toPosManager.println("initregister " + registerIdentifier);
		toPosManager.flush();
		running = true;
		run();
	}
	
	public void run() throws IOException {
		while (running) {
		    buffer = new byte[4096];
			fromPosManager.read(buffer);
			String received = new String(buffer);
			if (received.trim().length() > 0) {
			    processMessageFromPosManager(new String(buffer));
			}
		}
	}

	private void processMessageFromPosManager(String message) throws IOException {
		System.out.println("Register " + registerIdentifier + " received: " + message);
		if (message.equals("close")) {
			close();
			running = false;
		}
	}
	
	private void close() throws IOException {
//		log.info("Register " + registerIdentifier + " is closing.");
		toPosManager.println("close");
		toPosManager.flush();
		// need ack from server here before closing socket
		posManagerSocket.close();
		System.exit(0);
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		if (args.length < 1) {
			throw new RuntimeException("Register identifier must be passed as the first command line argument");
		}
		Register register = new Register(args[0]);
		register.init();
		System.exit(0);
	}

}
