package positioningmap;

public class MtInterface  implements MtSpecItem {

	private String version;
	private int ports;

	public MtInterface(String version, int ports) {
		this.version = version;
		this.ports = ports;
	}

	@Override
	public String value() {
		return version + " / " + ports + " " + "ports";
	}

}
