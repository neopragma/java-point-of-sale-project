package pos.register.ui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Displays the currency in the register's cash drawer.
 * 
 * @author neopragma
 */
public class CashDrawerPanel extends JPanel {

	private static final long serialVersionUID = -8189078071964728634L;
	private Border raisedBevel = BorderFactory.createRaisedBevelBorder();
	private Border loweredBevel = BorderFactory.createLoweredBevelBorder();
	private Border compoundBorder = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);

	public CashDrawerPanel() {
		this.setBorder(compoundBorder);
	    this.setLayout(new FlowLayout());
		this.add(new JLabel("Cash Drawer Panel"));		
	}

}
