package positioningmap;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InputContainer {

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
				panel.setPreferredSize(new Dimension(300, 30));
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
			
//			Class cls = this.specDef.getClass().getDeclaredField(fieldName).getType();
			if (cls.isEnum()) {
				return this.createCombo(fieldName);
			}
			else if (cls.equals(Boolean.class)) {
				
			}
			else if (cls.equals(String.class)){
				return this.createTextField(fieldName);
			}
		} catch (SecurityException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

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
					Method method = object.getClass().getMethod(methodName, new Class[]{ String.class });
					method.invoke(object, text.getText());
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (entry.getValue() instanceof JComboBox) {
				JComboBox combo  = ((JComboBox)entry.getValue());
				String methodName = entry.getKey();
				try {
					Method method = SpecDef.class.getMethod(methodName, new Class[]{ String.class });
					method.invoke(object, combo.getSelectedItem().toString());
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
}
