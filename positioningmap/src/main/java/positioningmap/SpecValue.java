package positioningmap;

public class SpecValue {

	public static final String INITIAL_VALUE = "-Infinity";
	
	private Double x = Double.NEGATIVE_INFINITY;
	private Double y = Double.NEGATIVE_INFINITY;
	private Boolean available = false;
	private String string = "";
	private Boolean defined = false;
	public SpecValue() {}
	
	public SpecValue(double x1, double y1) {
		this.x = x1;
		this.y = y1;
		this.defined = true;
	}

	public SpecValue(boolean available1) {
		this.available = available1;
		this.defined = available1;
	}

	public SpecValue(double v) {
		this.x = v;
		this.defined = true;
	}

	public SpecValue(String string1) {
		this.string = string1;
		this.defined = true;
	}

	public String text() {
		if (!defined) {
			return "";
		}
		String ret = "";
		if (this.x != Double.NEGATIVE_INFINITY) {
			ret += String.valueOf(x);
			
			if (this.y != Double.NEGATIVE_INFINITY) {
				ret += " x " + String.valueOf(y);
			}
		}
		else if (this.available) {
			ret  = "Yes";
		}
		else if (!this.string.isEmpty()) {
			ret = this.string;
		}
		return ret;
	}

	public Double getX() {
		return x;
	}

	public Double getY() {
		return y;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
		this.defined = available;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
		if (this.string.isBlank()) {
			this.defined = false;
		}
		else {
			this.defined = true;
		}
	}

	public void setX(Double x) {
		this.x = x;
		updateDefined();
	}

	private void updateDefined() {
		if (this.x == Double.NEGATIVE_INFINITY && this.y == Double.NEGATIVE_INFINITY) {
			this.defined = false;
		}
		else {
			this.defined = true;
		}
	}

	public void setY(Double y) {
		this.y = y;
		updateDefined();
	}

	public Boolean getDefined() {
		return defined;
	}

	public void setDefined(Boolean defined) {
		this.defined = defined;
	}

	
}
