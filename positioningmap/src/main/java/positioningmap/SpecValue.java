package positioningmap;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SpecValue implements Cloneable {

	public static final String INITIAL_VALUE = "-Infinity";
	
	private Double x = Double.NEGATIVE_INFINITY;
	private Double y = Double.NEGATIVE_INFINITY;
	private Double z = Double.NEGATIVE_INFINITY;
	private Boolean available = false;
	private String string = "";
	private Boolean defined = false;
	private List<String> multiple = new ArrayList<>();
	private String comment;
	
//	@JsonIgnore
//	private boolean init;
//	@JsonIgnore
//	public boolean isInit() {
//		return init;
//	}
//	@JsonIgnore
//	public void setInit(boolean init) {
//		this.init = init;
//	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public SpecValue() {}
	
	public SpecValue(double x1, double y1) {
		this.x = x1;
		this.y = y1;
		this.setDefined(true);
	}

	public SpecValue(boolean available1) {
		this.available = available1;
		this.setDefined(true);
	}

	public SpecValue(double v) {
		this.x = v;
		this.setDefined(true);
	}

	public SpecValue(String string1) {
		this.string = string1;
		this.setDefined(true);
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

	public Double getZ() {
		return z;
	}

	public void setZ(Double z) {
		this.z = z;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
		this.updateDefined();
		//this.setDefined(true);
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
		this.updateDefined();
//		if (this.string.isEmpty()) {
//			this.setDefined(false);
//		}
//		else {
//			this.setDefined(true);
//		}
	}

	public void setX(Double x) {
		if (this.x != x) {
			this.x = x;
			updateDefined();
		}
	}

	private void updateDefined() {
		//if (this.x == Double.NEGATIVE_INFINITY && this.y == Double.NEGATIVE_INFINITY) {
		if (this.initialized()) {
			this.setDefined(false);
		}
		else {
			this.setDefined(true);
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
		this.multiple = multiple;
		this.updateDefined();
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
	
	public boolean initialized() {
		return x == Double.NEGATIVE_INFINITY && y == Double.NEGATIVE_INFINITY && available == false && string.isEmpty() && multiple.size() == 0;
	}
}
