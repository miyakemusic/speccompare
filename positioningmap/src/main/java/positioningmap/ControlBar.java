package positioningmap;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public abstract class ControlBar extends JPanel {
	abstract void onOk();
	abstract void onCancel();
	private boolean ok = false;
	public ControlBar() {
		this.setLayout(new FlowLayout());
		this.setSize(new Dimension(200, 30));
		JButton okButton = new JButton("OK");
		this.add(okButton);
		
		JButton cancel = new JButton("Cancel");
		this.add(cancel);
		

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ok = true;
				onOk();
			}
			
		});
		
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
			
		});
	}
	public boolean ok() {
		return ok;
	}
}
