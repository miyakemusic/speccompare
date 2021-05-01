package positioningmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import positioningmap.Main.Better;
import positioningmap.Main.SpecTypeEnum;

interface ScoreCalculatorInterface {

	List<String> specNames();

	SpecDef specDef(String specName);

	List<SpecHolder> allProductSpec(String id);

	List<String> productNames();

	SpecHolder getSpec(String productName, String id);
	
}
public class ScoreCalculator {
	private Map<String, CalcResult> result = new HashMap<>();
	public ScoreCalculator(SpecSheet specSheet, List<String> targets) {
		Map<String, CalcResult> valueRange = new HashMap<>();
		String category = "OTDR";
		
		SpecCategory categories = specSheet.getCategories().get(category);
		
		Map<String, SpecDef> specs = new HashMap<>();
		targets.forEach(t -> {
			specs.put(t, categories.getSpecs().get(t));
		});
		
		// At first target product should be filtered. 
		// Product does not satisfy mandatory requirements should be removed
		Set<String> notSatistied = new HashSet<>();
		for (Map.Entry<String, ProductSpec> entry : specSheet.getProductSpecs().entrySet()) {
			String productName = entry.getKey();
			ProductSpec productSpec = entry.getValue();
			for (String targetSpecName : targets) {
				SpecDef specDef = specSheet.getCategories().get(category).getSpecs().get(targetSpecName);
				if (specDef.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0) {
					SpecHolder specHolder = productSpec.getValues().get(specDef.id());
					if (!specHolder.getGuarantee().getAvailable()) {
						notSatistied.add(productName);
						break;
					}
				}				
			}
		}
				
		for (Map.Entry<String, SpecDef> entry: specs.entrySet()) {
			SpecDef specDefY = entry.getValue();
			if (specDefY.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0) {
				continue;
			}
			Map<String, SpecHolder> allProduct = new HashMap<>();
			specSheet.products().forEach((k, v) -> {
				if (!notSatistied.contains(k)) {
					allProduct.put(k, v.getValues().get(specDefY.getId()));
				}
			});
			
			CalcResult minMax = new CalcResult();
			valueRange.put(entry.getKey(), minMax);
			
			for (Map.Entry<String, SpecHolder> productEntry : allProduct.entrySet()) {
				SpecHolder specHolder = productEntry.getValue();
				if (specHolder == null) {
					System.out.println(entry.getKey() + "." + productEntry.getKey());
					continue;
				}
				
				CalcResult res = calcMinMax(specDefY, specHolder);
				minMax.merge(res);	
				
				if (res.min == Double.NaN || res.max == Double.NaN) {
					System.out.println();
				}	
				System.out.println(entry.getKey() + "\n" + productEntry.getKey() + "min=" + res.min + " max=" + res.max);
			}

		}
		
		
		for (Map.Entry<String, ProductSpec> entry : specSheet.getProductSpecs().entrySet()) {
			if (notSatistied.contains(entry.getKey())) {
				continue;
			}
			CalcResult score = new CalcResult();
			score.min = 0.0;
			score.max = 0.0;
			result.put(entry.getKey(), score);
			for (String target : targets) {
				SpecDef specDef = specSheet.getCategories().get(category).getSpecs().get(target);
				SpecHolder specHolder = entry.getValue().getValues().get(specDef.id());
				if ((specHolder == null) || (specDef.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0)) {
					continue;
				}
				CalcResult minMax = calcMinMax(specDef, specHolder);

				CalcResult ref = valueRange.get(target);
				
				if (ref.range() > 0.0) {
					double vmin = (minMax.min - ref.min) / (ref.max - ref.min) - (ref.max - ref.min) * 0.0005;
					double vmax = (minMax.max - ref.min) / (ref.max - ref.min) + (ref.max - ref.min) * 0.0005;
					
	
					score.min += vmin;				
					score.max += vmax;
				}
				System.out.println();
			}
			System.out.println();
		}
		System.out.println();
	}

	private CalcResult calcMinMax(SpecDef specDef, SpecHolder specHolder) {
		CalcResult minMax = new CalcResult();
		SpecValue specValue = specHolder.getGuarantee();
		if ((specValue == null) || !specValue.getDefined()) {
			specValue = specHolder.getTypical();
		}
		if (!specValue.getDefined()) {
			return minMax;
		}
		
		if (specDef.getSpecType().compareTo(SpecTypeEnum.Range) == 0) {
			if (specDef.getBetter().compareTo(Better.Narrower) == 0) {
				double width = specValue.getY() - specValue.getX();
				minMax.setValue(-width);
			}
			else if (specDef.getBetter().compareTo(Better.Wider) == 0) {
				double width = specValue.getY() - specValue.getX();
				minMax.setValue(width);				
			}
			else if (specDef.getBetter().compareTo(Better.Higher) == 0) {
				minMax.setValue(specValue.getX());
				minMax.setValue(specValue.getY());
			}
			else if (specDef.getBetter().compareTo(Better.Lower) == 0) {
				minMax.setValue(-specValue.getX());
				minMax.setValue(-specValue.getY());					
			}
			else if (specDef.getBetter().compareTo(Better.None) == 0) {
				
			}
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Numeric) == 0) {
			if (specDef.getBetter().compareTo(Better.Narrower) == 0) {
				System.out.println("Invalid Condition");
			}
			else if (specDef.getBetter().compareTo(Better.Wider) == 0) {
				System.out.println("Invalid Condition");
			}
			else if (specDef.getBetter().compareTo(Better.Higher) == 0) {
				minMax.setValue(specValue.getX());
			}
			else if (specDef.getBetter().compareTo(Better.Lower) == 0) {
				minMax.setValue(-specValue.getX());
			}
			else if (specDef.getBetter().compareTo(Better.None) == 0) {
				minMax.setValue(specValue.getX());
			}				
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0) {
			//minMax.setValid(specValue.getAvailable());
		}
		return minMax;
	}

	public CalcResult calc(String product) {
		return result.get(product);
	}

}
class CalcResult {
	public double min = Double.POSITIVE_INFINITY;
	public double max = Double.NEGATIVE_INFINITY;
//	private Boolean valid = true;
	
	public void setValue(double value) {
		if ((value != Double.POSITIVE_INFINITY) && (value != Double.NEGATIVE_INFINITY)) {
			this.min = Math.min(value, min);
			this.max = Math.max(value, max);
		}
		else {
			System.err.println(this.getClass().getName() + " Invalid Value");
		}
	}

	public double range() {
		return this.max - this.min;
	}

	public void merge(CalcResult ref) {
		if (ref.min != Double.POSITIVE_INFINITY) {
			this.min = Math.min(ref.min, this.min);
		}
		if (ref.max != Double.NEGATIVE_INFINITY) {
			this.max = Math.max(ref.max, this.max);
		}
	}
	
}