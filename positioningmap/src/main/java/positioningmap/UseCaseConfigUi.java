package positioningmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public abstract class UseCaseConfigUi extends JFrame {

	public UseCaseConfigUi(SpecSheet specSheet, UseCaseContainer pmdefs2) {
		setSize(new Dimension(1000, 800));
		getContentPane().setLayout(new BorderLayout());
		JTable table;
		UseCaseConfigModel model;
		getContentPane().add(new JScrollPane(table = new JTable(model = new UseCaseConfigModel(specSheet, pmdefs2) {

			@Override
			protected void save() {
				UseCaseConfigUi.this.save();
			}
			
		})), BorderLayout.CENTER);
		
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					UseCaseDefElement config = model.getValue(table.getSelectedRow(), table.getSelectedColumn());
					FieldEditor editor = new FieldEditor(config) {
						@Override
						void onOk() {
							model.fireTableDataChanged();
						}
					};
					editor.setLocation(e.getX(), e.getY());
					editor.setVisible(true);
				}
			}
			
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.getContentPane().add(panel, BorderLayout.NORTH);
		JButton saveButton = new JButton("Save");
		panel.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.save();
			}
		});
	}

	abstract protected void save();

}
