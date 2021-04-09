package positioningmap;

import positioningmap.Main.Unit;

public class MtPlusMinus implements MtSpecItem {

	private double value;
	private Unit unit;

	public MtPlusMinus(double value, Unit unit) {
		this.value = value;
		this.unit = unit;
	}

	@Override
	public String value() {
		return "Å}" + String.valueOf(value) + " "+ unit;
	}

}
