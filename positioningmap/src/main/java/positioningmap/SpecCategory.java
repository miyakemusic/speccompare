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
			
//			for (Map.Entry<String, SpecDef> entry : specs.entrySet()) {
//				if (entry.getValue().equals(specDef)) {
//					if (!entry.getKey().equals(name)) {
//						specs.remove(entry.getKey());
//						specDef.setSpecInterface(specDefInterface);
//						specs.put(name, specDef);
//					}
//					return;
//				}
//			}
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
	
	public void init(SpecInterface specInterface) {
		this.specInterface = specInterface;
		this.specs.values().forEach(v -> v.setSpecInterface(specDefInterface));
	}
	public void add(SpecDef specDef) {
		this.specs.put(specDef.getName(), specDef);
		specDef.setSpecInterface(specDefInterface);
	}
}

