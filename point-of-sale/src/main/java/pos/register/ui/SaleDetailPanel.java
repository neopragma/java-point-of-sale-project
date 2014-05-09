package pos.register.ui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Displays the line items for a sale as the cashier rings up items.
 * 
 * @author neopragma
 */
public class SaleDetailPanel extends JPanel {
	
	private static final long serialVersionUID = 5651546672314230658L;
	private Border raisedBevel = BorderFactory.createRaisedBevelBorder();
	private Border loweredBevel = BorderFactory.createLoweredBevelBorder();
	private Border compoundBorder = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);

	public SaleDetailPanel() {
        this.setBorder(compoundBorder);
        this.setLayout(new FlowLayout());
		this.add(new JLabel("Sale Detail Panel"));
	}

}
