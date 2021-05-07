package positioningmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class UseCaseContainer {
	private Map<String, UseCaseDef> pmDefs = new LinkedHashMap<>(); // Usercase, Def
	
	public void add(String def) {
		pmDefs.put(def, new UseCaseDef());
	}

	public List<String> defs() {
		return new ArrayList<String>(pmDefs.keySet());
	}

	public UseCaseDef get(int i) {
		return get(defs().get(i));
	}

	public UseCaseDef get(String usecase) {
		if (!pmDefs.containsKey(usecase)) {
			pmDefs.put(usecase, new UseCaseDef());
		}
		return pmDefs.get(usecase);
	}

	public Map<String, UseCaseDef> getPmDefs() {
		return pmDefs;
	}

	public void setPmDefs(Map<String, UseCaseDef> pmDefs) {
		this.pmDefs = pmDefs;
	}

	public void clean() {
		getPmDefs().forEach((usercaseName, usecase) -> {
			List<String> removes = new ArrayList<String>();
			for (Map.Entry<String, UseCaseDefElement> entry : usecase.getValues().entrySet()) {
				if (!entry.getValue().getDefined()) {
					removes.add(entry.getKey());
				}
			}
			removes.forEach(key -> {
				usecase.getValues().remove(key);
			});
		});
	}
	
	public void init(UseCaseDefInterface useCaseDefInterface) {
		for (Map.Entry<String, UseCaseDef> entry : this.pmDefs.entrySet()) {
			entry.getValue().init(useCaseDefInterface);
		}		
	}
}

interface UseCaseDefInterface {
	String getParentId(String key);
}

class UseCaseDef {
	private Map<String, UseCaseDefElement> values = new HashMap<>(); // spec id, value
	private UseCaseDefInterface useCaseDefInterface;

	public UseCaseDefElement value(String id) {
		if (!values.containsKey(id)) {
			UseCaseDefElement e = new UseCaseDefElement();
			applyListener(id, e);
			values.put(id, e);
		}
		UseCaseDefElement ret = values.get(id);
		return ret;
	}

	public Map<String, UseCaseDefElement> getValues() {
		return values;
	}

	public void setValues(Map<String, UseCaseDefElement> values) {
		this.values = values;
	}

	public List<String> getDefines() {
		List<String> ret = new ArrayList<>();
		for (Map.Entry<String, UseCaseDefElement> entry : this.values.entrySet()) {
			if (entry.getValue().getDefined()) {
				ret.add(entry.getKey());
			}
		}
		return ret;
	}
	
	public void init(UseCaseDefInterface useCaseDefInterface2) {
		this.useCaseDefInterface = useCaseDefInterface2;
		for (Map.Entry<String, UseCaseDefElement> entry : values.entrySet()) {
			String key = entry.getKey();
			UseCaseDefElement value = entry.getValue();
			
			applyListener(key, value);			
		}

	}

	private void applyListener(String key, UseCaseDefElement value) {
		value.setListener(new UseCaseDefElementListener() {
			@Override
			public void onDefineChanged(Boolean defined) {
				String id = key;
				
				while (true) {
					String parentId = useCaseDefInterface.getParentId(id);
					if ((parentId != null) && !parentId.isEmpty()) {
						values.get(parentId).setDefined(true);
						id = parentId;
					}
					else {
						break;
					}
				}
			}
		});
	}
}

interface UseCaseDefElementListener {
	void onDefineChanged(Boolean defined);
}
class NullUseCaseDefElementListener implements UseCaseDefElementListener {
	@Override
	public void onDefineChanged(Boolean defined) {
	}
}

class UseCaseDefElement {
	enum Level {
		Mandatory,
		High_Priority,
		Middle_Priority,
		Optional
	}
	private Boolean defined = false;
	private Level level = Level.Mandatory;
	private Double threshold = 0.0;
	private Double thresholdY = 0.0;
	
	@JsonIgnore
	private UseCaseDefElementListener listener = new NullUseCaseDefElementListener();
	
	public Boolean getDefined() {
		return defined;
	}
	public void setDefined(Boolean defined) {
		this.defined = defined;
		this.listener.onDefineChanged(defined);
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
	
	public Double getThresholdY() {
		return thresholdY;
	}
	public void setThresholdY(Double thresholdY) {
		this.thresholdY = thresholdY;
	}
	@JsonIgnore
	public void setListener(UseCaseDefElementListener listener) {
		this.listener = listener;
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