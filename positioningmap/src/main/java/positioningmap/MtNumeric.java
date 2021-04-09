package positioningmap;

import positioningmap.Main.Better;
import positioningmap.Main.Unit;

public class MtNumeric  implements MtSpecItem {

	private double value;
	private Unit unit;

	public MtNumeric(double d, Unit unit, Better higher) {
		this.value = d;
		this.unit = unit;
	}

	@Override
	public String value() {
		return String.valueOf(value) + " " + unit;
	}

}
