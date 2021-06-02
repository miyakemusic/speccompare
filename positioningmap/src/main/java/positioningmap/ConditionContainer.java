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
	
}
