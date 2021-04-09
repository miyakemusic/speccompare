package positioningmap;

public class SpecHolder {

//	public enum SpecType {
//		Guarantee,
//		Typical
//	}

//	private Map<SpecType, SpecValue> values = new HashMap<>();
	
	private SpecValue guarantee = new SpecValue();
	private SpecValue typical = new SpecValue();
	
	public void guarantee(SpecValue v) {
		this.guarantee = v;
	}

	public void typical(SpecValue v) {
		this.typical = v;
	}

	public SpecValue getGuarantee() {
//		if (!this.values.containsKey(SpecType.Guarantee)) {
//			this.values.put(SpecType.Guarantee, new SpecValue());
//		}
		return this.guarantee;
	}
	
	public SpecValue getTypical() {
//		if (!this.values.containsKey(SpecType.Typical)) {
//			this.values.put(SpecType.Typical, new SpecValue());
//		}
		return this.typical;
	}
	
}
