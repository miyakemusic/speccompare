package positioningmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConditionContainer {
	private Map<String, List<ConditionElement>> map = new HashMap<>();
	
	public ConditionContainer() {}
	public ConditionContainer(Collection<String> conditions) {
		conditions.forEach(c -> {
			if (c.contains("@")) {
				String[] tmp = c.split("@");
				
				if (!map.containsKey(tmp[0])) {
					map.put(tmp[0], new ArrayList<ConditionElement>());
				}
				map.get(tmp[0]).add(new ConditionElement(tmp[1]));
			}
		});
	}

	public List<String> conditionNameList() {
		return new ArrayList<String>(map.keySet());
	}

	public List<String> getValues(String conditionName) {
		List<String> ret = new ArrayList<>();
		map.get(conditionName).forEach(v -> {
			ret.add(v.value);
		});
		return ret;
	}
	public Map<String, List<ConditionElement>> getMap() {
		return map;
	}
	public void setMap(Map<String, List<ConditionElement>> map) {
		this.map = map;
	}
	
}
