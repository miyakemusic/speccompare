package positioningmap;

public class ConditionElement {
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
}