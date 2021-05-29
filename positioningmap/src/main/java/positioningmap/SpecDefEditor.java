package positioningmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import positioningmap.Main.InstrumentType;
import positioningmap.Main.SpecTypeEnum;

public class SpecDefEditor extends JDialog {

	private InputContainer inputs = null;
	private ControlBar contorlBar;
	
	public SpecDefEditor(JFrame parent, SpecDef specDef, List<String> categories, List<String> units, Map<String, String> parents) {
		super(parent);
		
		this.setLocationRelativeTo(null);
		this.setSize(new Dimension(500, 400));
		this.getContentPane().setLayout(new BorderLayout());
		
		inputs = new InputContainer(specDef) {
			@Override
			protected void onChange(String fieldName, String value) {
				if (fieldName.equals("specType") && value.equals(SpecTypeEnum.InstrumentType.name())) {
					JComboBox<String> combo = (JComboBox<String>)component("choices");
					combo.removeAllItems();
					for (int i = 0; i < InstrumentType.values().length; i++) {
						combo.addItem(InstrumentType.values()[i].name());
					}
					((JTextField)component("name")).setText(SpecTypeEnum.InstrumentType.name());
				}
			}
		};
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.getContentPane().add(panel, BorderLayout.CENTER);
		
		panel.add(inputs.createEditableCombo("category", categories));
		panel.add(inputs.createWidget("name"));
		panel.add(inputs.createEditableCombo("unit", units));
		panel.add(inputs.createWidget("specType"));
		panel.add(inputs.createWidget("better"));
		
		panel.add(inputs.createConfigList("choices", specDef.getChoices()));
		
//		panel.add(inputs.createEditableCombo("parentId", new ArrayList<String>(parents.keySet())));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(new JLabel("Parent"));
		JComboBox<String> parentCombo = new JComboBox<>();
		p.add(parentCombo);
		panel.add(parentCombo);
		parentCombo.addItem("");
		parents.keySet().forEach(k -> {
			parentCombo.addItem(k);
		});
		
		parents.forEach((k,v) -> {
			if (v.equals(specDef.getParentId())) {
				parentCombo.setSelectedItem(k);
				return;
			}
		});
		
		
		
		this.getContentPane().add(contorlBar = new ControlBar() {
			@Override
			void onOk() {
				inputs.commit();
				specDef.setParentId(parents.get(parentCombo.getSelectedItem().toString()));
				SpecDefEditor.this.setVisible(false);
			}

			@Override
			void onCancel() {
				SpecDefEditor.this.setVisible(false);
			}
		}, BorderLayout.SOUTH);

	}

	public boolean ok() {
		return contorlBar.ok();
	}

}
