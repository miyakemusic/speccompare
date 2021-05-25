package positioningmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import positioningmap.Main.Better;
import positioningmap.Main.SpecTypeEnum;

interface FilterInterface {

	boolean categoryEnabled(String category);

	boolean productEnabled(String product);

	boolean qualified(String product, ProductSpec value);
	
}
public class SpecSheet {
			
	private String product;
	private Map<String, ProductSpec> productSpecs = new HashMap<>();
	private Map<String, SpecCategory> categories = new LinkedHashMap<>();
	private FilterInterface filterInterface;


	public SpecSheet() {}
	
	public SpecSheet(String product) {
		this.product = product;
	}

	public void setFilter(FilterInterface filterInterface) {
		this.filterInterface = filterInterface;
	}
	
	public SpecDef addSpec(String specCategory, String specName, SpecTypeEnum specType, String unit, Better better) {
		SpecDef ret = getCategory(specCategory).createSpec(specName, specType, unit, better);
		return ret;
	}


	public SpecDef newSpec() {
		if (this.categories.size() > 0) {
			String cat = this.categories.keySet().iterator().next();
			SpecDef ret = addSpec(cat, "New Spec", SpecTypeEnum.Numeric, "", Better.None);
			categories.get(cat).init(specInterface);
			return ret;
		}
		else {
			return this.addSpec("Category", "New Spec Name", SpecTypeEnum.Numeric, "", Better.None);
		}
	}
	
	private SpecCategory getCategory(String specCategory) {
		if (!this.categories.containsKey(specCategory)) {
			SpecCategory newCategory = new SpecCategory();
			this.categories.put(specCategory, newCategory);
		}
		SpecCategory category = this.categories.get(specCategory);
		return category;
	}

	public SpecDef addSpec(String specCategory, String specName, SpecTypeEnum specType) {
		SpecCategory category = getCategory(specCategory);
		SpecDef ret = category.createSpec(specName, specType, "", null);
		category.init(specInterface);
		return ret;
	}

	public List<String> filteredCategories() {
		List<String> ret = new ArrayList<>();
		this.categories.keySet().forEach(cat -> {
			if (filterInterface.categoryEnabled(cat)) {
				ret.add(cat);
			}
		});
		return ret;
	}

	public Map<String, SpecDef> getSpecs(String category) {
		return this.categories.get(category).getSpecs();
	}

	private SpecCategoryInterface specInterface = new SpecCategoryInterface() {
		@Override
		public String category(SpecCategory specCategory) {
			for (Map.Entry<String, SpecCategory> entry : categories.entrySet()) {
				if (entry.getValue().equals(specCategory)) {
					return entry.getKey();
				}
			}
			return null;
		}

		@Override
		public void onCategoryChange(SpecCategory specCategory, String category, SpecDef specDef) {
			if (!categories.containsKey(category)) {
				SpecCategory newCategory = new SpecCategory();
				newCategory.init(specInterface);
				categories.put(category, newCategory);
			}

			categories.get(category).add(specDef);	
		}
	};

	public ProductSpec addProduct(String vendorName, String modelName) {
		ProductSpec newSpec = new ProductSpec();
		productSpecs.put(modelName, newSpec);
		return newSpec;
	}

	public Map<String, ProductSpec> products() {
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
		this.productSpecs.values().forEach(v ->{v.init();});
		this.categories.values().forEach(v -> { v.init(specInterface);});
	}

	public SpecDef specDef(String id) {
		return find(id);
	}

	public SpecDef find(String id) {
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
		SpecHolder specHolder = spec.specHolder(id);
		if (specHolder == null) {

		}
		return specHolder;
	}

	public List<String> units() {
		Set<String> ret = new HashSet<>();
		this.categories.values().forEach(v -> {
			v.getSpecs().values().forEach(vv -> {
				ret.add(vv.getUnit());
			});
		});
		return new ArrayList<String>(ret);
	}

	public void moveUp(String category) {
		new MapMover<String, SpecCategory>(this.categories).moveUp(this.getCategory(category));
	}
	public void moveDown(String category) {
		new MapMover<String, SpecCategory>(this.categories).moveDown(this.getCategory(category));
	}

	public void changeProductName(String oldName, String newName) {
		new MapCopier<String, ProductSpec>(this.productSpecs) {
			@Override
			protected void handle(String key, ProductSpec value, Map<String, ProductSpec> newMap) {
				if (key.equals(oldName)) {
					newMap.put(newName, value);
				}
				else {
					newMap.put(key, value);
				}	
			}
		};
	}

	public void copyProduct(String name) {		
		new MapCopier<String, ProductSpec>(this.productSpecs) {
			@Override
			protected void handle(String key, ProductSpec value, Map<String, ProductSpec> newMap) {
				newMap.put(key, value);
				if (key.equals(name)) {
					String[] tmp = key.split("\n");
					String newName = tmp[0];
					if (tmp.length > 1) {
						newName += "\n" + tmp[1];
					}
					if (tmp.length > 2) {
						newName += "\n" + tmp[2];
					}

					newMap.put(newName + "_copied", value.clone());	
				}
			}
		};
	}

	public void moveLeft(String name) {
		new MapMover<String, ProductSpec>(this.productSpecs).moveUp(productSpecs.get(name));
	}

	public void moveRight(String name) {
		new MapMover<String, ProductSpec>(this.productSpecs).moveDown(productSpecs.get(name));
	}

	public void copyCells(List<String> fromRows, String fromColumn, String toColumn) {
//		List<String> producs = new ArrayList<String>(this.productSpecs.keySet());
		ProductSpec from = this.productSpecs.get(fromColumn);
		ProductSpec to = this.productSpecs.get(toColumn);
		for (String id : fromRows) {
			to.copy(from, id);
//			SpecHolder sh = from.getValues().get(id);
//			if (sh != null) {
//				to.getValues().put(id, sh.clone());
//			}
		}
	}

	public Map<String, String> booleanIds() {
		Map<String, String> ret = new LinkedHashMap<>();
		this.categories.forEach((k,v) -> {
			v.getSpecs().forEach((kk, vv) -> {
				if (vv.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0) {
					ret.put("[" + k + "]" + kk, vv.getId());
				}
			});
		});
		return ret;
	}

	public Collection<String> allIds() {
		List<String> ret = new ArrayList<>();
		this.categories.forEach((k,v) -> {
			v.getSpecs().forEach((kk, vv) -> {
				ret.add(vv.getId());
			});
		});
		return ret;
	}
	
	public void delete(String category, String id) {
		for (Map.Entry<String, SpecDef> entry : categories.get(category).getSpecs().entrySet()) {
			if (entry.getValue().getId().equals(id)) {
				categories.get(category).getSpecs().remove(entry.getKey());
				return;
			}
		}
	}
	
	public void copySpec(String category, String id) {
		for (Map.Entry<String, SpecDef> entry : categories.get(category).getSpecs().entrySet()) {
			if (entry.getValue().getId().equals(id)) {
				SpecDef specDef = entry.getValue();
				this.addSpec(category, "Copy of "+ specDef.getName(), specDef.getSpecType(), specDef.getUnit(), specDef.getBetter());
				return;
			}
		}

	}

	public Collection<String> vendors() {
		Set<String> ret = new HashSet<>();
		this.productSpecs.keySet().forEach(k -> {
			ret.add(k.split("\n")[0]);
		});
		return ret;
	}

	public void clean() {
		List<String> keys = new ArrayList<>();
		this.categories.forEach((k,v) ->{
			if (v.getSpecs().size() == 0) {
				keys.add(k);
			}
		});
		keys.forEach(key -> {
			categories.remove(key);
		});
		
		this.products().forEach((productName, productSpec) ->{
			productSpec.clean(allIds());
		});
	}


	public Map<String, ProductSpec> filteredProducts() {
		Map<String, ProductSpec> ret = new LinkedHashMap<>();
		for (Map.Entry<String, ProductSpec> entry : this.productSpecs.entrySet()) {
			if (this.filterInterface.productEnabled(entry.getKey())) {
				if (this.filterInterface.qualified(entry.getKey(), entry.getValue())) {
					ret.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return ret;
	}

	public Collection<? extends String> categories() {
		return this.categories.keySet();
	}

	public void clearValue(String id, String model) {
		this.getValue(id, model).clearValue();
	}

	public Collection<String> conditionList(String productName) {
		return this.productSpecs.get(productName).getConditions();
	}

}
