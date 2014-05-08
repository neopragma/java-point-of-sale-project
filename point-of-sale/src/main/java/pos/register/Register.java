package pos.register;

import static pos.common.Utils.log;
import static pos.common.Utils.message;
import static pos.common.Utils.posConnectionTimeoutMillis;
import static pos.common.Utils.posHost;
import static pos.common.Utils.posPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
		createAndShowGUI();
		run();
	}
	
	private void createAndShowGUI() throws UnknownHostException, IOException {
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(statusPanel());
        frame.pack();
        frame.setVisible(true);
        
        // this will be done in response to user input
        connectToStoreSystem();
    }
	
	private JPanel statusPanel() throws IOException {
        JPanel panel = new JPanel();
        JLabel statusLabel = new JLabel(message("register.status", registerIdentifier, message("closed")));
        panel.add(statusLabel);
		return panel;
	}
	
	private void connectToStoreSystem() throws UnknownHostException, IOException {
		posManagerSocket = new Socket();
		log("attempting.connection", registerIdentifier);
		try {
			posManagerSocket.connect(
				new InetSocketAddress(posHost(), posPort()), posConnectionTimeoutMillis());
		} catch (Exception e) {
            log("unable.to.connect", registerIdentifier);
            return;
		}		
		toPosManager = new PrintStream(posManagerSocket.getOutputStream());
	    fromPosManager = posManagerSocket.getInputStream();
		toPosManager.println("init " + registerIdentifier);
		toPosManager.flush();
		running = true;		
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
		if (message.equals("close")) {
			close();
			running = false;
		}
	}
	
	private void close() throws IOException {
		log("closing", registerIdentifier);
		toPosManager.println("close");
		toPosManager.flush();
		//TODO need ack from server here before closing socket
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
