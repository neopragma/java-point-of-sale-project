package pos.register.ui;

import static pos.common.Utils.message;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.PlainDocument;

import pos.model.Product;
import pos.register.Register;

/**
 * Allows the user to simulate ringing up a sale by selecting products from the available stock
 * and quantities of stock items sold.
 * 
 * @author neopragma
 */
public class ProductSelectionPanel extends JPanel {
	
	private static final long serialVersionUID = 3914221834077273834L;
	private Border raisedBevel = BorderFactory.createRaisedBevelBorder();
	private Border loweredBevel = BorderFactory.createLoweredBevelBorder();
	private Border compoundBorder = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);
	private static final String SPACER = "  ";
	private static final String EMPTY_STRING = "";

	public ProductSelectionPanel(final Register register) throws IOException {
        this.setBorder(compoundBorder);
		JPanel productListPanel = new JPanel();
		productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
		JLabel productLabel = new JLabel(message("product"));
        productListPanel.add(productLabel);
		
		List<Product> products = Product.findAll();

		final JComboBox<String> productList = new JComboBox<String>();
		productList.setToolTipText(message("select.product"));
		for (Product product : products) {
			productList.addItem(
                product.getSku() + SPACER + 
                product.getUnitPrice() + SPACER +
                product.getDescription());
		}
		productList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				@SuppressWarnings("unchecked")
				JComboBox<String> productSelection = (JComboBox<String>) event.getSource();
				register.setCurrentProductSelection((String) productSelection.getSelectedItem());
			}
		});
		productListPanel.add(productList);
		this.add(productListPanel);
		
		JPanel quantityPanel = new JPanel();
		quantityPanel.setLayout(new BoxLayout(quantityPanel, BoxLayout.Y_AXIS));
		JLabel quantityLabel = new JLabel(message("quantity"));
        quantityPanel.add(quantityLabel);
        
        final JTextField quantity = new JTextField();
        quantity.setPreferredSize(new Dimension(120,24));
        quantity.setToolTipText(message("enter.quantity"));
        quantityPanel.add(quantity);
        quantity.addFocusListener(new FocusListener() {
        	@Override
        	public void focusGained(FocusEvent event) {
                ;        
        	}
        	@Override
        	public void focusLost(FocusEvent event) {
        		JTextField quantity = (JTextField) event.getSource();
        		register.setCurrentQuantity(Integer.parseInt(quantity.getText()));
        	}
        });
        PlainDocument doc = (PlainDocument) quantity.getDocument();
        doc.setDocumentFilter(new IntegerFilter());
        this.add(quantityPanel);
        
        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));
        JLabel spacer = new JLabel(" ");
        entryPanel.add(spacer);        
        JButton enterLineItem = new JButton();
        enterLineItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (register.getCurrentProductSelection().trim().length() > 0 && 
				    register.getCurrentQuantity() > 0) {
			
			System.out.println(
					"ready to add line item: " + 
			        register.getCurrentProductSelection() + 
			        ", quantity: " + 
			        register.getCurrentQuantity());

                    register.setCurrentProductSelection(EMPTY_STRING);
                    register.setCurrentQuantity(1);
                    productList.setSelectedIndex(0);
                    quantity.setText("1");
				}
            }
        });
        enterLineItem.setText(message("enter"));
        enterLineItem.setToolTipText(message("add.to.order"));
        entryPanel.add(enterLineItem);
        this.add(entryPanel);
	}
}