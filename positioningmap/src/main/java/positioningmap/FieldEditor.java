package positioningmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class FieldEditor extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private InputContainer inputs;
	
	abstract void onOk();
	public FieldEditor(Object object) {
		this.setLocationRelativeTo(null);
		inputs = new InputContainer(object);
		
		this.setSize(new Dimension(400, object.getClass().getDeclaredFields().length * 24 + 100));
		
		this.getContentPane().setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		for (Field field : object.getClass().getDeclaredFields()) {
			if (skip(field)) {
				continue;
			}
			panel.add(inputs.createWidget(field.getName()));
		}
		
		this.getContentPane().add(panel, BorderLayout.CENTER);
		ControlBar controlBar = new ControlBar() {
			@Override
			void onOk() {
				inputs.commit();
				FieldEditor.this.setVisible(false);
				FieldEditor.this.onOk();
			}

			@Override
			void onCancel() {
				FieldEditor.this.setVisible(false);
			}
		};
		this.getContentPane().add(controlBar, BorderLayout.SOUTH);
	}
	private boolean skip(Field field) {
		for (int i = 0; i < field.getAnnotations().length; i++) {
			Annotation an = field.getAnnotations()[i];
			if (an.annotationType().getName().equals(JsonIgnore.class.getName())) {
				return true;
			}
			
		}
		return false;
	}

}
