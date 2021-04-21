package positioningmap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

interface TableFrameInterface {

	List<String> categories();

	List<String> units();

	SpecDef createSpecDef();

	Map<String, String> parents();

	boolean isEnabled(int row, String product);	
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
	abstract void rename(String oldName, String newName);
	abstract void copyProduct(String name);
	abstract void moveLeft(String name);
	abstract void moveRight(String name);
	abstract void copyCells(int[] fromRows, String fromColumn, String toColumn);
	abstract void delete(int row);
	
	private JTable table = null;
	protected String selecteHeaderName;
	private int copiedColumn;
	private int[] copiedRows;
	private TableFrameInterface tableFrameInterface;
	protected int currentRow = 0;
	
	public TableFrame(AbstractTableModel model, TableFrameInterface tableFrameInterface) {
		this.tableFrameInterface = tableFrameInterface;
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
		
		JButton upButton = new JButton("Move Up");
		panel.add(upButton);
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentRow = table.getSelectedRow();
				moveUp(currentRow, table.getSelectedColumn());
				currentRow--;
			}
		});
		
		JButton downButton = new JButton("Move Down");
		panel.add(downButton);
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentRow = table.getSelectedRow();
				moveDown(currentRow, table.getSelectedColumn());
				currentRow++;
			}
		});
		
		this.getContentPane().add(panel, BorderLayout.NORTH);
		
		JScrollPane scrollPane;
		this.getContentPane().add(scrollPane = new JScrollPane(table = new JTable(model)), BorderLayout.CENTER);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDefaultRenderer(Object.class, new StatusColumnCellRenderer());
		
		MultiLineHeaderRenderer renderer = new MultiLineHeaderRenderer();
		Enumeration<TableColumn> enumK = table.getColumnModel().getColumns();
		while (enumK.hasMoreElements())	{
			((TableColumn) enumK.nextElement()).setHeaderRenderer(renderer);
		}
			
		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				List<Integer> widths = new ArrayList<>();
				for (int col = 0; col < table.getColumnCount(); col++) {
					TableColumn tableColumn = table.getColumnModel().getColumn(col);
					widths.add(tableColumn.getWidth());
				}
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Enumeration<TableColumn> enumK = table.getColumnModel().getColumns();
						
						Iterator<Integer> it = widths.iterator();
						while (enumK.hasMoreElements())	{
							TableColumn tableColumn = ((TableColumn) enumK.nextElement());
							tableColumn.setHeaderRenderer(renderer);
							if (it.hasNext()) {
								tableColumn.setPreferredWidth(it.next());
							}
						}
						//table.setRowSelectionAllowed(true);
						table.setRowSelectionInterval(currentRow, currentRow);
					}
				});

			}
		});
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
		
		popup.add(createMenuItem("Delete", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delete(table.getSelectedRow());
			}
		}));
		
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCellSelectionEnabled(true);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				currentRow = table.getSelectedRow();
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
//		JMenuItem menuEditName = new JMenuItem("Edit Name");
//		popupHeader.add(menuEditName);
//		menuEditName.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (selecteHeaderName != null && !selecteHeaderName.isEmpty()) {
//					String value = JOptionPane.showInputDialog(this, selecteHeaderName);
//					if (value != null && !value.isEmpty()) {
//						changeProductName(selecteHeaderName, value);
//					}
//				}
//			}
//		});
		
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
		
		JMenuItem menuRename = new JMenuItem("Rename");
		popupHeader.add(menuRename);
		menuRename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProductKey key = new ProductKey();
				if (selecteHeaderName.contains("\n")) {
					String[] tmp = 	selecteHeaderName.split("\n");
					key.setProductName(tmp[1]);
					key.setVendorName(tmp[0]);
				}
				else {
					key.setVendorName("");
					key.setProductName(selecteHeaderName);
				}
				new FieldEditor(key) {

					@Override
					void onOk() {
						rename(selecteHeaderName, key.getVendorName() + "\n" + key.getProductName());
					}
					
				}.setVisible(true);

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
		DefEditor dialog = new DefEditor(this, spedDef, 
				tableFrameInterface.categories(), 
				tableFrameInterface.units(),
				tableFrameInterface.parents());
		dialog.setModal(true);
		dialog.setVisible(true);
		if (dialog.ok()) {
			onUpdate();
		}
	}

	class StatusColumnCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			
			if (row == currentRow) {
				this.setBackground(Color.YELLOW);
			}
			else if(isSelected) {
	            this.setBackground(Color.GREEN);
	        }
	        else {
	            this.setBackground(table.getBackground());
	        }

	        if(hasFocus) {
	            this.setBackground(Color.GREEN);
	        }
	         	        
	        if (column >= 2) {
		        if (!tableFrameInterface.isEnabled(row, table.getColumnName(column)) ) {
		        	this.setBackground(Color.DARK_GRAY);
		        }
	        }
	        this.setFont(table.getFont());
	        this.setText(value.toString());
	        return this;
		}
		
	}
}

class MultiLineHeaderRenderer extends JList implements TableCellRenderer {
	public MultiLineHeaderRenderer() {
		setOpaque(true);
		setForeground(UIManager.getColor("TableHeader.foreground"));
		setBackground(UIManager.getColor("TableHeader.background"));
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		ListCellRenderer renderer = getCellRenderer();
		((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
		setCellRenderer(renderer);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Font font = new Font(table.getFont().getName(), table.getFont().getStyle(), 10);
		setFont(font);
		String str = (value == null) ? "" : value.toString();
		BufferedReader br = new BufferedReader(new StringReader(str));
		String line;
		Vector v = new Vector();
		try {
			while ((line = br.readLine()) != null) {
				v.addElement(line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		setListData(v);
		return this;
	}
}
