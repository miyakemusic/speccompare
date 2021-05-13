package positioningmap;

import positioningmap.Main.Better;
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
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.TwoDmensionalSize) == 0) {
			return onTwoDimensional(specValue);
		}
		return false;
	}

	protected abstract boolean onTwoDimensional(SpecValue specValue2);

	protected abstract boolean onVaridation(SpecValue specValue2);

	protected abstract boolean onChoice(SpecValue specValue2);

	protected abstract boolean onRange(SpecValue specValue2);

	protected abstract boolean onNumeric(SpecValue specValue2);

	abstract protected boolean onBoolean(SpecValue specValue2);

}
abstract class BetterBranch {
	private SpecDef specDef;
	private SpecValue specValue;

	public BetterBranch(SpecDef specDef, SpecValue specValue) {
		this.specDef = specDef;
		this.specValue = specValue;
	}
	
	public boolean branch() {
		if (specDef.getBetter().compareTo(Better.Higher) == 0) {
			return onHigher(specValue);
		}
		else if (specDef.getBetter().compareTo(Better.Lower) == 0) {
			return onLower(specValue);
		}
		else if (specDef.getBetter().compareTo(Better.Narrower) == 0) {
			return onNarrower(specValue);
		}
		else if (specDef.getBetter().compareTo(Better.Wider) == 0) {
			return onWilder(specValue);
		}
		return false;
	}

	protected abstract boolean onWilder(SpecValue specValue2);

	protected abstract boolean onNarrower(SpecValue specValue2);

	protected abstract boolean onLower(SpecValue specValue2);

	protected abstract boolean onHigher(SpecValue specValue2);
}
