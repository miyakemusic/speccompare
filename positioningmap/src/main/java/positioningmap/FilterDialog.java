package positioningmap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

interface FilterDialogInterface {

	Collection<String> vendors();

	Collection<String> categories();

	Boolean filter(String type, String string);

	void set(String type, String string, Boolean v);

	void changeCompleted();
	
}
public class FilterDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FilterDialog(FilterDialogInterface filterDialogInterface) {
		this.setSize(new Dimension(800, 600));
		this.setLayout(new BorderLayout());
		
		List<FilterWrapper> caches = new ArrayList<>();
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		JPanel vendorPanel = new JPanel();
		mainPanel.add(vendorPanel);
		
		vendorPanel.setLayout(new BorderLayout());
		vendorPanel.setBorder(new TitledBorder("Vendor"));
		List<String> vendors = new ArrayList<>(filterDialogInterface.vendors());
		vendorPanel.add(createTable(FilterContainer.Vendors, vendors, filterDialogInterface, caches), BorderLayout.CENTER);
				
		JPanel categoryPanel = new JPanel();
		categoryPanel.setLayout(new BorderLayout());
		categoryPanel.setBorder(new TitledBorder("Categories"));
		mainPanel.add(categoryPanel);
		List<String> categories = new ArrayList<String>(filterDialogInterface.categories());
		categoryPanel.add(createTable(FilterContainer.Categories, categories, filterDialogInterface, caches), BorderLayout.CENTER);	
		
		this.getContentPane().add(new ControlBar() {
			@Override
			void onOk() {
				caches.forEach(c -> c.commit());
				filterDialogInterface.changeCompleted();
				FilterDialog.this.setVisible(false);
			}

			@Override
			void onCancel() {
				FilterDialog.this.setVisible(false);
			}
		}, BorderLayout.SOUTH);
		
	}

	private Component createTable(String type, List<String> list, FilterDialogInterface filterDialogInterface, List<FilterWrapper> caches) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		List<Class> types = Arrays.asList(String.class, Boolean.class);
		FilterWrapper wrapper = new FilterWrapper(filterDialogInterface, type);
		caches.add(wrapper);
		AbstractTableModel model;
		JTable table =  new JTable(model = new AbstractTableModel() {
			@Override
			public int getRowCount() {
				return list.size();
			}

			@Override
			public int getColumnCount() {
				return types.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex == 0) {
					return list.get(rowIndex);
				}
				else if (columnIndex == 1) {
					return wrapper.filter(list.get(rowIndex));
				}
				return null;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return types.get(columnIndex);
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				wrapper.setValue(list.get(rowIndex), (Boolean)aValue);
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 1) {
					return true;
				}
				return false;
			}
			
		});
		
		panel.add(table, BorderLayout.CENTER);
		
		JPanel tools = new JPanel();
		tools.setLayout(new FlowLayout());
		
		JButton checkAll = new JButton("Check All");
		tools.add(checkAll);
		checkAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < table.getRowCount(); i++) {
					table.setValueAt(true, i, 1);
				}
				model.fireTableDataChanged();
			}
		});
		JButton cancelAll = new JButton("Cancel All");
		tools.add(cancelAll);
		cancelAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < table.getRowCount(); i++) {
					table.setValueAt(false, i, 1);
				}
				model.fireTableDataChanged();
			}
		});
		
		panel.add(tools, BorderLayout.NORTH);
		return panel;
	}
}

class FilterWrapper {
	private FilterDialogInterface filterDialogInterface;
	private Map<String, Boolean> cache = new HashMap<>();
	private String type;
	
	public FilterWrapper(FilterDialogInterface filterDialogInterface, String type) {
		this.filterDialogInterface  = filterDialogInterface;
		this.type = type;
	}

	public void commit() {
		cache.forEach((k, v) -> {
			filterDialogInterface.set(type, k, v);
		});
	}

	public void setValue(String string, Boolean aValue) {
		cache.put(string, aValue);
	}

	public Boolean filter(String string) {
		String key = string;
		if (!cache.containsKey(key)) {
			Boolean ret = filterDialogInterface.filter(type, string);
			cache.put(key, ret);			
		}
		return cache.get(key);
	}
	
}