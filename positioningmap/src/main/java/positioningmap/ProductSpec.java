package positioningmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import positioningmap.Main.SpecTypeEnum;

public class ProductSpec implements Cloneable {

	private String productName;
	private Map<String, SpecHolder> values = new HashMap<>();
	private SpecInterface specInterface;
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Map<String, SpecHolder> getValues() {
		return values;
	}

	public void setValues(Map<String, SpecHolder> values) {
		this.values = values;
	}

	public ProductSpec() {}
	
	public ProductSpec(String productName, SpecInterface specInterface2) {
		this.productName = productName;
		this.specInterface = specInterface2;
	}

	private void guarantee(String id, SpecValue v) {
		SpecHolder specHolder = specHolder(id);
		specHolder.guarantee(v);		
	}
	
	private void typical(String id, SpecValue v) {
		SpecHolder specHolder = specHolder(id);
		specHolder.typical(v);		
	}

	public void typical(String id, double v) {
		typical(id, new SpecValue(v));
	}
	
	private SpecHolder specHolder(String id) {
		if (!this.values.containsKey(id)) {
			this.values.put(id, new SpecHolder());
		}
		SpecHolder specHolder = this.values.get(id);
		return specHolder;
	}
	
	public void guarantee(String id, double x, double y) {
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

	public String value(String id) {
		if (this.values.containsKey(id) ) {
			String ret = "";
			
			SpecValue guarantee = this.values.get(id).getGuarantee();
			if (guarantee.getDefined()) {
				if (guarantee != null) {
					ret += generateText(id, guarantee);
				}
			}

			String g = "";
			SpecValue typical = this.values.get(id).getTypical();
			if (typical.getDefined()) {
				if (typical != null) {
					g += generateText(id, typical) + "(Typ.)";
				}
			}
			if (!ret.isEmpty() && !g.isEmpty()) {
				ret = ret + " / " + g;
			}
			else {
				ret = ret + g;
			}
			return ret;
		}
		return "---";
	}

	private String generateText(String id, SpecValue guarantee) {
		SpecValue specValue = guarantee;
		if (this.specInterface.type(id).compareTo(SpecTypeEnum.Range) == 0) {
//			if (Math.abs(specValue.getX()) == Math.abs(specValue.getY())) {
//				return "Å}" + Math.abs(specValue.getX());
//			}
			return specValue.getX() + " ... " + specValue.getY();
		}
		else if (this.specInterface.type(id).compareTo(SpecTypeEnum.Variation) == 0) {
			return "Å}" + specValue.getX();
		}
		else if (this.specInterface.type(id).compareTo(SpecTypeEnum.TwoDmensionalSize) == 0) {
			return specValue.getX() + " x " + specValue.getY();
		}
		else {
			return specValue.text();
		}
	}

	public void typical(String id, double x, double y) {
		typical(id, new SpecValue(x, y));
	}

	@JsonIgnore
	public void init(SpecInterface specInterface) {
		this.specInterface = specInterface;
	}

	@Override
	public ProductSpec clone() {
		try {
			ProductSpec ret = (ProductSpec)super.clone();
			ret.specInterface = this.specInterface;
			ret.productName = new String(this.productName);
		
			ret.values = new LinkedHashMap<String, SpecHolder>();
			for (Map.Entry<String, SpecHolder> entry: this.values.entrySet()) {
				ret.values.put(entry.getKey(), entry.getValue().clone());
			}
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
	}
}
