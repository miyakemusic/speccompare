package positioningmap;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

interface SpecHolderInterface {

	void onAddCondition(String string);
	
}
public class SpecHolder implements Cloneable {

	public static final String DEFAULT = "Default";
	private SpecHolderInterface specHolderInterface;
	
	private Map<String, SpecHolderElement> specs = new LinkedHashMap<>();
	
	public SpecHolder() {
		specs.put(DEFAULT, new SpecHolderElement());
	}
	public SpecHolder(SpecHolderInterface specHolderInterface2) {
		this.specHolderInterface = specHolderInterface2;
		specs.put(DEFAULT, new SpecHolderElement());
	}
	
	@JsonIgnore
	public void guarantee(SpecValue v) {
		specs.get(DEFAULT).guarantee(v);
	}
	@JsonIgnore
	public void typical(SpecValue v) {
		specs.get(DEFAULT).typical(v);
	}

	@JsonIgnore
	public SpecValue guarantee() {
		if (!specs.containsKey(DEFAULT)) {
			specs.put(DEFAULT, new SpecHolderElement());
		}
		return specs.get(DEFAULT).getGuarantee();
	}
	
	@JsonIgnore
	public SpecValue typical() {
		if (!specs.containsKey(DEFAULT)) {
			specs.put(DEFAULT, new SpecHolderElement());
		}
		return specs.get(DEFAULT).getTypical();
	}
	

	public Map<String, SpecHolderElement> getSpecs() {
		return specs;
	}

	public void setSpecs(Map<String, SpecHolderElement> specs) {
		this.specs = specs;
	}

	@Override
	public SpecHolder clone() {
		try {
			SpecHolder ret = (SpecHolder)super.clone();

			ret.specs = new LinkedHashMap<>();
			this.specs.forEach((k,v) -> {
				ret.specs.put(k, v.clone());
			});
			ret.specHolderInterface = this.specHolderInterface;
			return ret;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void clearValue() {
		specs.forEach( (k, v) -> {
			v.clearValue();
		});
	}

	public void addCondition(String string) {
		this.specs.put(string, new SpecHolderElement());
		this.specHolderInterface.onAddCondition(string);
	}
	public SpecHolderElement defaultSpec() {
		return this.getSpecs().get(DEFAULT);
	}
	
	@JsonIgnore
	public void setSpecHolderInterface(SpecHolderInterface specHolderInterface) {
		this.specHolderInterface = specHolderInterface;
	}
	
}
