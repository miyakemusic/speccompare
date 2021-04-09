package positioningmap;

public class MtBoolean implements MtSpecItem {

	private boolean available;

	public MtBoolean(boolean b) {
		this.available = b;
	}

	@Override
	public String value() {
		return String.valueOf(available);
	}
	

}
