package positioningmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DefEditor extends JDialog {

	private InputContainer inputs = null;
	private ControlBar contorlBar;
	
	public DefEditor(JFrame parent, SpecDef specDef, List<String> categories, List<String> units) {
		super(parent);
		
		this.setLocationRelativeTo(null);
		this.setSize(new Dimension(400, 300));
		this.getContentPane().setLayout(new BorderLayout());
		
		inputs = new InputContainer(specDef);;
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.getContentPane().add(panel, BorderLayout.CENTER);
		
		panel.add(inputs.createEditableCombo("category", categories));
		panel.add(inputs.createWidget("name"));
		panel.add(inputs.createEditableCombo("unit", units));
		
//		JComboBox<String> comboSpecType = new JComboBox<>();
//		Arrays.asList(SpecTypeEnum.values()).forEach(v -> comboSpecType.addItem(v.toString()));
//		panel.add(comboSpecType);
		panel.add(inputs.createWidget("specType"));
		

		this.getContentPane().add(contorlBar = new ControlBar() {
			@Override
			void onOk() {
				inputs.commit();
				DefEditor.this.setVisible(false);
			}

			@Override
			void onCancel() {
				DefEditor.this.setVisible(false);
			}
		}, BorderLayout.SOUTH);
	}

	public boolean ok() {
		return contorlBar.ok();
	}

}
