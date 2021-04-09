package positioningmap;

import positioningmap.Main.Unit;

public class MtRange implements MtSpecItem {

	private double min;
	private double max;
	private Unit unit;

	public MtRange(double min, double max, Unit unit) {
		this.min = min;
		this.max = max;
		this.unit = unit;
	}

	@Override
	public String value() {
		return String.valueOf(min) + " - " + String.valueOf(max) + " "+ unit;
	}

}
