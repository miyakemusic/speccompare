package positioningmap;

public class SpecHolder implements Cloneable {

	private SpecValue guarantee = new SpecValue();
	private SpecValue typical = new SpecValue();
	
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
	public SpecHolder clone() {
		try {
			SpecHolder ret = (SpecHolder)super.clone();
			ret.guarantee = this.guarantee.clone();
			ret.typical = this.typical.clone();
			return ret;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void clearValue() {
		this.guarantee.clear();
		this.typical.clear();
	}
}
