package positioningmap;

import java.util.HashMap;
import java.util.Map;

public class FilterContainer {
	public static final String Vendors = "Vendors";
	public static final String Categories = "Categories";
	
	private Map<String, Map<String, Boolean>> values = new HashMap<>();
	private String useCaseName = "";
	private boolean useCaseFilter;
	
	public Boolean get(String type, String string) {
		Map<String, Boolean> v = map(type, string);
		return v.get(string);
	}

	private Map<String, Boolean> map(String type, String string) {
		if (!this.values.containsKey(type)) {
			this.values.put(type, new HashMap<String, Boolean>());
		}
		Map<String, Boolean> v = this.values.get(type);
		if (!v.containsKey(string)) {
			v.put(string, true);
		}
		return v;
	}

	public void set(String type, String string, Boolean v) {
		Map<String, Boolean> map = map(type, string);
		map.put(string, v);
	}

	public Map<String, Map<String, Boolean>> getValues() {
		return values;
	}

	public void setValues(Map<String, Map<String, Boolean>> values) {
		this.values = values;
	}

	public void setUseCase(String useCaseName) {
		this.useCaseName = useCaseName;
	}

	public String getUseCaseName() {
		return useCaseName;
	}

	public void setUseCaseName(String useCaseName) {
		this.useCaseName = useCaseName;
	}

	public boolean isUseCaseFilter() {
		return useCaseFilter;
	}

	public void setUseCaseFilter(boolean useCaseFilter) {
		this.useCaseFilter = useCaseFilter;
	}

}
