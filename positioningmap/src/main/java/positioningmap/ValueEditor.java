package positioningmap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import positioningmap.Main.SpecTypeEnum;

public class ValueEditor extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<LocalSpecKey, JComponent> inputs = new HashMap<>();
	private boolean ok = false;
	
	public ValueEditor(JFrame parent, String model, SpecDef specDef, SpecHolder specHolder) {
		super(parent);
		setLocationRelativeTo(null);
		int height = 0;
		this.setTitle(model + " " + specDef.getCategory() + " - " + specDef.getName());
		
		this.getContentPane().setLayout(new BorderLayout());
				
		JPanel base = new JPanel();
		this.getContentPane().add(base, BorderLayout.CENTER);
		//base.setLayout(new GridLayout(3, 1));
		base.setLayout(new FlowLayout());
		
		JPanel title = new JPanel();
		this.getContentPane().add(title, BorderLayout.NORTH);
		
		title.add(new JLabel("<HTML>" + model + " / " + specDef.getCategory() + " - " + specDef.getName() + "</HTML>"));
				
		JPanel guarantee = new JPanel();
		base.add(guarantee);	
		guarantee.setLayout(new FlowLayout());
		guarantee.setBorder(new TitledBorder("Guarantee"));
		createArea(specDef, specHolder.getGuarantee(), guarantee);
		height += guarantee.getPreferredSize().height;
		
		if ((specDef.getSpecType().compareTo(SpecTypeEnum.Numeric) == 0) || (specDef.getSpecType().compareTo(SpecTypeEnum.Range) == 0)
				|| (specDef.getSpecType().compareTo(SpecTypeEnum.Variation) == 0)) {
			JPanel typical = new JPanel();
			base.add(typical);	
			typical.setLayout(new FlowLayout());
			typical.setBorder(new TitledBorder("Typical"));
			createArea(specDef, specHolder.getTypical(), typical);
			height += typical.getPreferredSize().height;
		}
		
//		this.pack();
		
		JPanel control = new JPanel();
		this.getContentPane().add(control, BorderLayout.SOUTH);
		control.setLayout(new FlowLayout());
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		control.add(okButton);
		control.add(cancelButton);
		height += control.getPreferredSize().height;
		
		base.setPreferredSize(new Dimension(600, height + 50));
		this.pack();
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (LocalSpecKey key : inputs.keySet()) {
					String value = "";
					List<String> list = null;
					JComponent component = inputs.get(key);
					if (component instanceof JTextField) {
						value = ((JTextField)component).getText();
					}
					else if (component instanceof JComboBox) {
						value = ((JComboBox)component).getSelectedItem().toString();
					}
					else if (component instanceof JCheckBox) {
						value = String.valueOf(((JCheckBox)component).isSelected());
					}
					else if (component instanceof MyTextArea) {
						list = ((MyTextArea)component).textList();
					}
					SpecValue specValue = key.spec;
					try {
						if (SpecValue.class.getDeclaredField(key.field.toLowerCase()).getType().equals(Double.class)) {
							
							Method method = SpecValue.class.getMethod("set" + key.field, new Class[]{ Double.class });
							
							double d;
							if (value.isEmpty()) {
								d = Double.NEGATIVE_INFINITY;
							}
							else {
								d = Double.valueOf(value);
							}
							method.invoke(specValue, d);
						}
						else if (SpecValue.class.getDeclaredField(key.field.toLowerCase()).getType().equals(String.class)) {
							Method method = SpecValue.class.getMethod("set" + key.field, new Class[]{ String.class });
							method.invoke(specValue, value);
						}
						else if (SpecValue.class.getDeclaredField(key.field.toLowerCase()).getType().equals(Boolean.class)) {
							Method method = SpecValue.class.getMethod("set" + key.field, new Class[]{ Boolean.class });
							method.invoke(specValue, Boolean.valueOf(value));
						}
						else if (SpecValue.class.getDeclaredField(key.field.toLowerCase()).getType().equals(List.class)) {
							Method method = SpecValue.class.getMethod("set" + key.field, new Class[]{ List.class });
							method.invoke(specValue, list);
						}
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
							| SecurityException | NoSuchMethodException | InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}
				ok = true;
				setVisible(false);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = false;
				setVisible(false);
			}
		});
	}

	private void createArea(SpecDef specDef, SpecValue specValue, JPanel panel) {
		if ((specDef.getSpecType().compareTo(SpecTypeEnum.Numeric) == 0) 
				|| (specDef.getSpecType().compareTo(SpecTypeEnum.Variation) == 0)) {
			panel.setPreferredSize(new Dimension(200, 80));
			panel.add(createTextField(specValue, "X"));
			panel.add(new JLabel(specDef.getUnit()));
		}
		else if ((specDef.getSpecType().compareTo(SpecTypeEnum.Range) == 0) || (specDef.getSpecType().compareTo(SpecTypeEnum.TwoDmensionalSize) == 0)) {
			panel.setPreferredSize(new Dimension(300, 80));
			panel.add(createTextField(specValue, "X"));
			panel.add(new JLabel(" - "));
			panel.add(createTextField(specValue, "Y"));
			panel.add(new JLabel(specDef.getUnit()));			
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Choice) == 0) {
			panel.setPreferredSize(new Dimension(200, 80));
			JComboBox<String> combo = createComboBox(specDef, specValue);
			panel.add(combo);
			
			inputs.put(new LocalSpecKey(specValue, "String"), combo);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0) {
			panel.setPreferredSize(new Dimension(200, 80));
			JCheckBox check = new JCheckBox();
			check.setSelected(specValue.getAvailable());
			inputs.put(new LocalSpecKey(specValue, "Available"), check);
			panel.add(check);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Text) == 0) {
			panel.setPreferredSize(new Dimension(200, 80));
			panel.add(createTextField(specValue, "String"));
			panel.add(new JLabel(specDef.getUnit()));			
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.MultipleChoice) == 0) {
			panel.setPreferredSize(new Dimension(300, 150));
			
			MultipleChoiceUi multiplePane = new MultipleChoiceUi(specDef.getChoices(), specValue.getMultiple());
			panel.add(multiplePane);

			inputs.put(new LocalSpecKey(specValue, "Multiple"), multiplePane.textArea());	
		}
	}

	private JComboBox<String> createComboBox(SpecDef specDef, SpecValue specValue) {
		JComboBox<String> combo = new JComboBox<>();
		combo.addItem("");;
		specDef.getChoices().forEach(v -> {
			combo.addItem(v);
		});
		combo.setSelectedItem(specValue.getString());
		return combo;
	}
	
	private Component createTextField(SpecValue specValue, String field) {
		String v = "";
		if (specValue.getDefined()) {
				//v = SpecValue.class.getField(field).get(specValue).toString();
				try {
					Method method = SpecValue.class.getMethod("get" + field);
					v = method.invoke(specValue).toString();
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
		else {
			
		}
		JTextField text = new JTextField(v);
		inputs.put(new LocalSpecKey(specValue, field), text);
		text.setPreferredSize(new Dimension(100, 20));
		return text;
	}

	public boolean ok() {
		return ok;
	}
}
class LocalSpecKey { 
	public LocalSpecKey(SpecValue spec, String field) {
		this.spec = spec;
		this.field = field;
	}

	SpecValue spec;
	String field;
}
