package pos.register;

import static pos.common.Utils.log;
import static pos.common.Utils.message;
import static pos.common.Utils.posConnectionTimeoutMillis;
import static pos.common.Utils.posHost;
import static pos.common.Utils.posPort;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.PlainDocument;

import pos.common.RegisterStatus;
import pos.model.Product;

public class Register implements ActionListener, FocusListener {
	
	private static final String EMPTY_STRING = "";
	private static final String PRODUCT_SELECTION = "productSelection";
	private static final String ENTER_LINE_ITEM = "enterLineItem";
	private String currentProductSelection = EMPTY_STRING;
	private int currentQuantity = 1;
	private Socket posManagerSocket;
	private PrintStream toPosManager;
	private InputStream fromPosManager;
	private byte[] buffer;
	private boolean running;
	private String registerIdentifier;
	Border raisedBevel = BorderFactory.createRaisedBevelBorder();
	Border loweredBevel = BorderFactory.createLoweredBevelBorder();
	Border compoundBorder = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);
	FlowLayout flowLayout = new FlowLayout();
	RegisterStatus status = RegisterStatus.CLOSED;
	String cashierDisplayName = null;
	private static final String SPACER = "  ";
	
	public Register(String registerIdentifier) {
		this.registerIdentifier = registerIdentifier;
	}
	
	public void init() throws UnknownHostException, IOException {
		createAndShowGUI();
		run();
	}
	
	private void createAndShowGUI() throws UnknownHostException, IOException {
        JFrame frame = new JFrame(message("pos.register"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
        rootPanel.add(statusPanel());
        rootPanel.add(cashierLoginButtonPanel());
		rootPanel.add(productSelectionPanel());
		rootPanel.add(saleDetailPanel());
		rootPanel.add(cardSwipePanel());
		rootPanel.add(cashDrawerPanel());
        frame.getContentPane().add(rootPanel);

        frame.pack();
        frame.setVisible(true);
        
        // this will be done in response to user input
        connectToStoreSystem();
    }
	
	private JPanel statusPanel() throws IOException {
		JPanel panel = standardPanel();
		panel.setLayout(flowLayout);

        JPanel registerIdPanel = standardPanel();
        registerIdPanel.add(new JLabel(message("register", registerIdentifier)));
        panel.add(registerIdPanel);
        
        JPanel registerStatusPanel = standardPanel();
        registerStatusPanel.add(new JLabel(message("status", message(status.toString().toLowerCase()))));
        panel.add(registerStatusPanel);
        
        JPanel cashierIdPanel = standardPanel();
        cashierIdPanel.add(new JLabel(
        		cashierDisplayName == null ? message("cashier", message("none")) : cashierDisplayName));
        panel.add(cashierIdPanel);
                
		return panel;
	}
	
	private JPanel cashierLoginButtonPanel() throws IOException {
        JPanel panel = standardPanel();
        
        panel.add(new JLabel(message("cashier.login")));
        
        JTextField employeeId = new JTextField();
        employeeId.setPreferredSize(new Dimension(120,24));
        employeeId.setEditable(true);
        employeeId.setToolTipText(message("enter.employee.id"));
        panel.add(employeeId);
        
        JButton cashierLoginButton = new JButton();
        cashierLoginButton.setText(message("submit"));
        panel.add(cashierLoginButton);
		
		return panel;
	}
	
	private JPanel saleDetailPanel() {
		JPanel panel = standardPanel();
		panel.add(new JLabel("Sale Detail Panel"));
		
		return panel;
	}
	
	private JPanel productSelectionPanel() throws IOException {
		JPanel panel = standardPanel();

		JPanel productListPanel = new JPanel();
		productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
		JLabel productLabel = new JLabel(message("product"));
        productListPanel.add(productLabel);
		
		List<Product> products = Product.findAll();

		JComboBox<String> productList = new JComboBox<String>();
		productList.setToolTipText(message("select.product"));
		for (Product product : products) {
			productList.addItem(
                product.getSku() + SPACER + 
                product.getUnitPrice() + SPACER +
                product.getDescription());
		}
		productList.setActionCommand(PRODUCT_SELECTION);
		productList.addActionListener(this);
		productListPanel.add(productList);
		panel.add(productListPanel);
		
		JPanel quantityPanel = new JPanel();
		quantityPanel.setLayout(new BoxLayout(quantityPanel, BoxLayout.Y_AXIS));
		JLabel quantityLabel = new JLabel(message("quantity"));
        quantityPanel.add(quantityLabel);
        
        JTextField quantity = new JTextField();
        quantity.setPreferredSize(new Dimension(120,24));
        quantity.setToolTipText(message("enter.quantity"));
        quantityPanel.add(quantity);
        quantity.addFocusListener(this);
        PlainDocument doc = (PlainDocument) quantity.getDocument();
        doc.setDocumentFilter(new IntegerFilter());
        panel.add(quantityPanel);
        
        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));
        JLabel spacer = new JLabel(" ");
        entryPanel.add(spacer);        
        JButton enterLineItem = new JButton();
        enterLineItem.setActionCommand(ENTER_LINE_ITEM);
        enterLineItem.addActionListener(this);
        enterLineItem.setText(message("enter"));
        enterLineItem.setToolTipText(message("add.to.order"));
        entryPanel.add(enterLineItem);
        panel.add(entryPanel);
		
		return panel;
	}
	
	private JPanel cashDrawerPanel() {
		JPanel panel = standardPanel();
		panel.add(new JLabel("Cash Drawer Panel"));
		
		return panel;
	}
	
	private JPanel cardSwipePanel() {
		JPanel panel = standardPanel();
		panel.add(new JLabel("Card Swipe Panel"));
		
		return panel;
	}

	private JPanel standardPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(compoundBorder);
        return panel;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		if (actionCommand.equals(PRODUCT_SELECTION)) {
			@SuppressWarnings("unchecked")
			JComboBox<String> productSelection = (JComboBox<String>) event.getSource();
			currentProductSelection = (String) productSelection.getSelectedItem();
		} else if (actionCommand.equals(ENTER_LINE_ITEM)) {
			if (null != currentProductSelection && 
					currentProductSelection.trim().length() > 0 && 
					currentQuantity > 0) {
				
				System.out.println("ready to add line item: " + currentProductSelection + ", quantity: " + currentQuantity);

	        currentProductSelection = EMPTY_STRING;
	        currentQuantity = 1;
				
			}
			
		}
	}

	@Override
	public void focusGained(FocusEvent event) {
        ;        
	}

	@Override
	public void focusLost(FocusEvent event) {
		JTextField quantity = (JTextField) event.getSource();
		currentQuantity = Integer.parseInt(quantity.getText());
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
