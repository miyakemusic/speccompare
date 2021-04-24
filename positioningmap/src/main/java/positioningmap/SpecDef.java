package positioningmap;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import positioningmap.Main.Better;
import positioningmap.Main.SpecTypeEnum;

public class SpecDef {

	private SpecTypeEnum specType;
	private String unit;
	private Better better = Better.None;
	private List<String> choices = new ArrayList<>();
	private String id;
	private String parentId = "";
	
	@JsonIgnore
	private SpecDefInterface specInterface;
	
	public SpecDef() {}
	public SpecDef(SpecTypeEnum specType, String unit, Better better) {
		this.specType = specType;
		this.unit = unit;
		this.better = better;
		this.id = String.valueOf(this.hashCode());
	}

	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public SpecDef choice(String choice) {
		this.choices.add(choice);
		return this;
	}

	public String id() {
		return id;
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
	public void setBetter(String better) {
		if (better == null) {
			this.better = Better.None;
		}
		else {
			this.better = Better.valueOf(better);
		}
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
	public void setSpecType(String specType) {
		this.specType = SpecTypeEnum.valueOf(specType);
	}
	
	@JsonIgnore
	public String getCategory() {
		return specInterface.category(this);
	}
	
	@JsonIgnore
	public void setCategory(String category) {
		this.specInterface.category(this, category);
	}
	
	
	public void setSpecInterface(SpecDefInterface specDefInterface) {
		this.specInterface = specDefInterface;
	}
	
	@JsonIgnore
	public String getName() {
		return specInterface.name(this);
	}
	
	@JsonIgnore
	public void setName(String name) {
		this.specInterface.name(this, name);
	}
	public void moveUp() {
		specInterface.moveUp(this);
	}
	public void moveDown() {
		specInterface.moveDown(this);
	}

}