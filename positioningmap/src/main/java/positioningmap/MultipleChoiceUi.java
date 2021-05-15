package positioningmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MultipleChoiceUi extends JPanel {

	private MyTextArea area;

	public MultipleChoiceUi(List<String> choices, List<String> multiple) {
		JPanel panel = this;
		
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		panel.add(left);
		JComboBox<String> combo = new JComboBox<>();
		left.add(combo, BorderLayout.NORTH);
		choices.forEach(c -> {
			combo.addItem(c);
		});
//		panel.setPreferredSize(new Dimension(100, 100));
		area = new MyTextArea();
		area.setPreferredSize(new Dimension(80, 60));
		area.addList(multiple);
		panel.add(new JScrollPane(area));
		JButton addButton = new JButton("Add");
		left.add(addButton, BorderLayout.SOUTH);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				area.add(combo.getSelectedItem().toString());
			}
		});
		
		JButton removeButton = new JButton("Remove");
		panel.add(removeButton);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				area.delete();
			}
		});
		
	}

	public JComponent textArea() {
		return area;
	}

}
