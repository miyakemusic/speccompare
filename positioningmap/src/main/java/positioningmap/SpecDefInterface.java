package positioningmap;

public interface SpecDefInterface {

	String category(SpecDef specDef);

	String name(SpecDef specDef);

	void category(SpecDef specDef, String category);

	void name(SpecDef specDef, String name);

	void moveUp(SpecDef specDef);

	void moveDown(SpecDef specDef);

}
