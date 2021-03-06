package positioningmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.fasterxml.jackson.databind.ObjectMapper;

import positioningmap.UseCaseDefElement.Level;

public class Main {

	private static final String FILTER_JSON = "Filter.json";
	protected static final int SPEC_COL = 1;
	protected static final int CATEGORY_COL = 0;
	
	public static void main(String[] args) {
		new Main();
	}

	public enum InterfaceType {
		E100Mbps,
		E1Gbps,
		E10Gbps,
		USB1_1,
		USB2_0,
		USB3_0,
	}

	public enum InstrumentType {
		NoSupport,
		Standalone,
		MainFrame,
		Module,
		Probe,
		UseExternal
	}
	public enum Better {
		Higher,
		Lower, Closer,Wider, Narrower, None
	}
	
	public enum SpecTypeEnum {
		InstrumentType, TwoDmensionalSize, Numeric, Choice, Boolean, Variation, Range, Text, MultipleChoice, ThreemensionalSize
		
	}
	public enum Unit {
		Pixies,
		Inch,
		Mbps,
		GB,
		Hours,
		dB, Version, km, None
	}
	
	private List<List<String>> list = new ArrayList<>();
	private List<String> title = new ArrayList<>();
	private SpecSheet specSheet = new SpecSheet("OTDR");
	private UseCaseContainer pmdefs = new UseCaseContainer();
	protected FilterContainer filterContainer = new FilterContainer();
	protected PositioningMapUi positioningMapUi;
	private PositioningMapModel positioningMapModel;
	private EnableChecker enableChecker;
	private SatisfactionChecker satisfactionChecker;
	public Main() {
		enableChecker = new EnableChecker() {
			@Override
			protected SpecSheet getSpecSheet() {
				return specSheet;
			}
		};
		satisfactionChecker = new SatisfactionChecker() {
			@Override
			protected UseCaseContainer useCaseContainer() {
				return pmdefs;
			}

			@Override
			protected SpecSheet specSheet() {
				return specSheet;
			}
		};
		
//		createDemo();
		loadFile();
		
		AbstractTableModel model = new AbstractTableModel() {
			@Override
			public String getColumnName(int column) {
				return title.get(column);
			}

			@Override
			public int getRowCount() {
				return list.size();
			}

			@Override
			public int getColumnCount() {
				return list.get(0).size() - 1;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return list.get(rowIndex).get(columnIndex + 1);
			}
		};
		
		updateModel(specSheet, model, false);
		
		TableFrameInterface tableFrameInterface = new TableFrameInterface() {
			@Override
			public List<String> categories() {
				return new ArrayList<String>(specSheet.categories());
			}

			@Override
			public List<String> units() {
				return specSheet.units();
			}

			@Override
			public SpecDef createSpecDef() {
				return specSheet.newSpec();
			}

			@Override
			public Map<String, String> parents() {
				return specSheet.booleanIds();
			}

			@Override
			public boolean isEnabled(int row, String product) {
				String id = list.get(row).get(0);
				//return specSheet.getValue(id, product).isEnabled();
				//return new EnableChecker(specSheet).check(id, product);
				return enableChecker.check(id, product);
			}


			@Override
			public ResultLevelEnum qualified(int row, String product) {
				String id = list.get(row).get(0);
//				SpecHolder specHolder = specSheet.getValue(id, product);//.getProductSpecs().get(product).getValues().get(id);
//				UseCaseDef useCaseDef = pmdefs.get(filterContainer.getUseCaseName());
				
				return satisfactionChecker.check(filterContainer.getUseCaseName(), product, id);
//				return checkQualify(useCaseDef, specHolder, id);
			}
			@Override
			public Collection<String> vendors() {
				return specSheet.vendors();
			}

			@Override
			public Boolean filter(String type, String string) {
				return filterContainer .get(type, string);
			}

			@Override
			public void setFilter(String type, String string, Boolean v) {
				filterContainer.set(type, string, v);
			}

			@Override
			public void requestUpdate() {
				updateModel(specSheet, model, true);
			}

			@Override
			public Collection<String> useCases() {
				return pmdefs.defs();
			}

			@Override
			public void filterUsecase(boolean b, String useCaseName) {
				filterContainer.setUseCase(useCaseName);
				filterContainer.setUseCaseFilter(b);
				
				updateModel(specSheet, model, true);
			}

			@Override
			public String selectedUseCase() {
				return filterContainer.getUseCaseName();
			}

			@Override
			public boolean useCaseFilterEnabeld() {
				return filterContainer.isUseCaseFilter();
			}

			@Override
			public Collection<String> productList() {
				return specSheet.products().keySet();
			}

//			@Override
//			public Collection<String> conditionList(String productName) {
//				return specSheet.conditionList(productName);
//			}

		};
		
		new TableFrame(model, tableFrameInterface) {
			@Override
			void save() {
				saveToFile(specSheet);
			}

			@Override
			void newProduct(String value) {
				specSheet.addProduct("EXFO", value);
				updateModel(specSheet, model, true);
			}

			@Override
			void newItem() {
				// TODO Auto-generated method stub
				
			}

			@Override
			SpecDef getSpecDef(int row) {
				SpecDef specDef = specDefByRow(row);
				return specDef;
			}

			private SpecDef specDefByRow(int row) {
				String id = list.get(row).get(0).toString();
				SpecDef specDef = specSheet.specDef(id);
				return specDef;
			}
			
			@Override
			SpecHolder getSpecValue(int row, int col) {
				String id = list.get(row).get(0).toString();
				String model = title.get(col).toString();
				SpecHolder spec = specSheet.getValue(id, model);
				return spec;
			}

			@Override
			String getModel(int col) {
				String model = title.get(col).toString();
				return model;
			}

			@Override
			void onUpdate(String productName) {
				new SpecLinker(specSheet, productName);
				updateModel(specSheet, model, true);
			}

			@Override
			void moveUp(int row, int col) {
				if (col == SPEC_COL) {
					SpecDef specDef = specDefByRow(row);
					specDef.moveUp();
				}
				else if (col == CATEGORY_COL) {
					String category = list.get(row).get(1);
					specSheet.moveUp(category);
				}
				updateModel(specSheet, model, false);
			}

			@Override
			void moveDown(int row, int col) {
				if (col == SPEC_COL) {
					SpecDef specDef = specDefByRow(row);
					specDef.moveDown();
				}
				else if (col == CATEGORY_COL) {
					String category = list.get(row).get(1);
					specSheet.moveDown(category);	
				}
				updateModel(specSheet, model, false);
			}

			@Override
			void rename(String oldName, String newName) {
				specSheet.changeProductName(oldName, newName);
				updateModel(specSheet, model, true);
			}

			@Override
			void copyProduct(String name) {
				specSheet.copyProduct(name);
				updateModel(specSheet, model, true);
			}

			@Override
			void moveLeft(String name) {
				specSheet.moveLeft(name);
				updateModel(specSheet, model, true);
			}

			@Override
			void moveRight(String name) {
				specSheet.moveRight(name);
				updateModel(specSheet, model, true);
			}

			@Override
			void copyCells(int[] fromRows, String fromColumn, String toColumn) {
				List<String> ids = new ArrayList<>();
				for (int row : fromRows) {
					ids.add(list.get(row).get(0));
				}
				specSheet.copyCells(ids, fromColumn, toColumn);
				updateModel(specSheet, model, false);
			}

			@Override
			void delete(int row) {
				specSheet.delete(list.get(row).get(1), list.get(row).get(0));
				updateModel(specSheet, model, true);
			}

			@Override
			void onPositioningMap() {
				if (positioningMapUi == null) {
					positioningMapUi = new PositioningMapUi(positioningMapModel = new PositioningMapModel(specSheet, pmdefs));
				}
				positioningMapUi.setVisible(true);
			}

			@Override
			void copySpec(int row) {
				String id = list.get(row).get(0).toString();
				specSheet.copySpec(list.get(row).get(1), list.get(row).get(0));
				updateModel(specSheet, model, true);
			}

			@Override
			void onConifgPositioningMap() {
				UseCaseConfigUi ui = new UseCaseConfigUi(specSheet, pmdefs) {
					@Override
					protected void save() {
						saveUseSaveConfig();
						positioningMapModel.update();
					}
				};
				ui.setVisible(true);
			}

			@Override
			void clearValue(int[] rows, int[] columns) {
				for (int row : rows) {
					for (int col : columns) {
						String id = list.get(row).get(0).toString();
						String model = title.get(col).toString();
						specSheet.clearValue(id, model);
						
					}
				}
				updateModel(specSheet, model, false);
			}

//			@Override
//			Collection<String> productCondition(String name) {
//				return specSheet.conditionList(name);
//			}

			@Override
			ConditionContainer replaceConditionName(String productName, String prevString, String newString) {
				ConditionContainer ret = specSheet.replaceConditionName(productName, prevString, newString);
				updateModel(specSheet, model, true);
				return ret;
			}

			@Override
			ConditionContainer productConditionConfig(String productName) {
				return specSheet.productConditionConfig(productName);
			}

		}.setVisible(true);
	}

	private void loadFile() {
		try {
			this.specSheet = new ObjectMapper().readValue(new File("otdr.spec"), SpecSheet.class);
			this.specSheet.init();
//			this.enableChecker.clear();
			
			this.pmdefs = new ObjectMapper().readValue(new File("pmdef.json"), UseCaseContainer.class);
			this.pmdefs.init(new UseCaseDefInterface() {
				@Override
				public String getParentId(String id) {
					return specSheet.find(id).getParentId();
				}
			});
			if (new File(FILTER_JSON).exists()) {
				this.filterContainer = new ObjectMapper().readValue(new File(FILTER_JSON), FilterContainer.class);
			}
			this.specSheet.setFilter(new FilterInterface() {
				@Override
				public boolean categoryEnabled(String cat) {
					return filterContainer.get(FilterContainer.Categories, cat);
				}

				@Override
				public boolean productEnabled(String product) {
					String vendor = product.split("\n")[0];
					return filterContainer.get(FilterContainer.Vendors, vendor);
				}

				@Override
				public boolean qualified(String productName, ProductSpec value) {
					if (!filterContainer.isUseCaseFilter()) {
						return true;
					}
					if ( filterContainer.getUseCaseName().isEmpty() || filterContainer.getUseCaseName().equals("-")) {
						return true;
					}
					
					UseCaseDef useCaseDef = pmdefs.get(filterContainer.getUseCaseName());
					if (useCaseDef == null) {
						return true;
					}
//					System.out.println(productName);
					for (String id : specSheet.allIds()) {
//						SpecHolder specHolder = value.getValues().get(id);
						
						ResultLevelEnum ret = satisfactionChecker.check(filterContainer.getUseCaseName(), productName, id);
						
//						if ((checkQualify(useCaseDef, specHolder, id) == ResultLevelEnum.Critical) || 
//								(checkQualify(useCaseDef, specHolder, id) == ResultLevelEnum.Warning)) {
//							return false;
//						}
						if ((ret == ResultLevelEnum.Critical) || (ret == ResultLevelEnum.Warning)) {
							return false;
						}
					}

					return true;
				}
			});
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void saveUseSaveConfig() {
		try {
			pmdefs.clean();			
			new ObjectMapper().writeValue(new File("pmdef.json"), pmdefs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void saveToFile(SpecSheet specOtdr) {
		try {
			specOtdr.clean();
			
			if (!Files.exists(Paths.get("backup"))) {
				Files.createDirectories(Paths.get("backup"));
			}
			Files.copy(Paths.get("otdr.spec"), Paths.get("backup/otdr.spec." + System.currentTimeMillis()));
  			new ObjectMapper().writeValue(new File("otdr.spec"), specOtdr);
			new ObjectMapper().writeValue(new File(FILTER_JSON), this.filterContainer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateModel(SpecSheet specOtdr, AbstractTableModel model, boolean structureChanged) {
		this.list.clear();
		this.title.clear();
		this.enableChecker.clear();
		this.satisfactionChecker.clear();
		
		Map<String, ProductSpec> ps = specOtdr.filteredProducts();
		for (String category : specOtdr.filteredCategories()) {
			Map<String, SpecDef> specs = specOtdr.getSpecs(category);
			for (String specName : specs.keySet()) {
				SpecDef spec = specs.get(specName);
				List<String> line = new ArrayList<>();
				line.add(spec.id());
				line.add(category);
				String specWithUnit = specName;
				if (!spec.getUnit().isEmpty()) {
					specWithUnit = specName + " (" + spec.getUnit() + ")";
				}
				line.add(specWithUnit);

				for (Map.Entry<String, ProductSpec> entry : ps.entrySet()) {
					ProductSpec pn = entry.getValue();
					
					//line.add(pn.value(spec.id()) /*+ " " + spec.unit()*/);
					line.add(createText(spec, pn.getValues().get(spec.getId())));
				}
				list.add(line);
			}
		}
		
		title.add("Category");
		title.add("Spec");
		for (String product : ps.keySet()) {
			title.add(product);
		}
		if (structureChanged) {
			model.fireTableStructureChanged();
		}
		else {
			model.fireTableDataChanged();
		}
		if (positioningMapModel != null) {
			positioningMapModel.update();
		}
	}

	private String createText(SpecDef spec, SpecHolder specHolder) {
		if (specHolder == null) {
			return ""
					;
		}
		return new TextGenerator().generate(spec, specHolder);
	}

//	private ResultLevelEnum checkQualify(UseCaseDef useCaseDef, SpecHolder specHolder, String id) {
//		UseCaseDefElement useCaseDefE = useCaseDef.value(id);
//		ResultLevelEnum failResult = null;
//		if ((useCaseDefE.getLevel().compareTo(Level.Mandatory) == 0) || (specHolder == null)) {
//			failResult = ResultLevelEnum.Critical;
//		}
//		else {
//			failResult = ResultLevelEnum.Warning;
//		}
//		
//		if (useCaseDefE.getDefined()) {
//			if (specHolder == null) {
//				return failResult;
//			}
//			SpecValue specValue = specHolder.getGuarantee();
//			if (specValue == null) {
//				specValue = specHolder.getTypical();
//			}
//			SpecDef specDef = specSheet.find(id);
//			if (specDef == null) {
////				System.out.println();
//			}
//
//			DoubleWrapper ret = new BasicScoreCalculator().calc(specDef, specHolder, useCaseDefE);
//			if (ret.value >= 0.0) {
//				return ResultLevelEnum.Qualify;
//			}
//			else {
//				return failResult;
//			}
//		}
//		return failResult.NotJudged;
//	}
	
	private void createDemo() {
		String displayDevice = specSheet.addSpec("Display", "Device", SpecTypeEnum.Choice).choice("LCD").choice("OLED").id();
		String resolutionId = specSheet.addSpec("Display", "Resolution", SpecTypeEnum.TwoDmensionalSize, "pixels", Better.Higher).id();
		String lcdSize = specSheet.addSpec("Display", "Size", SpecTypeEnum.Numeric, "inch", Better.Higher).id();
	
		String usb = specSheet.addSpec("Interface", "USB", SpecTypeEnum.Numeric, "", Better.Higher).id();
		String eth = specSheet.addSpec("Interface", "Ethernet", SpecTypeEnum.Numeric, "Mbps", Better.Higher).id();
		String storageSize = specSheet.addSpec("Storage", "Size", SpecTypeEnum.Numeric, "GB", Better.Higher).id();
	
		String batteryType = specSheet.addSpec("Battery", "type", SpecTypeEnum.Choice).choice("lithium-polymer").choice("lithium-ion").id();
		String runtime = specSheet.addSpec("Battery", "Runtime", SpecTypeEnum.Numeric, "Hours", Better.Higher).id();
	
		String powerSupply = specSheet.addSpec("Power Supply", "Input", SpecTypeEnum.Range, "VAC", Better.Wider).id();
		String powerConsumption = specSheet.addSpec("Power Supply", "Consumption", SpecTypeEnum.Numeric, "Watts", Better.Lower).id();

		String w850 = specSheet.addSpec("Wavelength", "850nm", SpecTypeEnum.Boolean).id();
		String w850_accuracy = specSheet.addSpec("Wavelength", "850nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();
		
		String w1300 = specSheet.addSpec("Wavelength", "1300nm", SpecTypeEnum.Boolean).id();
		String w1300_accuracy = specSheet.addSpec("Wavelength", "1300nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();
		
		String w1490 = specSheet.addSpec("Wavelength", "1490nm", SpecTypeEnum.Boolean).id();
		String w1490_accuracy = specSheet.addSpec("Wavelength", "1490nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();
		
		String w1310 = specSheet.addSpec("Wavelength", "1310nm", SpecTypeEnum.Boolean).id();
		String w1310_accuracy = specSheet.addSpec("Wavelength", "1310nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();

		
		String w1550 = specSheet.addSpec("Wavelength", "1550nm", SpecTypeEnum.Boolean).id();
		String w1550_accuracy = specSheet.addSpec("Wavelength", "1550nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();

		String w1625 = specSheet.addSpec("Wavelength", "1625nm", SpecTypeEnum.Boolean).id();
		String w1625f = specSheet.addSpec("Wavelength", "1625nm (Filter)", SpecTypeEnum.Boolean).id();
		String w1625_accuracy = specSheet.addSpec("Wavelength", "1625nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();

		
		String w1650f = specSheet.addSpec("Wavelength", "1650nm (filtered)", SpecTypeEnum.Boolean).id();
		String w1650_accuracy = specSheet.addSpec("Wavelength", "1650nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();

		String dr850 = specSheet.addSpec("Dynamic Range", "850nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		String dr1300 = specSheet.addSpec("Dynamic Range", "1300nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();		
		String dr1310 = specSheet.addSpec("Dynamic Range", "1310nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		String dr1490 = specSheet.addSpec("Dynamic Range", "1490nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		String dr1550 = specSheet.addSpec("Dynamic Range", "1550nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		String dr1625 = specSheet.addSpec("Dynamic Range", "1625nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		String dr1650 = specSheet.addSpec("Dynamic Range", "1650nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		
		String filterSpecHighpass = specSheet.addSpec("Built-in Filter (1625nm)", "Highpass", SpecTypeEnum.Numeric, "nm", Better.Higher).id();
		String filterSpecIsolation = specSheet.addSpec("Built-in Filter (1625nm)", "Isolation", SpecTypeEnum.Numeric, "dB", Better.Higher).id();

		String eventDeadZone = specSheet.addSpec("Event dead zone", "", SpecTypeEnum.Numeric, "m", Better.Higher).id();
		String attenuationDeadZone = specSheet.addSpec("Attenuation dead zone", "", SpecTypeEnum.Numeric, "m", Better.Higher).id();

		
		ProductSpec maxTester715B = specSheet.addProduct("EXFO", "MaxTester 715B");
		maxTester715B.guarantee(displayDevice, "LCD");
		maxTester715B.createGuarantee(resolutionId, 800, 480);
		maxTester715B.guarantee(lcdSize, 7);
		
		maxTester715B.guarantee(usb,  2.0);
		maxTester715B.guarantee(eth, 100);
		maxTester715B.guarantee(storageSize, 20);
		
		maxTester715B.guarantee(batteryType, "lithium-polymer");
		maxTester715B.guarantee(runtime, 12);
		
		maxTester715B.createGuarantee(powerSupply, 100, 240);
		maxTester715B.guarantee(powerConsumption, 15);
		
		maxTester715B.guarantee(w850, false);
				
		maxTester715B.guarantee(w1300, false);
		
		maxTester715B.guarantee(w1310, true);
		maxTester715B.createTypical(w1310_accuracy, -30, 30);
		
		maxTester715B.guarantee(w1550, true);
		maxTester715B.createTypical(w1550_accuracy, -30, 30);
		
		maxTester715B.guarantee(w1490, false);
		
		maxTester715B.guarantee(w1625, false);
		maxTester715B.guarantee(w1625f, true);
		maxTester715B.createTypical(w1625_accuracy, -10, 10);
		
		maxTester715B.guarantee(w1650f, false);
		
		maxTester715B.typical(dr1310, 30);
		maxTester715B.typical(dr1550, 28);
		maxTester715B.typical(dr1625, 28);
		
		maxTester715B.guarantee(filterSpecHighpass, 1595);
		maxTester715B.guarantee(filterSpecIsolation, 50);
		
		maxTester715B.typical(eventDeadZone, 1);
		maxTester715B.typical(attenuationDeadZone, 4);
		
		this.specSheet.init();
	}

}
