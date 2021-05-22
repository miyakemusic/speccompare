package positioningmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MultipleChoiceUi extends JPanel {

	private MyTextArea area;

	public MultipleChoiceUi(List<String> choices, List<String> multiple) {
		JPanel panel = this;
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel addPane = new JPanel();
		addPane.setLayout(new FlowLayout());
		panel.add(addPane);
		JComboBox<String> combo = new JComboBox<>();
		addPane.add(combo, BorderLayout.NORTH);
		JButton addButton = new JButton("Add");
		addPane.add(addButton);

		choices.forEach(c -> {
			combo.addItem(c);
		});
//		panel.setPreferredSize(new Dimension(100, 100));
		area = new MyTextArea();
		area.setPreferredSize(new Dimension(80, 60));
		area.addList(multiple);
		JScrollPane scroll;
		panel.add(scroll = new JScrollPane(area));
		scroll.setPreferredSize(new Dimension(400, 80));
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
		//this.setPreferredSize(new Dimension(500, 400));
	}

	public JComponent textArea() {
		return area;
	}

}
