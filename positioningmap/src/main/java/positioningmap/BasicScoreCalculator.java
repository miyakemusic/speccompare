package positioningmap;

import positioningmap.UseCaseDefElement.Level;

public class BasicScoreCalculator {

	public DoubleWrapper calc(SpecDef specDef, SpecValue specValue, UseCaseDefElement useCaseDefElement) {
		DoubleWrapper ret = new DoubleWrapper();

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
						double center = (useCaseDefElement.getThreshold() + useCaseDefElement.getThresholdY()) / 2.0;
						double halfSpan = (useCaseDefElement.getThresholdY() - useCaseDefElement.getThreshold()) / 2.0;
						
						double denominator = center;
						if (denominator == 0.0) {
							denominator = Math.abs(useCaseDefElement.getThreshold());
						}
						double value = (halfSpan - Math.abs(specValue2.getX() - center)) / denominator;
						double valueY = (halfSpan - Math.abs(specValue2.getY() - center)) / denominator;
						
						ret.value = Math.min(value, valueY);
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

			@Override
			protected boolean onBoolean(SpecValue specValue2) {
				if (!specValue2.getAvailable()) {
					ret.value = -0.2;
				}
				return false;
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
						result.value = -value;
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
		}.branch();
		
		double mag = 0.0;
		if (useCaseDefElement.getLevel().compareTo(Level.Mandatory) == 0) {
			mag = 1.0;
		}
		else if (useCaseDefElement.getLevel().compareTo(Level.High_Priority) == 0) {
			mag = 0.8;
		}
		else if (useCaseDefElement.getLevel().compareTo(Level.Middle_Priority) == 0) {
			mag = 0.4;
		}
		else if (useCaseDefElement.getLevel().compareTo(Level.Optional) == 0) {
			mag = 0.1;
		}
		ret.value = ret.value * mag;
		
		return ret;
	}

}
class DoubleWrapper {
	public double value = 0.0;
}