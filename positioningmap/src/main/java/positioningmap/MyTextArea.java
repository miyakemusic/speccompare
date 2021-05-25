package positioningmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

public class MyTextArea extends JTable {//JList<String> {

//	private DefaultListModel<String> model;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> set = new ArrayList<>();

	private AbstractTableModel model;
	
	public MyTextArea() {
		this.setModel(model = new AbstractTableModel() {

			@Override
			public int getRowCount() {
				return set.size();
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return set.get(rowIndex);
			}
			
		});
		this.setTableHeader(null);
		//model = new DefaultListModel<>();
		//this.setModel(model);
	}
	
	public void add(String string) {
		if (string.isEmpty()) {
			return;
		}
		
		if (!set.contains(string)) {
			this.set.add(string);
		}
		model.fireTableDataChanged();
//		for (int row = 0; row < set.size(); row++) {
//			String value = model.getElementAt(row);
//			if (value.equals(string)) {
//				return;
//			}
//		}
//		
//		model.addElement(string);
	}

	public List<String> textList() {	
		return this.set;
	}

	public void addList(Collection<String> multiple) {
		multiple.forEach(s -> {
			this.add(s);
		});
		model.fireTableDataChanged();
	}

	public void delete() {
		//model.removeElementAt(this.getSelectedIndex());
		set.remove(this.getSelectedRow());
		model.fireTableDataChanged();
	}

}
