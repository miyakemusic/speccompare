package positioningmap;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpecContainer {
	private Map<String, MtSpecItem> specs = new LinkedHashMap<>();
	
	public void add(String subSpecName, MtSpecItem specItem) {
		specs.put(subSpecName, specItem);
	}

	public Map<String, MtSpecItem> specs() {
		return this.specs;
	}
}
