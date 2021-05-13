package positioningmap;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

public class MyTextArea extends JTextArea {

	public void add(String string) {
		if (string.isEmpty()) {
			return;
		}
		
		for (String s : this.getText().split("\n")) {
			if (s.equals(string)) {
				return;
			}
		}
		
		if (!this.getText().isEmpty()) {
			this.setText(this.getText() + "\n" + string);
		}
		else {
			this.setText(string);
		}
	}

	public List<String> textList() {
		List<String> ret = new ArrayList<>();
		for (String s : this.getText().split("\n")) {
			ret.add(s);
		}		
		return ret;
	}

	public void addList(List<String> multiple) {
		multiple.forEach(s -> {
			this.add(s);
		});
	}

}
