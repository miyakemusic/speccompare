package positioningmap;

import java.util.ArrayList;
import java.util.List;

public class SpecValue implements Cloneable {

	public static final String INITIAL_VALUE = "-Infinity";
	
	private Double x = Double.NEGATIVE_INFINITY;
	private Double y = Double.NEGATIVE_INFINITY;
	private Boolean available = false;
	private String string = "";
	private Boolean defined = false;
	private List<String> multiple = new ArrayList<>();
	
	public SpecValue() {}
	
	public SpecValue(double x1, double y1) {
		this.x = x1;
		this.y = y1;
		this.defined = true;
	}

	public SpecValue(boolean available1) {
		this.available = available1;
		this.defined = true;
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
		else if (this.multiple.size() > 0) {
			for (String s: multiple) {
				ret += s + ",";
			}
			ret = ret.substring(0, ret.length()-1);
		}
		else if (!this.string.isEmpty()) {
			ret = this.string;
		}
		else if (this.available) {
			ret  = "Yes";
		}
		else {
			ret = "No";
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
//		if (this.available != available) {
			this.available = available;
			this.defined = true;
//		}
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
		if (this.string.isEmpty()) {
			this.defined = false;
		}
		else {
			this.defined = true;
		}
	}

	public void setX(Double x) {
		if (this.x != x) {
			this.x = x;
			updateDefined();
		}
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
		if (this.y != y) {
			this.y = y;
			updateDefined();
		}
	}

	public Boolean getDefined() {
		return defined;
	}

	public void setDefined(Boolean defined) {
		this.defined = defined;
	}

	
	public List<String> getMultiple() {
		return multiple;
	}

	public void setMultiple(List<String> multiple) {
		this.defined = true;
		this.multiple = multiple;
	}

	@Override
	public SpecValue clone() {
		try {
			SpecValue ret = (SpecValue)super.clone();
			ret.available = new Boolean(available);
			ret.defined = new Boolean(defined);
			ret.string = new String(this.string);
			ret.x = new Double(this.x);
			ret.y = new Double(this.y);
			ret.multiple = new ArrayList<String>(this.multiple);
			
			return ret;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void clear() {
		x = Double.NEGATIVE_INFINITY;
		y = Double.NEGATIVE_INFINITY;
		available = false;
		string = "";
		defined = false;
		this.multiple.clear();
	}
	
}
