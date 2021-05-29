package positioningmap;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class ComponentAndType {
	public ComponentAndType(JComponent com, Class<?> clz) {
		this.component = com;
		this.cls = clz;
	}
	public JComponent component;
	public Class<?> cls;
}

public class InputContainer {
	protected void onChange(String fieldName, String string) {}
	public JComponent component(String fieldName) {
		return map.get("set" + toCaptal(fieldName));
	}
	private Object object;
	private Map<String, JComponent> map = new HashMap<>();
	public InputContainer(Object object) {
		this.object = object;
	}

	public void addToMap(String setMethod, JComponent component) {
		this.map.put(setMethod, component);
	}
	
	abstract class MyPanel extends JPanel {
		public MyPanel(String fieldName) {
			try {
				JPanel panel = this;
				panel.setPreferredSize(new Dimension(400, 30));
				String capital = toCaptal(fieldName);
				
				panel.add(new JLabel(capital));
				panel.add(new JLabel(":"));
				panel.setLayout(new FlowLayout());
				
				Method method = object.getClass().getMethod("get" + capital);
				
				Object ret = method.invoke(object);
				String v= "";
				if (ret != null) {
					v = ret.toString();
				}
				JComponent component = createWidget(v);
				panel.add(component);
				addToMap("set" + capital, component);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}



		abstract JComponent createWidget(String v);
		
	};
	
	private String toCaptal(String fieldName) {
		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
	}
	
	public Component createEditableCombo(String fieldName, List<String> categories) {
		return new MyPanel(fieldName) {
			@Override
			JComponent createWidget(String v) {
				try {
					JComboBox<String> combo = new JComboBox<>();
					combo.setEditable(true);
					categories.forEach(cat -> {
						combo.addItem(cat);
					});
					combo.setSelectedItem(v);
					return combo;
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
	}
	
	public JPanel createCombo(String fieldName) {
		return new MyPanel(fieldName) {
			@Override
			JComponent createWidget(String v) {
				try {
					JComboBox<String> combo = new JComboBox<>();
					Class type = object.getClass().getDeclaredField(fieldName).getType();
					if (type.isEnum()) {
						Arrays.asList(type.getEnumConstants()).forEach(v2 ->{
							combo.addItem(v2.toString());
						});
					}
					combo.setSelectedItem(v);
					combo.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							onChange(fieldName, combo.getSelectedItem().toString());
						}						
					});
					return combo;
				} catch (NoSuchFieldException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
	}
	
	public JPanel createWidget(String fieldName) {
		try {
			Method method = this.object.getClass().getMethod("get" + toCaptal(fieldName));
			Class cls = method.getReturnType();//.getReturnType().getClass();

			if (cls.isEnum()) {
				return this.createCombo(fieldName);
			}
			else if (cls.equals(Boolean.class)) {
				return this.createCheckBox(fieldName);
			}
			else if (cls.equals(String.class) || cls.equals(Double.class)){
				return this.createTextField(fieldName);
			}
		} catch (SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	private JPanel createCheckBox(String fieldName) {
		return new MyPanel(fieldName) {
			@Override
			JComponent createWidget(String v) {
				JCheckBox checkBox = new JCheckBox("");
				checkBox.setPreferredSize(new Dimension(150, 20));
				checkBox.setSelected(Boolean.valueOf(v));
				return checkBox;
			}
		};
	}

	public JPanel createTextField(String fieldName) {
		return new MyPanel(fieldName) {
			@Override
			JComponent createWidget(String v) {
				JTextField textField = new JTextField(v);
				textField.setPreferredSize(new Dimension(150, 20));
				return textField;
			}
		};
	}
	
	public void commit() {
		for(Map.Entry<String, JComponent> entry : map.entrySet()) {
			if (entry.getValue() instanceof JTextField) {
				JTextField text  = ((JTextField)entry.getValue());
				String methodName = entry.getKey();
				try {
					Class<?> type = findParameterType(methodName);
					Method method = object.getClass().getMethod(methodName, new Class[]{ type });
					if (type.equals(Double.class)) {
						method.invoke(object, Double.valueOf(text.getText()));
					}
					else {
						method.invoke(object, text.getText());
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			else if (entry.getValue() instanceof JComboBox) {
				JComboBox combo  = ((JComboBox)entry.getValue());
				String methodName = entry.getKey();
				try {
					Class type = findParameterType(methodName);
					Method method = object.getClass().getMethod(methodName, new Class[]{ type });
					
					if (type.isEnum()) {
						Enum<?> v = Enum.valueOf(type, combo.getSelectedItem().toString());
						method.invoke(object, v);
					}
					else if (type.equals(List.class)) {
						List<String> list = new ArrayList<>();
						for (int i = 0; i < combo.getItemCount(); i++) {
							list.add(combo.getItemAt(i).toString());
						}
						method.invoke(object, list);
					}
					else {
						method.invoke(object, combo.getSelectedItem().toString());
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
				
			}
			else if (entry.getValue() instanceof JCheckBox) {
				JCheckBox check  = ((JCheckBox)entry.getValue());
				String methodName = entry.getKey();	
				try {
					Method method = object.getClass().getMethod(methodName, new Class[]{ Boolean.class });
					method.invoke(object, check.isSelected());
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Class<?> findParameterType(String methodName) {
		Class<?> type = null;
		for (Method me : object.getClass().getMethods()) {
			if (me.getName().equals(methodName)) {
				type = me.getParameters()[0].getType();
				break;
			}
		}
		return type;
	}

	public Component createConfigList(String fieldName, List<String> choices) {
		JComboBox<String> combo = new JComboBox<>();
		JPanel panel =  new MyPanel(fieldName) {
			@Override
			JComponent createWidget(String v) {
				try {
					
//					combo.setEditable(true);
					choices.forEach(cat -> {
						combo.addItem(cat);
					});
					combo.setSelectedItem(v);
					return combo;
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
		panel.setPreferredSize(new Dimension(500, 40));
		JTextField textField = new JTextField();
		textField.setPreferredSize(new Dimension(100, 24));
		panel.add(textField);
		JButton button = new JButton("Add");
		panel.add(button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < combo.getItemCount(); i++) {
					if (combo.getItemAt(i).equals(textField.getText()) ) {
						return;
					}
				}
				combo.addItem(textField.getText());
			}			
		});
		JButton remove = new JButton("Remove");
		panel.add(remove);
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				combo.removeItemAt(combo.getSelectedIndex());
			}
		});
		return panel;
	}
	
}
