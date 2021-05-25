package positioningmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductSpec implements Cloneable {

	private Map<String, SpecHolder> values = new HashMap<>();
	private Set<String> conditions = new LinkedHashSet<String>();
	
	private SpecHolderInterface specHolderInterface = new SpecHolderInterface() {
		@Override
		public void onAddCondition(String string) {
			conditions.add(string);
		}
	};
	
	public Map<String, SpecHolder> getValues() {
		return values;
	}

	public void setValues(Map<String, SpecHolder> values) {
		this.values = values;
	}

	public ProductSpec() {}

	private void guarantee(String id, SpecValue v) {
		SpecHolder specHolder = specHolder(id);
		specHolder.guarantee(v);		
	}
	
	private void createTypical(String id, SpecValue v) {
		SpecHolder specHolder = specHolder(id);
		specHolder.typical(v);		
	}

	public void typical(String id, double v) {
		createTypical(id, new SpecValue(v));
	}
	
	SpecHolder specHolder(String id) {
		if (!this.values.containsKey(id)) {
			this.values.put(id, new SpecHolder(specHolderInterface));
		}
		SpecHolder specHolder = this.values.get(id);
		return specHolder;
	}
	
	public void createGuarantee(String id, double x, double y) {
		guarantee(id, new SpecValue(x, y));
	}

	public void guarantee(String id, double v) {
		guarantee(id, new SpecValue(v));
	}

	public void guarantee(String id, boolean b) {
		guarantee(id, new SpecValue(b));
	}

	public void guarantee(String id, String string) {
		guarantee(id, new SpecValue(string));
	}

	public void createTypical(String id, double x, double y) {
		createTypical(id, new SpecValue(x, y));
	}

	@Override
	public ProductSpec clone() {
		try {
			ProductSpec ret = (ProductSpec)super.clone();
			ret.values = new LinkedHashMap<String, SpecHolder>();
			for (Map.Entry<String, SpecHolder> entry: this.values.entrySet()) {
				ret.values.put(entry.getKey(), entry.getValue().clone());
			}
			ret.conditions = new LinkedHashSet<String>(this.conditions);
			return ret;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	public void clean(Collection<String> useIds) {
		List<String> removes = new ArrayList<>();
		this.values.keySet().forEach(id -> {
			if (!useIds.contains(id)) {
				removes.add(id);
			}
		});
		removes.forEach(id -> {
			values.remove(id);
		});
		
		values.forEach((id, v) -> {
			SpecValue value = v.typical();
			if (value == null) {
				return;
			}
			if (value.initialized()) {
				value.setDefined(false);
			}
		});
	}

	public void setConditions(Set<String> conditions) {
		this.conditions = conditions;
	}

	public Collection<String> getConditions() {
		return conditions;
	}

	public void init() {
		this.values.forEach((id, specHolder) -> {
			specHolder.setSpecHolderInterface(specHolderInterface);
		});
	}

	public void copy(ProductSpec from, String id) {
		SpecHolder specHolder = from.getValues().get(id).clone();
		this.values.put(id, specHolder);
		specHolder.setSpecHolderInterface(specHolderInterface);
		specHolder.getSpecs().forEach(((condition, element) -> {
			specHolderInterface.onAddCondition(condition);
		}));
	}
}
