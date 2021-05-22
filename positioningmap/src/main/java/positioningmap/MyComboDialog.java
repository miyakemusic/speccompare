package positioningmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JDialog;

public abstract class MyComboDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyComboDialog(Collection<String> list) {
		this.setSize(new Dimension(500, 200));
		this.setLocationRelativeTo(null);
		
		this.getContentPane().setLayout(new BorderLayout());
		
		JComboBox<String> combo = new JComboBox<>();
		combo.setEditable(true);
		
		list.forEach(v -> {
			combo.addItem(v);
		});
		this.getContentPane().add(combo, BorderLayout.CENTER);
		
		this.getContentPane().add(new ControlBar() {
			@Override
			void onOk() {
				MyComboDialog.this.setVisible(false);
				MyComboDialog.this.onOk(combo.getSelectedItem().toString());
			}

			@Override
			void onCancel() {
				MyComboDialog.this.setVisible(false);
			}
		}, BorderLayout.SOUTH);
	}

	abstract protected void onOk(String string);
}
