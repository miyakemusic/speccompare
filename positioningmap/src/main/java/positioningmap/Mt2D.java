package positioningmap;

import positioningmap.Main.Better;
import positioningmap.Main.Unit;

public class Mt2D implements MtSpecItem {

	private double min;
	private double max;
	private Unit unit;

	public Mt2D(double min, double max, Unit unit, Better higher){
		this.min = min;
		this.max = max;
		this.unit = unit;
	}

	@Override
	public String value() {
		return String.valueOf(min) + " x " + String.valueOf(max) + " " + unit;
	}

}
