package positioningmap;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import positioningmap.Main.Better;
import positioningmap.Main.InstrumentType;
import positioningmap.Main.SpecTypeEnum;

public class SpecDef {

	private SpecTypeEnum specType;
	private String unit;
	private Better better = Better.None;
	private List<String> choices = new ArrayList<>();
	private String id;
	private String parentId = "";
	
	@JsonIgnore
	private SpecDefInterface specDefInterface;
	
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
//		if (this.specType.compareTo(SpecTypeEnum.InstrumentType) == 0) {
//			this.choices.clear();
//			for (int i = 0; i < InstrumentType.values().length; i++) {
//				this.choices.add(InstrumentType.values()[i].name());
//			}
//			this.setName(SpecTypeEnum.InstrumentType.name());
//		}
	}
	
	@JsonIgnore
	public String getCategory() {
		return specDefInterface.category(this);
	}
	
	@JsonIgnore
	public void setCategory(String category) {
		this.specDefInterface.category(this, category);
	}
	
	
	public void setSpecDefInterface(SpecDefInterface specDefInterface) {
		this.specDefInterface = specDefInterface;
	}
	
	@JsonIgnore
	public String getName() {
		return specDefInterface.name(this);
	}
	
	@JsonIgnore
	public void setName(String name) {
		this.specDefInterface.name(this, name);
	}
	public void moveUp() {
		specDefInterface.moveUp(this);
	}
	public void moveDown() {
		specDefInterface.moveDown(this);
	}

}