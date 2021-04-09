package positioningmap;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataSheet {

	public DataSheet(String modelName) {
		// TODO Auto-generated constructor stub
	}
	private String vendorName;
	private String modelName;
	private Map<String, SpecContainer> specs = new LinkedHashMap<>();
	
	private SpecContainer spec(String specName) {
		if (!specs.containsKey(specName)) {
			specs.put(specName, new SpecContainer());
		}
		return specs.get(specName);
	}
	
	public void addSpec(String specName, String subSpecName, MtSpecItem specItem) {
		this.spec(specName).add(subSpecName, specItem);
	}
	public Map<String, SpecContainer> specs() {
		return specs;
	}

	
}
