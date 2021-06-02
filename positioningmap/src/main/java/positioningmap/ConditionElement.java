package positioningmap;

public class ConditionElement implements Cloneable {
	public ConditionElement() {}
	public ConditionElement(String string) {
		String[] tmp = string.split("[()]+");
		this.value = tmp[0];
		if (tmp.length > 1) {
			this.description = tmp[1];
		}
	}
	public String value;
	public String description;
	@Override
	protected ConditionElement clone() {
		try {
			return (ConditionElement)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}