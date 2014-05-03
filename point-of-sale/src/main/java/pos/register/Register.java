package pos.register;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Register {
	
	private Socket sock;
	PrintStream toPosManager;
	InputStream fromPosManager;
	byte[] buffer;
	
	public void init() throws UnknownHostException, IOException {
		sock = new Socket("localhost", 1234);
		toPosManager = new PrintStream(sock.getOutputStream());
		toPosManager.println("Init");
		buffer = new byte[4096];
		fromPosManager = sock.getInputStream();
		fromPosManager.read(buffer);
		String received = new String(buffer);
		System.out.println("Register received: " + received);
		if (received.equals("close")) {
			return;
		}
	}
	
	public void run() {
		while (true) {
			
		}
	}

	public void close() throws IOException {
		sock.close();
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Register register = new Register();
		register.init();
		register.close();
		System.exit(0);
	}

}
