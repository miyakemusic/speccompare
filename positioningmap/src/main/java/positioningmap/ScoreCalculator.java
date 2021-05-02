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
	
	public ScoreCalculator(SpecSheet specSheet, List<SpecDef> targetSpecs, UseCaseDef usecaseDef) {
		Map<String, CalcResult> valueRange = new HashMap<>();		
		// At first target product should be filtered. 
		// Product does not satisfy mandatory requirements should be removed
		Set<String> notSatistiedProducts = new HashSet<>();
		Map<String, ProductSpec> targetProducts = new HashMap<>();
		for (Map.Entry<String, ProductSpec> entry : specSheet.getProductSpecs().entrySet()) {
			String productName = entry.getKey();
			ProductSpec productSpec = entry.getValue();
			boolean target = true;
			for (SpecDef specDef : targetSpecs) {
				if (specDef.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0) {
					SpecHolder specHolder = productSpec.getValues().get(specDef.id());
					if (!specHolder.getGuarantee().getAvailable()) {
						notSatistiedProducts.add(productName);
						target = false;
						break;
					}
				}				
			}
			if (target) {
				targetProducts.put(productName, productSpec);
			}
		}
				
		try {
			Map<String, Double> scores = new HashMap<>();
			CalcResult minMax = calcNew(specSheet, targetSpecs, usecaseDef, valueRange, targetProducts, scores);
			
			scores.forEach((productName, value) -> {
				result.put(productName, new CalcResult(value - minMax.range()/50, value + minMax.range()/50));
			});
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private CalcResult calcNew(SpecSheet specSheet, List<SpecDef> targetSpecs, UseCaseDef usecaseDef,
			Map<String, CalcResult> valueRange, Map<String, ProductSpec> targetProducts, Map<String, Double> scores) throws Exception {
		
//		Map<ProductSpec, Double> ret = new HashMap<>();
		CalcResult minMax = new CalcResult();
		for (String productName : targetProducts.keySet()) {
			ProductSpec productSpec = targetProducts.get(productName); 
			double sum = 0;
			for (SpecDef specDef : targetSpecs) {
				SpecHolder specHolder = productSpec.getValues().get(specDef.getId());
				if (specHolder == null) {
					System.out.println(productName + "::calcNew::" + specDef.getName() + " is null");
					continue;
				}
				SpecValue specValue = specHolder.getGuarantee();
				if ((specValue == null) || !specValue.getDefined()) {
					specValue = specHolder.getTypical();
				}
				if (!specValue.getDefined()) {
					throw new Exception();
				}
				UseCaseDefElement useCaseDefElement = usecaseDef.value(specDef.getId());
				double result  = calcScore(specDef, specHolder, useCaseDefElement);
				sum += result;
			}
			minMax.setValue(sum);
			scores.put(productName, sum);
		}
		
		return minMax;
	}
	class DoubleWrapper {
		public double value = 0.0;
	}
	private double calcScore(SpecDef specDef, SpecHolder specHolder, UseCaseDefElement useCaseDefElement) {
		DoubleWrapper ret = new DoubleWrapper();
		SpecValue specValue = specHolder.getGuarantee();
		if ((specValue == null) || !specValue.getDefined()) {
			specValue = specHolder.getTypical();
		}
		new SpecTypeBranch(specDef, specValue) {
			@Override
			protected boolean onVaridation(SpecValue specValue2) {
				return judgeValue(specDef, ret, specValue2);
			}

			@Override
			protected boolean onChoice(SpecValue specValue2) {
				return new BetterBranch(specDef, specValue2) {
					@Override
					protected boolean onWilder(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					protected boolean onNarrower(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					protected boolean onLower(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					protected boolean onHigher(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}	
				}.branch();
			}

			@Override
			protected boolean onRange(SpecValue specValue2) {
				return new BetterBranch(specDef, specValue2) {
					@Override
					protected boolean onWilder(SpecValue specValue2) {
						double width = specValue2.getY() - specValue2.getX();
						return false;
					}

					@Override
					protected boolean onNarrower(SpecValue specValue2) {
						double width = specValue2.getY() - specValue2.getX();
						return false;
					}

					@Override
					protected boolean onLower(SpecValue specValue2) {
						return false;
					}

					@Override
					protected boolean onHigher(SpecValue specValue2) {
						return false;
					}	
				}.branch();
			}

			@Override
			protected boolean onNumeric(SpecValue specValue2) {
				return judgeValue(specDef, ret, specValue2);
			}

			private boolean judgeValue(SpecDef specDef, DoubleWrapper result, SpecValue specValue2) {
				return new BetterBranch(specDef, specValue2) {
					@Override
					protected boolean onWilder(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					protected boolean onNarrower(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					protected boolean onLower(SpecValue specValue2) {
						double value = (specValue2.getX() - useCaseDefElement.getThreshold()) / useCaseDefElement.getThreshold();
						result.value = value;
						return specValue2.getX() < useCaseDefElement.getThreshold();
					}

					@Override
					protected boolean onHigher(SpecValue specValue2) {
						double value = (specValue2.getX() - useCaseDefElement.getThreshold()) / useCaseDefElement.getThreshold();
						result.value = value;
						return specValue2.getX() > useCaseDefElement.getThreshold();
					}
				}.branch();
			}

			@Override
			protected boolean onBoolean(SpecValue specValue2) {
				// TODO Auto-generated method stub
				return false;
			}
			
		}.branch();
		return ret.value;
	}
	
	private void calcOld(SpecSheet specSheet, List<SpecDef> targetSpecs, UseCaseDef usecaseDef,
			Map<String, CalcResult> valueRange, Set<String> notSatistied) {
		for (SpecDef specDefY : targetSpecs) {		
			if (specDefY.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0) {
				continue;
			}
			UseCaseDefElement useCaseDefElement = usecaseDef.value(specDefY.getId());
			
			Map<String, SpecHolder> allProduct = new HashMap<>();
			specSheet.products().forEach((k, v) -> {
				if (!notSatistied.contains(k)) {
					allProduct.put(k, v.getValues().get(specDefY.getId()));
				}
			});
			
			CalcResult minMax = new CalcResult();
			valueRange.put(specDefY.getId(), minMax);
			
			for (Map.Entry<String, SpecHolder> productEntry : allProduct.entrySet()) {
				SpecHolder specHolder = productEntry.getValue();
				if (specHolder == null) {
					//System.out.println(entry.getKey() + "." + productEntry.getKey());
					continue;
				}
				
				CalcResult res = calcMinMax(specDefY, specHolder, useCaseDefElement);
				minMax.merge(res);	
				
				if (res.min == Double.NaN || res.max == Double.NaN) {
					System.out.println();
				}	
				System.out.println(specDefY.getId() + "\n" + productEntry.getKey() + "min=" + res.min + " max=" + res.max);
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
			for (SpecDef specDef : targetSpecs) {
				UseCaseDefElement useCaseDefElement = usecaseDef.getValues().get(specDef.getId());
				
				SpecHolder specHolder = entry.getValue().getValues().get(specDef.id());
				if ((specHolder == null) || (specDef.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0)) {
					continue;
				}
				CalcResult minMax = calcMinMax(specDef, specHolder, useCaseDefElement);

				CalcResult ref = valueRange.get(specDef.getId());
				score.min += minMax.min - (ref.max - ref.min) * 0.05;
				score.max += minMax.max + (ref.max - ref.min) * 0.05;
				
//				CalcResult ref = valueRange.get(specDef.getId());
//				
//				if (ref.range() > 0.0) {
//					double vmin = (minMax.min - ref.min) / (ref.max - ref.min) - (ref.max - ref.min) * 0.0005;
//					double vmax = (minMax.max - ref.min) / (ref.max - ref.min) + (ref.max - ref.min) * 0.0005;
//					
//	
//					score.min += vmin;				
//					score.max += vmax;
//				}
				System.out.print("");
			}
			System.out.print("");
		}
		System.out.print("");
	}


	private CalcResult calcMinMax(SpecDef specDef, SpecHolder specHolder, UseCaseDefElement useCaseDefElement) {
		CalcResult minMax = new CalcResult();
		SpecValue specValue = specHolder.getGuarantee();
		if ((specValue == null) || !specValue.getDefined()) {
			specValue = specHolder.getTypical();
		}
		if (!specValue.getDefined()) {
			return minMax;
		}
		new SpecTypeBranch(specDef, specValue) {
			@Override
			protected boolean onVaridation(SpecValue specValue2) {
				return judgeValue(specDef, minMax, specValue2);
			}

			@Override
			protected boolean onChoice(SpecValue specValue2) {
				return new BetterBranch(specDef, specValue2) {
					@Override
					protected boolean onWilder(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					protected boolean onNarrower(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					protected boolean onLower(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					protected boolean onHigher(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}	
				}.branch();
			}

			@Override
			protected boolean onRange(SpecValue specValue2) {
				return new BetterBranch(specDef, specValue2) {
					@Override
					protected boolean onWilder(SpecValue specValue2) {
						double width = specValue2.getY() - specValue2.getX();
						minMax.setValue(width);	
						return false;
					}

					@Override
					protected boolean onNarrower(SpecValue specValue2) {
						double width = specValue2.getY() - specValue2.getX();
						minMax.setValue(-width);
						return false;
					}

					@Override
					protected boolean onLower(SpecValue specValue2) {
						minMax.setValue(-specValue2.getX());
						minMax.setValue(-specValue2.getY());
						return false;
					}

					@Override
					protected boolean onHigher(SpecValue specValue2) {
						minMax.setValue(specValue2.getX());
						minMax.setValue(specValue2.getY());
						return false;
					}	
				}.branch();
			}

			@Override
			protected boolean onNumeric(SpecValue specValue2) {
				return judgeValue(specDef, minMax, specValue2);
			}

			private boolean judgeValue(SpecDef specDef, CalcResult minMax, SpecValue specValue2) {
				return new BetterBranch(specDef, specValue2) {
					@Override
					protected boolean onWilder(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					protected boolean onNarrower(SpecValue specValue2) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					protected boolean onLower(SpecValue specValue2) {
						double value = (specValue2.getX() - useCaseDefElement.getThreshold()) / useCaseDefElement.getThreshold();
						minMax.setValue(value);
						return specValue2.getX() < useCaseDefElement.getThreshold();
					}

					@Override
					protected boolean onHigher(SpecValue specValue2) {
						double value = (specValue2.getX() - useCaseDefElement.getThreshold()) / useCaseDefElement.getThreshold();
						minMax.setValue(value);
						return specValue2.getX() > useCaseDefElement.getThreshold();
					}
				}.branch();
			}

			@Override
			protected boolean onBoolean(SpecValue specValue2) {
				// TODO Auto-generated method stub
				return false;
			}
			
		}.branch();
		return minMax;
	}

	public CalcResult calc(String product) {
		return result.get(product);
	}

}
class CalcResult {
	public double min = Double.POSITIVE_INFINITY;
	public double max = Double.NEGATIVE_INFINITY;
	
	public CalcResult() {}
	
	public CalcResult(double min, double max) {
		this.min = min;
		this.max = max;
	}

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