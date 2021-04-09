package positioningmap;

import positioningmap.Main.Better;

public class MtText implements MtSpecItem {

	private String text;

	public MtText(String string) {
		this.text = string;
	}

	@Override
	public String value() {
		return text;
	}


}
