package positioningmap;

import positioningmap.UseCaseDefElement.Level;

public abstract class SatisfactionChecker {
	private CheckerBase<ResultLevelEnum> mapMap = new CheckerBase<>();
	
	abstract protected UseCaseContainer useCaseContainer();
	abstract protected SpecSheet specSheet();
	
	public ResultLevelEnum check(String useCaseName, String product, String id) {
		try {
			return this.mapMap.get(product, id);
		} catch (Exception e) {
			ResultLevelEnum ret = calc(useCaseName, product, id);
			mapMap.put(product, id, ret);
			return ret;
		}
	}
	
	private ResultLevelEnum calc(String useCaseName, String product, String id) {
		UseCaseDefElement useCaseDefE = useCaseContainer().get(useCaseName).value(id);
		SpecHolder specHolder = specSheet().getValue(id, product);
		
		ResultLevelEnum failResult = null;
		if ((useCaseDefE.getLevel().compareTo(Level.Mandatory) == 0) || (specHolder == null)) {
			failResult = ResultLevelEnum.Critical;
		}
		else {
			failResult = ResultLevelEnum.Warning;
		}
		
		if (useCaseDefE.getDefined()) {
			if (specHolder == null) {
				return failResult;
			}
			SpecValue specValue = specHolder.guarantee();
			if (specValue == null) {
				specValue = specHolder.typical();
			}
			SpecDef specDef = specSheet().find(id);
			if (specDef == null) {
//				System.out.println();
			}

			DoubleWrapper ret = new BasicScoreCalculator().calc(specDef, specHolder.defaultSpec(), useCaseDefE);
			if (ret.value >= 0.0) {
				return ResultLevelEnum.Qualify;
			}
			else {
				return failResult;
			}
		}
		return failResult.NotJudged;
	}
	public void clear() {
		mapMap.clear();
	}
}
