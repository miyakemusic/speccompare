package positioningmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

interface TableFrameInterface {

	List<String> categories();

	List<String> units();

	SpecDef createSpecDef();
	
}

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
	abstract void moveUp(int row, int col);
	abstract void moveDown(int row, int col);
	abstract void changeProductName(String oldName, String newName);
	abstract void copyProduct(String name);
	abstract void moveLeft(String name);
	abstract void moveRight(String name);
	abstract void copyCells(int[] fromRows, String fromColumn, String toColumn);
	
	private JTable table = null;
	protected String selecteHeaderName;
	private int copiedColumn;
	private int[] copiedRows;
	
	public TableFrame(AbstractTableModel model, TableFrameInterface tableFrameInterface) {
		this.setSize(new Dimension(1000, 800));
		this.getContentPane().setLayout(new BorderLayout());
		this.setLocationRelativeTo(null);
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
				SpecDef specDef = tableFrameInterface.createSpecDef();
				showDefEditor(specDef, tableFrameInterface);
				newItem();
			}	
		});
		
		this.getContentPane().add(panel, BorderLayout.NORTH);
		
		this.getContentPane().add(new JScrollPane(table = new JTable(model)), BorderLayout.CENTER);
				
		JPopupMenu popup = new JPopupMenu();
		JMenuItem editMenu = new JMenuItem("Edit");
		popup.add(editMenu);
		editMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doEdit(table, tableFrameInterface);
			}
		});
		
		popup.add(createMenuItem("Move Up", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveUp(table.getSelectedRow(), table.getSelectedColumn());
			}
		}));
		popup.add(createMenuItem("Move Down", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveDown(table.getSelectedRow(), table.getSelectedColumn());
			}
		}));
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCellSelectionEnabled(true);
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
					doEdit(table, tableFrameInterface);
				}
			}
		});
		
		JPopupMenu popupHeader = new JPopupMenu();
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int col = table.columnAtPoint(e.getPoint());
			        selecteHeaderName = table.getColumnName(col);
					popupHeader.show(table.getTableHeader(), e.getX(), e.getY());
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && (e.getKeyCode()== KeyEvent.VK_C)) {
					copyCell();
				}
				else if (e.isControlDown() && (e.getKeyCode()== KeyEvent.VK_V)) {
					pastCell();
				}
			}
			
		});
		JMenuItem menuEditName = new JMenuItem("Edit Name");
		popupHeader.add(menuEditName);
		menuEditName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selecteHeaderName != null && !selecteHeaderName.isEmpty()) {
					String value = JOptionPane.showInputDialog(this, selecteHeaderName);
					if (value != null && !value.isEmpty()) {
						changeProductName(selecteHeaderName, value);
					}
				}
			}
		});
		
		JMenuItem menuCopy = new JMenuItem("Copy");
		popupHeader.add(menuCopy);
		menuCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selecteHeaderName != null && !selecteHeaderName.isBlank()) {
					copyProduct(selecteHeaderName);
				}
			}
		});
		
		JMenuItem menuToLeft = new JMenuItem("Move to left");
		popupHeader.add(menuToLeft);
		menuToLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selecteHeaderName != null && !selecteHeaderName.isBlank()) {
					moveLeft(selecteHeaderName);
				}
			}
		});
		
		JMenuItem menuToRight = new JMenuItem("Move to Right");
		popupHeader.add(menuToRight);
		menuToRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selecteHeaderName != null && !selecteHeaderName.isBlank()) {
					moveRight(selecteHeaderName);
				}
			}
		});
	}
	
	protected void pastCell() {
		copyCells(this.copiedRows, table.getColumnName(this.copiedColumn), this.table.getColumnName(table.getSelectedColumn()));
	}
	
	protected void copyCell() {
		this.copiedRows = table.getSelectedRows();
		this.copiedColumn = table.getSelectedColumn();
	}
	
	private JMenuItem createMenuItem(String string, ActionListener actionListener) {
		JMenuItem menuItem = new JMenuItem(string);
		menuItem.addActionListener(actionListener);
		return menuItem;
	}
	protected void showValueEditor(String model, SpecDef specDef, SpecHolder specValue) {
		ValueEditor dialog = new ValueEditor(this, model, specDef, specValue);
		dialog.setModal(true);
		dialog.setVisible(true);
		if (dialog.ok()) {
			onUpdate();
		}
	}
	private void doEdit(JTable table, TableFrameInterface tableFrameInterface) {
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
		if (col == 1) {
			SpecDef ret = getSpecDef(row);
			showDefEditor(ret, tableFrameInterface);
		}
		else if (col >= 2) {
			String model = getModel(col);
			SpecDef ret = getSpecDef(row);
			SpecHolder specValue = getSpecValue(row, col);	
			showValueEditor(model, ret, specValue);
		}

	}
	private void showDefEditor(SpecDef spedDef, TableFrameInterface tableFrameInterface) {
		DefEditor dialog = new DefEditor(this, spedDef, tableFrameInterface.categories(), tableFrameInterface.units());
		dialog.setModal(true);
		dialog.setVisible(true);
		if (dialog.ok()) {
			onUpdate();
		}
	}

}
