package positioningmap;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import positioningmap.Main.Better;
import positioningmap.Main.SpecTypeEnum;

public class SpecDef {

	private SpecTypeEnum specType;
	private String unit;
	private Better better;
	private List<String> choices = new ArrayList<>();
	private String id;
	
	@JsonIgnore
	private SpecDefInterface specInterface;
	
	public SpecDef() {}
	public SpecDef(SpecTypeEnum specType, String unit, Better better) {
		this.specType = specType;
		this.unit = unit;
		this.better = better;
		this.id = String.valueOf(this.hashCode());
	}

	public SpecDef choice(String choice) {
		this.choices.add(choice);
		return this;
	}

	public String id() {
		return id;
	}

	public String unit() {
		return unit;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Better getBetter() {
		return better;
	}
	public void setBetter(Better better) {
		this.better = better;
	}
	public List<String> getChoices() {
		return choices;
	}
	public void setChoices(List<String> choices) {
		this.choices = choices;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public SpecTypeEnum getSpecType() {
		return specType;
	}
	public String category() {
		return specInterface.category(this);
	}
	public void setSpecInterface(SpecDefInterface specDefInterface) {
		this.specInterface = specDefInterface;
	}
	public String name() {
		return specInterface.name(this);
	}

}