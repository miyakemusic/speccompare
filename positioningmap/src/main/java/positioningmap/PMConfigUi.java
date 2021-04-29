package positioningmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class PMConfigUi extends JFrame {

	public PMConfigUi(SpecSheet specSheet) {
		setSize(new Dimension(1000, 800));
		getContentPane().setLayout(new BorderLayout());
		JTable table;
		PMConfigModel model;
		getContentPane().add(new JScrollPane(table = new JTable(model = new PMConfigModel(specSheet))), BorderLayout.CENTER);
		
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					PMConfigValue config = model.getValue(table.getSelectedRow(), table.getSelectedColumn());
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
	}

}
