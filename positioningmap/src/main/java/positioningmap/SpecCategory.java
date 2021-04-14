package positioningmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import positioningmap.Main.Better;
import positioningmap.Main.SpecTypeEnum;

public class SpecCategory {
	@JsonIgnore
	private SpecInterface specInterface;
	private Map<String, SpecDef> specs = new LinkedHashMap<>();

	public SpecDef createSpec(String subSpec, SpecTypeEnum specType, String unit, Better better) {
		SpecDef spec = new SpecDef(specType, unit, better);
		this.specs.put(subSpec, spec);
		return spec;
	}
	public Map<String, SpecDef> getSpecs() {
		return specs;
	}
	public void setSpecs(Map<String, SpecDef> specs) {
		this.specs = specs;
	}
	
	@JsonIgnore
	private SpecDefInterface specDefInterface = new SpecDefInterface() {

		@Override
		public String category(SpecDef specDef) {
			return specInterface.category(SpecCategory.this);
		}

		@Override
		public String name(SpecDef specDef) {
			for (Map.Entry<String, SpecDef> entry : specs.entrySet()) {
				if (entry.getValue().equals(specDef)) {
					return entry.getKey();
				}
			}
			return null;
		}

		@Override
		public void category(SpecDef specDef, String category) {
			if (!category.equals(specInterface.category(SpecCategory.this))) {
				specInterface.onCategoryChange(SpecCategory.this, category, specDef);
				for (String key : specs.keySet()) {
					if (specs.get(key).equals(specDef)) {
						specs.remove(key);
						break;
					}
				}
			}
		}

		@Override
		public void name(SpecDef specDef, String name) {
			for (Map.Entry<String, SpecDef> entry : specs.entrySet()) {
				if (entry.getValue().equals(specDef)) {
					if (!entry.getKey().equals(name)) {
						specs.remove(entry.getKey());
						specDef.setSpecInterface(specDefInterface);
						specs.put(name, specDef);
					}
					return;
				}
			}	
		}

		@Override
		public void moveUp(SpecDef specDef) {	
			new MapMover<String, SpecDef>(specs).moveUp(specDef);
//			if (new ArrayList<SpecDef>(specs.values()).indexOf(specDef) == 0) {
//				return;
//			}
//			Map<String, SpecDef> tmp = new LinkedHashMap<>();
//			String lastKey = "";
//			for (Map.Entry<String, SpecDef> entry : specs.entrySet()) {
//				if (entry.getValue().equals(specDef)) {
//					SpecDef lastValue = tmp.get(lastKey);
//					tmp.remove(lastKey);
//					tmp.put(entry.getKey(), entry.getValue());
//					tmp.put(lastKey, lastValue);
//				}
//				else {
//					tmp.put(entry.getKey(), entry.getValue());
//				}
//				lastKey = entry.getKey();
//			}
//			specs.clear();
//			specs.putAll(tmp);
		}

		@Override
		public void moveDown(SpecDef specDef) {
			new MapMover<String, SpecDef>(specs).moveDown(specDef);
//			if (new ArrayList<SpecDef>(specs.values()).indexOf(specDef) == specs.size()-1) {
//				return;
//			}
//			Map<String, SpecDef> tmp = new LinkedHashMap<>();
//			String key = "";
//			for (Map.Entry<String, SpecDef> entry : specs.entrySet()) {
//				if (entry.getValue().equals(specDef)) {
//					key = entry.getKey();
//					continue;
//				}
//				tmp.put(entry.getKey(), entry.getValue());	
//				if (!key.isBlank())  {
//					tmp.put(key, specs.get(key));
//					key = "";
//				}
//			}
//			specs.clear();
//			specs.putAll(tmp);
		}
	};
	
	public void init(SpecInterface specInterface) {
		this.specInterface = specInterface;
		this.specs.values().forEach(v -> v.setSpecInterface(specDefInterface));
	}
	public void add(SpecDef specDef) {
		this.specs.put(specDef.getName(), specDef);
		specDef.setSpecInterface(specDefInterface);
	}
}

