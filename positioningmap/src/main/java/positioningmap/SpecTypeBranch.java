package positioningmap;

import positioningmap.Main.Better;
import positioningmap.Main.SpecTypeEnum;

public abstract class SpecTypeBranch {

	private SpecDef specDef;
	private SpecHolderElement specHolder;
	private SpecValue specValue;

	public SpecTypeBranch(SpecDef specDef, SpecHolderElement element) {
		this.specDef = specDef;
		if (element == null) {
			return;
		}
		this.specHolder = element;
		this.specValue = element.getGuarantee();
		if (this.specValue == null) {
			this.specValue = element.getTypical();
		}
	}

	public boolean branch() {
		if (this.specHolder == null) {
			return false;
		}
		if (specDef.getSpecType().compareTo(SpecTypeEnum.Boolean) == 0) {
			return onBoolean(specHolder.getGuarantee(), specHolder.getTypical(), this.specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Numeric) == 0) {
			return onNumeric(specHolder.getGuarantee(), specHolder.getTypical(), this.specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Range) == 0) {
			return onRange(specHolder.getGuarantee(), specHolder.getTypical(), this.specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Choice) == 0) {
			return onChoice(specHolder.getGuarantee(), specHolder.getTypical(), this.specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Variation) == 0) {
			return onVaridation(specHolder.getGuarantee(), specHolder.getTypical(), this.specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.TwoDmensionalSize) == 0) {
			return onTwoDimensional(specHolder.getGuarantee(), specHolder.getTypical(), this.specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.ThreemensionalSize) == 0) {
			return onThreeDimensional(specHolder.getGuarantee(), specHolder.getTypical(), this.specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.Text) == 0) {
			return onText(specHolder.getGuarantee(), specHolder.getTypical(), this.specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.MultipleChoice) == 0) {
			return onMultiple(specHolder.getGuarantee(), specHolder.getTypical(), this.specValue);
		}
		else if (specDef.getSpecType().compareTo(SpecTypeEnum.InstrumentType) == 0) {
			return onInstrumentType(specHolder.getGuarantee(), specHolder.getTypical(), this.specValue);
		}
		return false;
	}

	protected abstract boolean onInstrumentType(SpecValue guarantee, SpecValue typical, SpecValue specValue2);

	protected abstract boolean onThreeDimensional(SpecValue guarantee, SpecValue typical, SpecValue specValue2);

	protected abstract boolean onMultiple(SpecValue guarantee, SpecValue typical, SpecValue specValue2);

	protected abstract boolean onText(SpecValue guarantee, SpecValue typical, SpecValue specValue2);

	protected abstract boolean onTwoDimensional(SpecValue guarantee, SpecValue typical, SpecValue specValue);

	protected abstract boolean onVaridation(SpecValue guarantee, SpecValue typical, SpecValue specValue);

	protected abstract boolean onChoice(SpecValue guarantee, SpecValue typical, SpecValue specValue);

	protected abstract boolean onRange(SpecValue guarantee, SpecValue typical, SpecValue specValue);

	protected abstract boolean onNumeric(SpecValue guarantee, SpecValue typical, SpecValue specValue);

	abstract protected boolean onBoolean(SpecValue guarantee, SpecValue typical, SpecValue specValue);

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
