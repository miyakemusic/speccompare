package positioningmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public abstract class ConditionContainerUi extends JFrame {

	private static final String DESCRIPTION = "Description";
	private static final String NUMBER = "Number";
	private static final String OPTION_NAME = "Option Name";

	abstract ConditionContainer replaceName(String prevCondition, String newCondition);
	
	public ConditionContainerUi(ConditionContainer conditions) {
		this.setSize(new Dimension(800, 600));
		List<String> list = conditions.conditionNameList();
		
		this.getContentPane().setLayout(new BorderLayout());
		
		List<String> title = Arrays.asList(OPTION_NAME, NUMBER, DESCRIPTION);
		AbstractTableModel model = new AbstractTableModel() {

			@Override
			public int getRowCount() {
				return list.size();
			}

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (title.get(columnIndex).equals(OPTION_NAME)) {
					return list.get(rowIndex);
				}
				else if (title.get(columnIndex).equals(NUMBER)) {
					StringBuffer buf = new StringBuffer();
					conditions.getConditions().get(list.get(rowIndex)).getElements().forEach(s -> {
						buf.append(s.value + "(" + s.description + ")" + ", ");
					});
					return buf.toString();
				}
				else if (title.get(columnIndex).equals(OPTION_NAME)) {
					return "";
				}
				return list.get(rowIndex);
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				list.clear();
				list.addAll(replaceName(list.get(rowIndex), aValue.toString()).conditionNameList());
				this.fireTableDataChanged();
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return true;
			}
			
		};
		this.getContentPane().add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
	}

}
