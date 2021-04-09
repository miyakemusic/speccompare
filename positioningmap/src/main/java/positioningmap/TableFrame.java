package positioningmap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import positioningmap.Main.SpecTypeEnum;

public abstract class TableFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	abstract void save();
	abstract void newProduct(String value);
	abstract void newItem();
	abstract void onUpdate();
	abstract SpecDef getSpecDef(int row);
	abstract SpecHolder getSpecValue(int row, int col);
	abstract String getModel(int col);
	
	public TableFrame(AbstractTableModel model) {
		this.setSize(new Dimension(1000, 800));
		this.getContentPane().setLayout(new BorderLayout());
	
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		JButton save = new JButton("Save");
		panel.add(save);
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}	
		});
		
		JButton createProduct = new JButton("New Product");
		panel.add(createProduct);
		createProduct.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String value = JOptionPane.showInputDialog(this, "Product Name");
				if (value != null && !value.isEmpty()) {
					newProduct(value);
				}
			}	
		});

		JButton createItem = new JButton("New Item");
		panel.add(createItem);
		createItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> combo = new JComboBox<>();
				
				Arrays.asList(SpecTypeEnum.values()).forEach(v -> combo.addItem(v.toString()));
				JOptionPane.showMessageDialog( null, combo, "Spec type", JOptionPane.QUESTION_MESSAGE);
				newItem();
			}	
		});
		
		this.getContentPane().add(panel, BorderLayout.NORTH);
		JTable table;
		this.getContentPane().add(new JScrollPane(table = new JTable(model)), BorderLayout.CENTER);
				
		JPopupMenu popup = new JPopupMenu();
		JMenuItem editMenu = new JMenuItem("Edit");
		popup.add(editMenu);
		editMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doEdit(table);
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					
					popup.show(table, e.getX(), e.getY());
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doEdit(table);
				}
			}
			
			
		});
	}
	protected void showEditor(String model, SpecDef specDef, SpecHolder specValue) {
		ValueEditor dialog = new ValueEditor(this, model, specDef, specValue);
		dialog.setModal(true);
		dialog.setVisible(true);
		if (dialog.ok()) {
			onUpdate();
		}
	}
	private void doEdit(JTable table) {
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
		if (col < 2) {
			return;
		}
		String model = getModel(col);
		SpecDef ret = getSpecDef(row);
		SpecHolder specValue = getSpecValue(row, col);	
		showEditor(model, ret, specValue);
	}

}
