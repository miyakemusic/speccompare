package positioningmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import positioningmap.PMConfigValue.Level;

public class PMDefContainer {
	private Map<String, PMDefUsercase> pmDefs = new LinkedHashMap<>(); // Usercase, Def
	
	public void add(String def) {
		pmDefs.put(def, new PMDefUsercase());
	}

	public List<String> defs() {
		return new ArrayList<String>(pmDefs.keySet());
	}

	public PMDefUsercase get(int i) {
		return get(defs().get(i));
	}

	public PMDefUsercase get(String usecase) {
		if (!pmDefs.containsKey(usecase)) {
			pmDefs.put(usecase, new PMDefUsercase());
		}
		return pmDefs.get(usecase);
	}
}
class PMDefUsercase {
	private Map<String, PMConfigValue> values = new HashMap<>(); // spec id, value
	public PMDefUsercase() {
		
	}
	public PMConfigValue value(String id) {
		if (!values.containsKey(id)) {
			values.put(id, new PMConfigValue());
		}
		PMConfigValue ret = values.get(id);
		return ret;
	}
}

class PMConfigValue {
	enum Level {
		Mandatory,
		NiceToHave
	}
	private Boolean defined = false;
	private Level level = Level.Mandatory;
	private Double threshold = 0.0;
	
	public Boolean getDefined() {
		return defined;
	}
	public void setDefined(Boolean defined) {
		this.defined = defined;
	}
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	public Double getThreshold() {
		return threshold;
	}
	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}
	
	@Override
	public String toString() {
		if (this.defined) {
			return level.toString() + "@" + threshold;
		}
		else {
			return "";
		}
	}
	
}