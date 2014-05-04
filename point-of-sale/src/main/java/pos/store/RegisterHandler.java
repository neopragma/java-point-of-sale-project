package pos.store;

import static pos.common.Utils.authEndpoint;
import static pos.common.Utils.today;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Calendar;

import com.google.gson.Gson;

/**
 * Handles interaction with a register.
 * 
 * @author neopragma
 * 
 * Copyright 2014 Our Fine Company Inc.
 */
public class RegisterHandler implements Runnable {

	private Socket sock;
	private static final String SEPARATOR = "/";
	
	public RegisterHandler(Socket sock) {
		this.sock = sock;
	}
    
    Auth doEcho(String value) throws Exception {
    	URL endpointURL = new java.net.URL(authEndpoint() + "echo");
        HttpURLConnection request = (HttpURLConnection)endpointURL.openConnection();
        request.setRequestMethod("GET");
        request.connect();
        java.io.BufferedReader rd  = new java.io.BufferedReader(new java.io.InputStreamReader(request.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line = null;
        while ((line = rd.readLine()) != null){
          response.append(line);
        }
        request.disconnect();
        rd.close();
        return new Auth(null, null, null, null, response.toString());
    }
    
    public Auth authorize(CardData cardData, Integer amount) throws Exception {
    	Calendar cardExpiryDate = Calendar.getInstance();
    	cardExpiryDate.setTime(cardData.getExpiryDate());
    	if (today().after(cardExpiryDate)) {
    		return new Auth(null, null, 66, null, null);
    	}
    	return doAuth(cardData.getAccountNumber().asString(), amount);
    }
    
    private Auth doAuth(String accountNumber, Integer amount) throws Exception {
    	StringBuilder sb = new StringBuilder(authEndpoint());
    	sb.append("auth");
    	sb.append(SEPARATOR);
    	sb.append("12345");
    	sb.append(SEPARATOR);
    	sb.append(accountNumber);
    	sb.append(SEPARATOR);
    	sb.append(amount);
    	URL endpointUrl = new java.net.URL(sb.toString());
        HttpURLConnection request = (HttpURLConnection)endpointUrl.openConnection();
        request.setRequestMethod("GET");
        request.connect();
    	Gson gson = new Gson();
    	Auth auth = null; 
   		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
   		auth = gson.fromJson(br, Auth.class);
        request.disconnect();
        br.close();
        return auth;
    }

	@Override
	public void run() {
      try {
    	  System.out.println("RegisterHandler.run");
    	  byte[] buffer = new byte[4096];
    	  InputStream input = new BufferedInputStream(sock.getInputStream());
          input.read(buffer);
          String received = new String(buffer);
          System.out.println("RegisterHandler received: " + received);
    	  PrintStream output = new PrintStream(sock.getOutputStream());
    	  System.out.println("RegisterHandler echoing: " + received);
          output.println(received);
          System.out.println("RegisterHandler sending close command");
          output.println("close");
//          output.close();
//          sock.close();
       }
       catch (IOException e) {
          System.out.println(e);
       }
	}
        
}
