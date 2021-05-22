package positioningmap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
	
	public ValueEditor(JFrame parent, String productName, SpecDef specDef, SpecHolder specHolder, 
			Collection<String> productList, Collection<String> conditions) {
		super(parent);
		setLocationRelativeTo(null);
		int height = 0;
		this.setTitle(productName + " " + specDef.getCategory() + " - " + specDef.getName());
		
		this.getContentPane().setLayout(new BorderLayout());
				
		JPanel base = new JPanel();
		this.getContentPane().add(base, BorderLayout.CENTER);
		base.setLayout(new FlowLayout());

		JButton addCondition = new JButton("Add Condition");
		base.add(addCondition);
		JPanel mainPanel = new JPanel();
		base.add(mainPanel);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		addCondition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String value = JOptionPane.showInputDialog(this, "Condition Name");
				MyComboDialog dlg = new MyComboDialog(conditions) {

					@Override
					protected void onOk(String value) {
						specHolder.addCondition(value);
						mainPanel.removeAll();
						inputs.clear();
						createMainPane(specDef, specHolder, productList, mainPanel);
					}
					
				};
				dlg.setModal(true);
				dlg.setVisible(true);
			}
		});

		
		JPanel title = new JPanel();
		this.getContentPane().add(title, BorderLayout.NORTH);
		
		title.add(new JLabel("<HTML>" + productName + " / " + specDef.getCategory() + " - " + specDef.getName() + "</HTML>"));

		height += createMainPane(specDef, specHolder, productList, mainPanel);
				
		JPanel control = new JPanel();
		this.getContentPane().add(control, BorderLayout.SOUTH);
		control.setLayout(new FlowLayout());
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		control.add(okButton);
		control.add(cancelButton);
		height += control.getPreferredSize().height;
		
		base.setPreferredSize(new Dimension(600, height + 80));
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

	private int createMainPane(SpecDef specDef, SpecHolder specHolder, Collection<String> productList,
			JPanel mainPanel) {
		int height = 0;
		for (String condition : specHolder.getSpecs().keySet()) {
			SpecHolderElement element = specHolder.getSpecs().get(condition);
			height += createOneSpec(specDef, condition, element, productList, mainPanel);
		}
		return height;
	}

	private int createOneSpec(SpecDef specDef, String condition, SpecHolderElement element, Collection<String> productList,
			JPanel base) {
		
		int height = 0;
		JPanel condPane = new JPanel();
		base.add(condPane);
		condPane.setLayout(new BoxLayout(condPane, BoxLayout.Y_AXIS));
		condPane.setBorder(new TitledBorder(condition));
		JPanel guarantee = new JPanel();
		condPane.add(guarantee);	
		guarantee.setLayout(new FlowLayout());
		guarantee.setBorder(new TitledBorder("Guarantee"));
		createArea(specDef, element.getGuarantee(), productList, guarantee);
		height += guarantee.getPreferredSize().height;
		
		if ((specDef.getSpecType().compareTo(SpecTypeEnum.Numeric) == 0) || (specDef.getSpecType().compareTo(SpecTypeEnum.Range) == 0)
				|| (specDef.getSpecType().compareTo(SpecTypeEnum.Variation) == 0)) {
			JPanel typical = new JPanel();
			condPane.add(typical);	
			typical.setLayout(new FlowLayout());
			typical.setBorder(new TitledBorder("Typical"));
			createArea(specDef, element.getTypical(), productList, typical);
			height += typical.getPreferredSize().height;
		}
		
		
		{
			JPanel commentPanel = new JPanel();
			commentPanel.setLayout(new FlowLayout());
			condPane.add(commentPanel);
			commentPanel.add(new JLabel("Comment:"));
			commentPanel.add(createTextField(element.getGuarantee(), "Comment"));			
		}
		return height;
	}

	private void createArea(SpecDef specDef, SpecValue specValue, Collection<String> productList, JPanel panel) {
		if ((specDef.getSpecType().compareTo(SpecTypeEnum.Numeric) == 0) 
				|| (specDef.getSpecType().compareTo(SpecTypeEnum.Variation) == 0)) {
			panel.setPreferredSize(new Dimension(500, 80));
			panel.add(createTextField(specValue, "X"));
			panel.add(new JLabel(specDef.getUnit()));
		}
		else if ((specDef.getSpecType().compareTo(SpecTypeEnum.Range) == 0) || (specDef.getSpecType().compareTo(SpecTypeEnum.TwoDmensionalSize) == 0)) {
			panel.setPreferredSize(new Dimension(500, 80));
			panel.add(createTextField(specValue, "X"));
			panel.add(new JLabel(" - "));
			panel.add(createTextField(specValue, "Y"));
			panel.add(new JLabel(specDef.getUnit()));			
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.ThreemensionalSize) == 0) {
			panel.setPreferredSize(new Dimension(500, 80));
			panel.add(createTextField(specValue, "X"));
			panel.add(new JLabel(" x "));
			panel.add(createTextField(specValue, "Y"));
			panel.add(new JLabel(" x "));
			panel.add(createTextField(specValue, "Z"));
			panel.add(new JLabel(specDef.getUnit()));
		}
		else if ((specDef.getSpecType().compareTo(SpecTypeEnum.Choice) == 0)) {
			panel.setPreferredSize(new Dimension(500, 80));
			JComboBox<String> combo = createComboBox(specDef.getChoices(), specValue);
			panel.add(combo);
			
			inputs.put(new LocalSpecKey(specValue, "String"), combo);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0) {
			panel.setPreferredSize(new Dimension(500, 80));
			JCheckBox check = new JCheckBox();
			check.setSelected(specValue.getAvailable());
			inputs.put(new LocalSpecKey(specValue, "Available"), check);
			panel.add(check);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Text) == 0) {
			panel.setPreferredSize(new Dimension(500, 80));
			panel.add(createTextField(specValue, "String"));
			panel.add(new JLabel(specDef.getUnit()));			
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.MultipleChoice) == 0) {
			panel.setPreferredSize(new Dimension(500, 200));
			
			MultipleChoiceUi multiplePane = new MultipleChoiceUi(specDef.getChoices(), specValue.getMultiple());
			panel.add(multiplePane);

			inputs.put(new LocalSpecKey(specValue, "Multiple"), multiplePane.textArea());	
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.InstrumentType) == 0) {
			panel.setPreferredSize(new Dimension(500, 200));
			JPanel instrumentTypePane = new JPanel();
			panel.add(instrumentTypePane);
			instrumentTypePane.setLayout(new BoxLayout(instrumentTypePane, BoxLayout.Y_AXIS));

			JComboBox<String> combo = createComboBox(specDef.getChoices(), specValue);
			instrumentTypePane.add(combo);
			
			inputs.put(new LocalSpecKey(specValue, "String"), combo);	
			
			MultipleChoiceUi multiplePane = new MultipleChoiceUi(new ArrayList<String>(productList), specValue.getMultiple());
			instrumentTypePane.add(multiplePane);
			inputs.put(new LocalSpecKey(specValue, "Multiple"), multiplePane.textArea());	
		}
	}

	private JComboBox<String> createComboBox(List<String> choices, SpecValue specValue) {
		JComboBox<String> combo = new JComboBox<>();
		combo.addItem("");;
		choices.forEach(v -> {
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
				} catch (Exception e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
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
