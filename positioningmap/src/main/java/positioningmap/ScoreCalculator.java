package positioningmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import positioningmap.Main.Better;
import positioningmap.Main.SpecTypeEnum;
import positioningmap.UseCaseDefElement.Level;

interface ScoreCalculatorInterface {

	List<String> specNames();

	SpecDef specDef(String specName);

	List<SpecHolder> allProductSpec(String id);

	List<String> productNames();

	SpecHolder getSpec(String productName, String id);
	
}
public class ScoreCalculator {
	private Map<String, CalcResult> result = new HashMap<>();
	private CalcResult minMax;
	
	public ScoreCalculator(SpecSheet specSheet, List<SpecDef> targetSpecs, UseCaseDef usecaseDef) {
		Map<String, CalcResult> valueRange = new HashMap<>();		
		// At first target product should be filtered. 
		// Product does not satisfy mandatory requirements should be removed
		Map<String, ProductSpec> targetProducts = filterqualifies(specSheet, targetSpecs, usecaseDef);
				
		try {
			Map<String, CalcResult> scores = new HashMap<>();
			minMax = calcNew(targetSpecs, usecaseDef, valueRange, targetProducts, scores);
			minMax.symmetric();
			
			scores.forEach((productName, value) -> {
				CalcResult mm = scores.get(productName);
				mm.min = mm.getSum() / minMax.max* 80.0 - 8.0;// - minMax.range()/30.0;
				mm.max = mm.getSum() / minMax.max * 80.0 + 8.0; //x\\+ minMax.range()/30.0;
				System.out.println("min=" + mm.min + " max=" + mm.max);
				result.put(productName, mm);
			});
			minMax.max = 100.0;
			minMax.min = -100.0;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, ProductSpec> filterqualifies(SpecSheet specSheet, List<SpecDef> targetSpecs, UseCaseDef usecaseDef) {
		Map<String, ProductSpec> targetProducts = new HashMap<>();
		for (Map.Entry<String, ProductSpec> entry : specSheet.getProductSpecs().entrySet()) {
			String productName = entry.getKey();
			ProductSpec productSpec = entry.getValue();
			boolean target = true;
			for (SpecDef specDef : targetSpecs) {
				UseCaseDefElement useCaseDefElement = usecaseDef.value(specDef.getId());
				SpecValue specValue = specValue(specDef, productSpec);
				if (specValue == null ) {
					if (useCaseDefElement.getLevel().compareTo(Level.Mandatory) == 0) {
						target = false;
						break;						
					}
					else {
						//continue;
						target = false;
						break;	
					}
				}
				
				boolean b = new BasicScoreCalculator().calc(specDef, specValue, useCaseDefElement).value >= 0;
				boolean enabled = true;
				if ((useCaseDefElement.getLevel().compareTo(Level.Mandatory) == 0) && !b) {
					enabled = false;
				}
				if (!enabled) {
					target = false;
					break;
				}
			}
			if (target) {
				targetProducts.put(productName, productSpec);
			}
		}
		return targetProducts;
	}

	private CalcResult calcNew(List<SpecDef> targetSpecs, UseCaseDef usecaseDef,
			Map<String, CalcResult> valueRange, Map<String, ProductSpec> targetProducts, Map<String, CalcResult> scores) throws Exception {

		CalcResult localMinMax = new CalcResult();
		for (String productName : targetProducts.keySet()) {
			ProductSpec productSpec = targetProducts.get(productName); 
			double sum = 0;
			CalcResult mm = new CalcResult();
			for (SpecDef specDef : targetSpecs) {
				SpecValue specValue = specValue(specDef, productSpec);
				if (specValue == null) {
					System.out.println(productName + "::calcNew::" + specDef.getName() + " is null");
					continue;
				}
				if (!specValue.getDefined()) {
					//throw new Exception();
					break;
				}
				UseCaseDefElement useCaseDefElement = usecaseDef.value(specDef.getId());
				DoubleWrapper score  = calcScore(specDef, specValue, useCaseDefElement);
				System.out.println("SCORE:" + productName.replace("\n", "") + "." + specDef.getName() + " = " + score);
				sum += score.value;
				mm.setValue(score.value);
				
			}
			localMinMax.setValue(sum);
			scores.put(productName, mm);
		}
		
		return localMinMax;
	}
	private SpecValue specValue(SpecDef specDef, ProductSpec productSpec) {
		SpecHolder specHolder = productSpec.getValues().get(specDef.getId());
		if (specHolder == null) {
//			System.out.println(productName + "::calcNew::" + specDef.getName() + " is null");
			return null;
		}
		SpecValue specValue = specHolder.getGuarantee();
		if ((specValue == null) || !specValue.getDefined()) {
			specValue = specHolder.getTypical();
		}
		return specValue;
	}
		
	private DoubleWrapper calcScore(SpecDef specDef, SpecValue specValue, UseCaseDefElement useCaseDefElement) {
		return new BasicScoreCalculator().calc(specDef, specValue, useCaseDefElement);
	}

	public CalcResult calc(String product) {
		return this.result.get(product);
	}

	public CalcResult minMax() {
		return this.minMax;
	}
}

class CalcResult {
	public double min = Double.POSITIVE_INFINITY;
	public double max = Double.NEGATIVE_INFINITY;
	public double sum = 0;
	public CalcResult() {}
	
	public void symmetric() {
		double abs = Math.max(Math.abs(min), Math.abs(max));
		this.min = -abs;
		this.max = abs;
	}

	public CalcResult(double min, double max) {
		this.min = min;
		this.max = max;
	}

//	private Boolean valid = true;
	
	public void setValue(double value) {
		if ((value != Double.POSITIVE_INFINITY) && (value != Double.NEGATIVE_INFINITY)) {
			this.min = Math.min(value, min);
			this.max = Math.max(value, max);
			this.sum += value;
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

	public double getSum() {
		return sum;
	}
	
}