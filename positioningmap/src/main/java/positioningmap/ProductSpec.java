package positioningmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProductSpec implements Cloneable {

	private Map<String, SpecHolder> values = new HashMap<>();
//	private List<String> conditions = new ArrayList<String>();
	
	private ConditionContainer conditionContainer = new ConditionContainer();
	
	private SpecHolderInterface specHolderInterface;
	
	public Map<String, SpecHolder> getValues() {
		return values;
	}

	public void setValues(Map<String, SpecHolder> values) {
		this.values = values;
	}

	public ProductSpec() {
		createSpecHolderInterface();
	}
	
	private void createSpecHolderInterface() {
		specHolderInterface = new SpecHolderInterface() {
			@Override
			public void onAddCondition(String string) {
				conditionContainer.addCondition(string);
//				if (!conditions.contains(string)) {
//					conditions.add(string);
//				}
			}
		};
	}

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
	
	public SpecHolder specHolder(String id) {
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
			//ret.conditions = new ArrayList<String>(this.conditions);
			ret.conditionContainer = this.conditionContainer.clone();
			ret.init();
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

//	public void setConditions(List<String> conditions) {
		
//		conditions.forEach(c -> {
//			conditionContainer.addCondition(c);
//		});
//		System.out.println(conditions);
//		this.conditions = conditions;
//		this.conditionContainer = new ConditionContainer(conditions);
//	}

//	public Collection<String> getConditions() {
//		return conditions;
//	}

	public void init() {
		createSpecHolderInterface();
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

	public ConditionContainer replaceCondtionName(String prevString, String newString) {
//		this.conditions.remove(prevString);
//		this.conditions.add(newString);
//		this.values.forEach((id, specHolder) -> {
//			specHolder.replaceCondition(prevString, newString);
//		});
//		Collections.sort(this.conditions);
//		return conditions;
		this.conditionContainer.replace(prevString, newString);
		return this.conditionContainer;
	}

	public ConditionContainer getConditionContainer() {
		return conditionContainer;
	}

	public void setConditionContainer(ConditionContainer conditionContainer) {
		this.conditionContainer = conditionContainer;
	}
	
}
