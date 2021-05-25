package positioningmap;

public class SpecHolderElement implements Cloneable {
	private SpecValue guarantee = new SpecValue();
	private SpecValue typical = new SpecValue();
	
	public SpecHolderElement(SpecValue guarantee2, SpecValue typical2) {
		this.guarantee = guarantee2;
		this.typical = typical2;
	}
	
	public SpecHolderElement() {
		// TODO Auto-generated constructor stub
	}
	
	public void guarantee(SpecValue v) {
		this.guarantee = v;
	}

	public void typical(SpecValue v) {
		this.typical = v;
	}

	public SpecValue getGuarantee() {
		return this.guarantee;
	}
	
	public SpecValue getTypical() {
		return this.typical;
	}
	

	@Override
	public SpecHolderElement clone() {
		try {
			SpecHolderElement ret = (SpecHolderElement)super.clone();
			ret.guarantee = this.guarantee.clone();
			ret.typical = this.typical.clone();
			return ret;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void clearValue() {
		this.guarantee.clear();
		this.typical.clear();
	}
}
