package pos.register;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
	
	public Register(String registerIdentifier) {
		this.registerIdentifier = registerIdentifier;
	}
	
	public void init() throws UnknownHostException, IOException {
		posManagerSocket = new Socket();
		posManagerSocket.connect(
			new InetSocketAddress(posHost(), posPort()), posConnectionTimeoutMillis());		
		toPosManager = new PrintStream(posManagerSocket.getOutputStream());
		
		System.out.println("Register " + registerIdentifier + " sending init message to PosManager");
		toPosManager.println("init " + registerIdentifier);
		running = true;
		run();
	}
	
	public void run() throws IOException {
		System.out.println("Register " + registerIdentifier + " in run() method");
		while (running) {
			buffer = new byte[4096];
			fromPosManager = posManagerSocket.getInputStream();
			fromPosManager.read(buffer);
			processMessageFromPosManager(new String(buffer));
		}
	}

	private void processMessageFromPosManager(String message) throws IOException {
		System.out.println("Register " + registerIdentifier + " received: " + message);
		if (message.equals("close")) {
			System.out.println("Register " + registerIdentifier + " closing");
			close();
			running = false;
		}			
	}
	
	private void close() throws IOException {
		System.out.println("Register " + registerIdentifier + " is closing.");
		posManagerSocket.close();
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
