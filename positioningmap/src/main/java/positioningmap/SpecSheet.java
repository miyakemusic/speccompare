package positioningmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import positioningmap.Main.Better;
import positioningmap.Main.SpecTypeEnum;

public class SpecSheet {
			
	private String product;
	private Map<String, ProductSpec> productSpecs = new HashMap<>();
	
	private Map<String, SpecCategory> categories = new LinkedHashMap<>();
	
	public SpecSheet() {}
	
	public SpecSheet(String product) {
		this.product = product;
	}

	public SpecDef addSpec(String specCategory, String specName, SpecTypeEnum specType, String unit, Better better) {
		return getCategory(specCategory).createSpec(specName, specType, unit, better);
	}

	private SpecCategory getCategory(String specCategory) {
		if (!this.categories.containsKey(specCategory)) {
			this.categories.put(specCategory, new SpecCategory());
		}
		SpecCategory category = this.categories.get(specCategory);
		return category;
	}

	public SpecDef addSpec(String specCategory, String specName, SpecTypeEnum specType) {
		return getCategory(specCategory).createSpec(specName, specType, "", null);
	}

	public Set<String> categories() {
		return this.categories.keySet();
	}

	public Map<String, SpecDef> getSpecs(String category) {
		return this.categories.get(category).getSpecs();
	}

	private SpecInterface specInterface = new SpecInterface() {
		@Override
		public SpecTypeEnum type(String id) {
			return find(id).getSpecType();
		}

		@Override
		public String category(SpecCategory specCategory) {
			for (Map.Entry<String, SpecCategory> entry : categories.entrySet()) {
				if (entry.getValue().equals(specCategory)) {
					return entry.getKey();
				}
			}
			return null;
		}

	};
	
	public ProductSpec addProduct(String vendorName, String modelName) {
		ProductSpec newSpec = new ProductSpec(modelName, specInterface);
		productSpecs.put(modelName, newSpec);
		return newSpec;
	}

	public Map<String, ProductSpec> products() {
		// TODO Auto-generated method stub
		return this.productSpecs;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public Map<String, ProductSpec> getProductSpecs() {
		return productSpecs;
	}

	public void setProductSpecs(Map<String, ProductSpec> productSpecs) {
		this.productSpecs = productSpecs;
	}

	public Map<String, SpecCategory> getCategories() {
		return categories;
	}

	public void setCategories(Map<String, SpecCategory> categories) {
		this.categories = categories;
	}

	public void init() {
		this.productSpecs.values().forEach(v ->{v.init(specInterface);});
		this.categories.values().forEach(v -> { v.init(specInterface);});
	}

	public SpecDef specDef(String id) {
		return find(id);
	}

	private SpecDef find(String id) {
		for (SpecCategory sc : categories.values()) {
			for (SpecDef spec : sc.getSpecs().values()) {
				if (spec.id().equals(id)) {
					return spec;
				}
			}
		}
		return null;
	}

	public SpecHolder getValue(String id, String model) {
		ProductSpec spec = this.productSpecs.get(model);
		SpecHolder specHolder = spec.getValues().get(id);
		if (specHolder == null) {
			spec.getValues().put(id, specHolder = new SpecHolder());
		}
		return specHolder;
	}

}
interface SpecInterface {
	SpecTypeEnum type(String id);

	String category(SpecCategory specCategory);
}