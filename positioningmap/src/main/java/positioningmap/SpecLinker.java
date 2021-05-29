package positioningmap;

import java.util.Map;

import positioningmap.Main.InstrumentType;
import positioningmap.Main.SpecTypeEnum;

public class SpecLinker {

	private String instrumentTypeId;

	public SpecLinker(SpecSheet specSheet, String productName) {
		specSheet.getCategories().forEach((category, specCategory) -> {
			specCategory.getSpecs().forEach((name, specDef) ->{
				if (specDef.getSpecType().compareTo(SpecTypeEnum.InstrumentType) == 0) {
					if (specDef.getCategory().equals("Common")) {
						instrumentTypeId = specDef.getId();
						return;
					}
				}
			});
		});
		ProductSpec productSpecs = specSheet.getProductSpecs().get(productName);
		if (productSpecs == null) {
			return;
		}
		SpecHolder specHolder = productSpecs.getValues().get(this.instrumentTypeId);
		
		SpecValue specValue = specHolder.defaultSpec().getGuarantee();
		String instrumentType = specValue.getString();
		
		if (instrumentType.equals(Main.InstrumentType.MainFrame.name())) {
			specValue.getMultiple().forEach(product -> {
				ProductSpec ps = specSheet.getProductSpecs().get(product);
				ps.getValues().get(instrumentTypeId).defaultSpec().getGuarantee().getMultiple().add(product);
			});
		}
		else if (instrumentType.equals(Main.InstrumentType.Module.name())) {
			specValue.getMultiple().forEach(product -> {
				ProductSpec ps = specSheet.getProductSpecs().get(product);
				ps.getValues().get(instrumentTypeId).defaultSpec().getGuarantee().getMultiple().add(productName);
				//System.out.println(ps);
			});		
		}
	}

}
