package positioningmap;

import positioningmap.Main.SpecTypeEnum;

public abstract class SpecTypeBranch {

	private SpecDef specDef;
	private SpecValue specValue;

	public SpecTypeBranch(SpecDef specDef, SpecValue specValue) {
		this.specDef = specDef;
		this.specValue = specValue;
	}

	public boolean branch() {
		if (specDef.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0) {
			return onBoolean(specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Numeric) == 0) {
			return onNumeric(specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Range) == 0) {
			return onRange(specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Choice) == 0) {
			return onChoice(specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Variation) == 0) {
			return onVaridation(specValue);
		}
		return false;
	}

	protected abstract boolean onVaridation(SpecValue specValue2);

	protected abstract boolean onChoice(SpecValue specValue2);

	protected abstract boolean onRange(SpecValue specValue2);

	protected abstract boolean onNumeric(SpecValue specValue2);

	abstract protected boolean onBoolean(SpecValue specValue2);

}
