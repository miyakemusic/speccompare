package positioningmap;

import positioningmap.Main.InstrumentType;

public class TextGenerator {
	private String createTextReturnValue = "";
	public String generate(SpecDef spec, SpecHolder specHolder) {
		createTextReturnValue = "";
		specHolder.getSpecs().forEach((condition, element) -> {
			if (!condition.equals(SpecHolder.DEFAULT)) {
				createTextReturnValue += ",[" + condition + "]";
			}
			generate_one(spec, element);
		});
		
		return this.createTextReturnValue;
	}
	public String generate_one(SpecDef spec, SpecHolderElement element) {
		
		new SpecTypeBranch(spec, element) {
			@Override
			protected boolean onTwoDimensional(SpecValue guarantee, SpecValue typical, SpecValue specValue2) {
				if (guarantee != null && guarantee.getDefined()) {
					createTextReturnValue += guarantee.getX() + " x " + guarantee.getY();
				}
				return false;
			}

			@Override
			protected boolean onVaridation(SpecValue guarantee, SpecValue typical, SpecValue specValue2) {
				if (guarantee != null && guarantee.getDefined()) {
					createTextReturnValue += "}" + guarantee.getX() + spec.getUnit();
				}
				if (typical != null && typical.getDefined()) {
					if (!createTextReturnValue.isEmpty()) {
						createTextReturnValue += "/";
					}
					createTextReturnValue += "}" + typical.getX() + "(Typ.)";
				}
				return false;
			}

			@Override
			protected boolean onChoice(SpecValue guarantee, SpecValue typical, SpecValue specValue2) {
				if (guarantee != null && guarantee.getDefined()) {
					createTextReturnValue += guarantee.getString();
				}
				return false;
			}

			@Override
			protected boolean onRange(SpecValue guarantee, SpecValue typical, SpecValue specValue2) {
				if (guarantee != null && guarantee.getDefined()) {
					createTextReturnValue += guarantee.getX() + " to " + guarantee.getY() + spec.getUnit();
				}
				if (typical != null && typical.getDefined()) {
					if (!createTextReturnValue.isEmpty()) {
						createTextReturnValue += "/";
					}
					createTextReturnValue += typical.getX() + " to " + typical.getY() + "(Typ.)";
				}
				return false;
			}

			@Override
			protected boolean onNumeric(SpecValue guarantee, SpecValue typical, SpecValue specValue2) {
				if (guarantee != null && guarantee.getDefined()) {
					createTextReturnValue += guarantee.getX().toString();
				}
				if (typical != null && typical.getDefined()) {
					if (!createTextReturnValue.isEmpty()) {
						createTextReturnValue += "/";
					}
					createTextReturnValue += typical.getX().toString() + "(Typ.)";
				}
				return false;
			}

			@Override
			protected boolean onBoolean(SpecValue guarantee, SpecValue typical, SpecValue specValue2) {
				if (guarantee != null && guarantee.getDefined()) {
					if (guarantee.getAvailable()) {
						createTextReturnValue += "Yes";
					}
					else {
						createTextReturnValue += "No";
					}
				}
				else {
					createTextReturnValue += "No";
				}
				return false;
			}

			@Override
			protected boolean onText(SpecValue guarantee, SpecValue typical, SpecValue specValue2) {
				if (guarantee != null && guarantee.getDefined()) {
					createTextReturnValue += guarantee.text();
				}
				return false;
			}

			@Override
			protected boolean onMultiple(SpecValue guarantee, SpecValue typical, SpecValue specValue2) {
				if (guarantee != null && guarantee.getDefined()) {
					 guarantee.getMultiple().forEach(v -> {
						 createTextReturnValue += v + ","; 
					});
					 if (!createTextReturnValue.isEmpty()) {
						 createTextReturnValue = createTextReturnValue.substring(0, createTextReturnValue.length()-1);
					 }
				}
				return false;
			}

			@Override
			protected boolean onThreeDimensional(SpecValue guarantee, SpecValue typical, SpecValue specValue2) {
				if (guarantee != null && guarantee.getDefined()) {
					createTextReturnValue += guarantee.getX() + " x " + guarantee.getY() +  " x " + guarantee.getZ();
				}
				return false;
			}

			@Override
			protected boolean onInstrumentType(SpecValue guarantee, SpecValue typical, SpecValue specValue2) {
				if (guarantee != null && guarantee.getDefined()) {
					createTextReturnValue += guarantee.getString();
					
//					if (guarantee.getString().equals(InstrumentType.UseExternal.name())) {
						createTextReturnValue += "(";
						guarantee.getMultiple().forEach(v -> {
							createTextReturnValue +=  v + ", ";
						});
						createTextReturnValue += ")";
//					}
				}
				return false;
			}
			
		}.branch();
		return createTextReturnValue;
	}

}
