package positioningmap;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import positioningmap.Main.Better;
import positioningmap.Main.SpecTypeEnum;
interface SpecCategoryInterface {
	String category(SpecCategory specCategory);
	void onCategoryChange(SpecCategory specCategory, String category, SpecDef specDef);
}
public class SpecCategory {
	@JsonIgnore
	private SpecCategoryInterface specCategoryInterface;
	private Map<String, SpecDef> specs = new LinkedHashMap<>();

	public SpecDef createSpec(String subSpec, SpecTypeEnum specType, String unit, Better better) {
		SpecDef spec = new SpecDef(specType, unit, better);
		this.specs.put(subSpec, spec);
		spec.setSpecDefInterface(specDefInterface);
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
			return specCategoryInterface.category(SpecCategory.this);
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
			if (!category.equals(specCategoryInterface.category(SpecCategory.this))) {
				specCategoryInterface.onCategoryChange(SpecCategory.this, category, specDef);
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
			new MapCopier<String, SpecDef>(specs) {
				@Override
				protected void handle(String key, SpecDef value, Map<String, SpecDef> newMap) {
					if (value.equals(specDef)) {
						newMap.put(name, specDef);
					}
					else {
						newMap.put(key, value);
					}
				}
			};
		}

		@Override
		public void moveUp(SpecDef specDef) {	
			new MapMover<String, SpecDef>(specs).moveUp(specDef);
		}

		@Override
		public void moveDown(SpecDef specDef) {
			new MapMover<String, SpecDef>(specs).moveDown(specDef);
		}
	};
	
	public void init(SpecCategoryInterface specInterface) {
		this.specCategoryInterface = specInterface;
		this.specs.values().forEach(v -> v.setSpecDefInterface(specDefInterface));
	}
	public void add(SpecDef specDef) {
		this.specs.put(specDef.getName(), specDef);
		specDef.setSpecDefInterface(specDefInterface);
	}
}

