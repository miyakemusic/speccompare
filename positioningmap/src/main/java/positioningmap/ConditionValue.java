package positioningmap;

import java.util.ArrayList;
import java.util.List;

public class ConditionValue implements Cloneable {

	private Boolean mandatory = true;
	private List<ConditionElement> elements = new ArrayList<>();
	public ConditionValue(List<ConditionElement> v) {
		this.elements = v;
	}
	
	public ConditionValue() {}
	
	public Boolean getMandatory() {
		return mandatory;
	}
	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}
	public List<ConditionElement> getElements() {
		return elements;
	}
	public void setElements(List<ConditionElement> elements) {
		this.elements = elements;
	}

	@Override
	protected ConditionValue clone() {
		ConditionValue ret;
		try {
			ret = (ConditionValue)super.clone();
			ret.elements = new ArrayList<ConditionElement>();
			this.elements.forEach(c -> {
				ret.elements.add(c.clone());
			});
			return ret;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
}
