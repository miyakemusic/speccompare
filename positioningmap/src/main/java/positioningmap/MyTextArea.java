package positioningmap;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MyTextArea extends JList<String> {

	private DefaultListModel<String> model;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyTextArea() {
		model = new DefaultListModel<>();
		this.setModel(model);
	}
	
	public void add(String string) {
		if (string.isEmpty()) {
			return;
		}
		
		for (int row = 0; row < model.getSize(); row++) {
			String value = model.getElementAt(row);
			if (value.equals(string)) {
				return;
			}
		}
		
		model.addElement(string);
	}

	public List<String> textList() {	
		List<String> ret = new ArrayList<>();
		for (int row = 0; row < model.getSize(); row++) {
			String value = model.getElementAt(row);
			ret.add(value);
		}
		return ret;
	}

	public void addList(List<String> multiple) {
		multiple.forEach(s -> {
			this.add(s);
		});
	}

	public void delete() {
		model.removeElementAt(this.getSelectedIndex());
	}

}
