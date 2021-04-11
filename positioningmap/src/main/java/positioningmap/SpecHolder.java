package positioningmap;

public class SpecHolder {

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
	
}
