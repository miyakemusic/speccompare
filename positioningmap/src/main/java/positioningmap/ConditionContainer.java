package positioningmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class ConditionContainer implements Cloneable {
//	private Map<String, List<ConditionElement>> map = new LinkedHashMap<>();
	private Map<String, ConditionValue> conditions = new LinkedHashMap<>();
	public ConditionContainer() {}
//	public ConditionContainer(List<String> conditions) {
//		Collections.sort(conditions);
//		conditions.forEach(c -> {
//			if (c.contains("@")) {
//				String[] tmp = c.split("@");
//				
//				if (!map.containsKey(tmp[0])) {
//					map.put(tmp[0], new ArrayList<ConditionElement>());
//				}
//				map.get(tmp[0]).add(new ConditionElement(tmp[1]));
//			}
//		});
//	}

	public List<String> conditionNameList() {
		return new ArrayList<String>(conditions.keySet());
	}

	public List<String> getValues(String conditionName) {
		List<String> ret = new ArrayList<>();
		conditions.get(conditionName).getElements().forEach(v -> {
			ret.add(v.value);
		});
		return ret;
	}
	
//	@JsonIgnore
//	public Map<String, List<ConditionElement>> getMap() {
//		return map;
//	}
//	
//	@JsonIgnore
//	public void setMap(Map<String, List<ConditionElement>> map) {
//		this.map = map;
//	}

	public Map<String, ConditionValue> getConditions() {
		return conditions;
	}

	public void setConditions(Map<String, ConditionValue> conditions) {
		this.conditions = conditions;
	}

	public void addCondition(String string) {
		if (string.contains("@")) {
			String[] tmp = string.split("@");
			
			if (!conditions.containsKey(tmp[0])) {
				conditions.put(tmp[0], new ConditionValue());
			}
			conditions.get(tmp[0]).getElements().add(new ConditionElement(tmp[1]));
		}
	}

	@Override
	protected ConditionContainer clone()  {
		try {
			ConditionContainer ret = (ConditionContainer)super.clone();
			ret.conditions = new LinkedHashMap<String, ConditionValue>();
			this.conditions.forEach((k, v) -> {
				ret.conditions.put(k, v.clone());
			});
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void replace(String prevString, String newString) {
		String[] tmp = prevString.split("@");
		this.conditions.remove(tmp[0]);
		this.addCondition(newString);
	}

	public void updateValue(String condition, String value, String newValue) {
		ConditionValue v = this.conditions.get(condition);
		for (ConditionElement e: v.getElements()) {
			if (e.value.equals(value)) {
				e.value = newValue;
				break;
			}
		}
	}

	public void updateDescription(String condition, String value, String description, String string) {
		ConditionValue v = conditions.get(condition);
		for (ConditionElement e: v.getElements()) {
			if (e.value.equals(value)) {
				e.description = string;
				break;
			}
		}	
	}

	public void updateCondition(String condition, String string) {
		ConditionValue v = conditions.get(condition);
		conditions.remove(condition);
		conditions.put(string, v);
	}

	public void addValue(String condition, String string) {
		this.conditions.get(condition).getElements().add(new ConditionElement(string));
	}

	public void removeCondition(String condition, String value) {
		ConditionValue v = conditions.get(condition);
		for (ConditionElement e: v.getElements()) {
			if (e.value.equals(value)) {
				v.getElements().remove(e);
				break;
			}
		}
	}

	public void updateMandatory(String condition, Boolean mandatory, Boolean aValue) {
		this.conditions.get(condition).setMandatory(aValue);
	}
	
}
