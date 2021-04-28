package positioningmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PMDef {
	private Map<String, PMDefElement> pmDefs = new LinkedHashMap<>();
	
	public void add(String def) {
		pmDefs.put(def, new PMDefElement());
	}

	public List<String> defs() {
		return new ArrayList<String>(pmDefs.keySet());
	}

	public PMDefElement get(int i) {
		return this.pmDefs.get(defs().get(i));
	}
}
class PMDefElement {
	private Map<String, String> values = new HashMap<>();
	public PMDefElement() {
		
	}
	public String id(String id) {
		String ret = values.get(id);
		if (ret == null) {
			ret = "";
		}
		return ret;
	}
}