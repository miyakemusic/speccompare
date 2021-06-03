package positioningmap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

class MyPair {
	public MyPair(String condition2, Boolean mandatory1, String value2, String description2) {
		this.condition = condition2;
		this.value = value2;
		this.mandatory = mandatory1;
		this.description = description2;
	}
	public String condition;
	public Boolean mandatory;
	public String value;
	public String description;
}
public abstract class ConditionContainerUi extends JDialog {

	private static final String DESCRIPTION = "Description";
	private static final String NUMBER = "Number";
	private static final String MANDATORY = "Mandatory";
	private static final String OPTION_NAME = "Option Name";

	abstract ConditionContainer replaceName(String prevCondition, String newCondition);
	
	public ConditionContainerUi(ConditionContainer conditions) {
		this.setSize(new Dimension(800, 600));
		
		List<MyPair> list = new ArrayList<>();
		createList(conditions, list);
			
		this.getContentPane().setLayout(new BorderLayout());
		
		List<String> title = Arrays.asList(OPTION_NAME, NUMBER, MANDATORY, DESCRIPTION);
		AbstractTableModel model = new AbstractTableModel() {

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (title.get(columnIndex).equals(MANDATORY)) {
					return Boolean.class;
				}
				else {
					return String.class;
				}
			}

			@Override
			public int getRowCount() {
				return list.size();
			}

			@Override
			public int getColumnCount() {
				return title.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				MyPair p = list.get(rowIndex);
				if (title.get(columnIndex).equals(OPTION_NAME)) {
					return p.condition;
				}
				else if (title.get(columnIndex).equals(NUMBER)) {
					return p.value;
				}
				else if (title.get(columnIndex).equals(MANDATORY)) {
					return p.mandatory;
				}
				else if (title.get(columnIndex).equals(DESCRIPTION)) {
					return p.description;
				}
//				if (title.get(columnIndex).equals(OPTION_NAME)) {
//					return list.get(rowIndex);
//				}
//				else if (title.get(columnIndex).equals(NUMBER)) {
//					StringBuffer buf = new StringBuffer();
//					conditions.getConditions().get(list.get(rowIndex)).getElements().forEach(s -> {
//						buf.append(s.value + "(" + s.description + ")" + ", ");
//					});
//					return buf.toString();
//				}
//				else if (title.get(columnIndex).equals(OPTION_NAME)) {
//					return "";
//				}
				return null;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				MyPair p = list.get(rowIndex);
				if (title.get(columnIndex).equals(OPTION_NAME)) {
					conditions.updateCondition(p.condition, aValue.toString());
				}
				else if (title.get(columnIndex).equals(NUMBER)) {
					conditions.updateValue(p.condition, p.value, aValue.toString());					
				}
				else if (title.get(columnIndex).equals(MANDATORY)) {
					conditions.updateMandatory(p.condition, p.mandatory, (Boolean)aValue);
				}
				else if (title.get(columnIndex).equals(DESCRIPTION)) {
					conditions.updateDescription(p.condition, p.value, p.description, aValue.toString());
				}
				
				createList(conditions, list);
				this.fireTableDataChanged();
			}

			@Override
			public String getColumnName(int column) {
				return title.get(column);
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return true;
			}
			
		};
		JTable table;
		this.getContentPane().add(new JScrollPane(table = new JTable(model)), BorderLayout.CENTER);
		
		JPanel panel  = new JPanel();
		panel.setLayout(new FlowLayout());
		this.getContentPane().add(panel, BorderLayout.NORTH);
		
		
		panel.add(createButton("Add New", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				conditions.addCondition("New" + System.currentTimeMillis() + "@Value" + System.currentTimeMillis());
				createList(conditions, list);
				model.fireTableDataChanged();
			}
		}));
		panel.add(createButton("Add Value", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String condition = list.get(table.getSelectedRow()).condition;
				conditions.addValue(condition, "Value" + System.currentTimeMillis());
				createList(conditions, list);
				model.fireTableDataChanged();
			}
		}));
		
		panel.add(createButton("Remove Value", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String condition = list.get(table.getSelectedRow()).condition;
				String value = list.get(table.getSelectedRow()).value;
				conditions.removeCondition(condition, value);
				createList(conditions, list);
				model.fireTableDataChanged();
			}
		}));
		
		panel.add(createButton("Move Up", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String condition = list.get(table.getSelectedRow()).condition;
				ConditionValue value = conditions.getConditions().get(condition);
				new MapMover<String, ConditionValue>(conditions.getConditions()).moveUp(value);
				
				createList(conditions, list);
				model.fireTableDataChanged();
			}
		}));
		
		panel.add(createButton("Move Down", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String condition = list.get(table.getSelectedRow()).condition;
				ConditionValue value = conditions.getConditions().get(condition);
				new MapMover<String, ConditionValue>(conditions.getConditions()).moveDown(value);
				
				createList(conditions, list);
				model.fireTableDataChanged();
			}
		}));
	}

	private Component createButton(String string, ActionListener actionListener) {
		JButton button = new JButton(string);
		button.addActionListener(actionListener);
		return button;
	}

	private void createList(ConditionContainer conditions, List<MyPair> list) {
		list.clear();
		conditions.getConditions().forEach((condition, value) ->{
			value.getElements().forEach(e ->{
				list.add(new MyPair(condition, value.getMandatory(), e.value, e.description));
			});
		});
	}

}
