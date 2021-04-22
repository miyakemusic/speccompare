package positioningmap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

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

	public enum Better {
		Higher,
		Lower, Wider, Narrower, None
	}
	
	public enum SpecTypeEnum {
		TwoDmensionalSize, Numeric, Choice, Boolean, Range
		
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
	private SpecSheet specOtdr = new SpecSheet("OTDR");
	public Main() {
		
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
		
		updateModel(specOtdr, model, false);
		
		TableFrameInterface tableFrameInterface = new TableFrameInterface() {
			@Override
			public List<String> categories() {
				return new ArrayList<String>(specOtdr.categories());
			}

			@Override
			public List<String> units() {
				return specOtdr.units();
			}

			@Override
			public SpecDef createSpecDef() {
				return specOtdr.newSpec();
			}

			@Override
			public Map<String, String> parents() {
				return specOtdr.allIds();
			}

			@Override
			public boolean isEnabled(int row, String product) {
				String id = list.get(row).get(0);
//				System.out.println(product + ";" + id);
				SpecDef specDef = specOtdr.find(id);
				if (specDef.getParentId() != null && !specDef.getParentId().isBlank()) {
					SpecHolder value = specOtdr.getValue(specDef.getParentId(), product);
					if (value.getGuarantee().getAvailable()) {
						return true;
					}
					else {
						return false;
					}
				}
				return true;
			}

		};
		
		new TableFrame(model, tableFrameInterface) {

			@Override
			void save() {
				saveToFile(specOtdr);
			}

			@Override
			void newProduct(String value) {
				specOtdr.addProduct("EXFO", value);
				updateModel(specOtdr, model, true);
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
				SpecDef specDef = specOtdr.specDef(id);
				return specDef;
			}
			
			@Override
			SpecHolder getSpecValue(int row, int col) {
				String id = list.get(row).get(0).toString();
				String model = title.get(col).toString();
				SpecHolder spec = specOtdr.getValue(id, model);
				return spec;
			}

			@Override
			String getModel(int col) {
				String model = title.get(col).toString();
				return model;
			}

			@Override
			void onUpdate() {
				updateModel(specOtdr, model, true);
			}

			@Override
			void moveUp(int row, int col) {
				if (col == SPEC_COL) {
					SpecDef specDef = specDefByRow(row);
					specDef.moveUp();
				}
				else if (col == CATEGORY_COL) {
					String category = list.get(row).get(1);
					specOtdr.moveUp(category);
				}
				updateModel(specOtdr, model, false);
			}

			@Override
			void moveDown(int row, int col) {
				if (col == SPEC_COL) {
					SpecDef specDef = specDefByRow(row);
					specDef.moveDown();
				}
				else if (col == CATEGORY_COL) {
					String category = list.get(row).get(1);
					specOtdr.moveDown(category);	
				}
				updateModel(specOtdr, model, false);
			}

			@Override
			void rename(String oldName, String newName) {
				specOtdr.changeProductName(oldName, newName);
				updateModel(specOtdr, model, true);
			}

			@Override
			void copyProduct(String name) {
				specOtdr.copyProduct(name);
				updateModel(specOtdr, model, true);
			}

			@Override
			void moveLeft(String name) {
				specOtdr.moveLeft(name);
				updateModel(specOtdr, model, true);
			}

			@Override
			void moveRight(String name) {
				specOtdr.moveRight(name);
				updateModel(specOtdr, model, true);
			}

			@Override
			void copyCells(int[] fromRows, String fromColumn, String toColumn) {
				List<String> ids = new ArrayList<>();
				for (int row : fromRows) {
					ids.add(list.get(row).get(0));
				}
				specOtdr.copyCells(ids, fromColumn, toColumn);
				updateModel(specOtdr, model, false);
			}

			@Override
			void delete(int row) {
				specOtdr.delete(list.get(row).get(1), list.get(row).get(0));
				updateModel(specOtdr, model, true);
			}

			@Override
			void onPositioningMap() {
				new PositioningMapUi(new PositioningMapModel(specOtdr)).setVisible(true);
			}
		}.setVisible(true);
	}

	private void loadFile() {
		try {
			this.specOtdr = new ObjectMapper().readValue(new File("otdr.spec"), SpecSheet.class);
			this.specOtdr.init();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void createDemo() {
		String displayDevice = specOtdr.addSpec("Display", "Device", SpecTypeEnum.Choice).choice("LCD").choice("OLED").id();
		String resolutionId = specOtdr.addSpec("Display", "Resolution", SpecTypeEnum.TwoDmensionalSize, "pixels", Better.Higher).id();
		String lcdSize = specOtdr.addSpec("Display", "Size", SpecTypeEnum.Numeric, "inch", Better.Higher).id();
	
		String usb = specOtdr.addSpec("Interface", "USB", SpecTypeEnum.Numeric, "", Better.Higher).id();
		String eth = specOtdr.addSpec("Interface", "Ethernet", SpecTypeEnum.Numeric, "Mbps", Better.Higher).id();
		String storageSize = specOtdr.addSpec("Storage", "Size", SpecTypeEnum.Numeric, "GB", Better.Higher).id();
	
		String batteryType = specOtdr.addSpec("Battery", "type", SpecTypeEnum.Choice).choice("lithium-polymer").choice("lithium-ion").id();
		String runtime = specOtdr.addSpec("Battery", "Runtime", SpecTypeEnum.Numeric, "Hours", Better.Higher).id();
	
		String powerSupply = specOtdr.addSpec("Power Supply", "Input", SpecTypeEnum.Range, "VAC", Better.Wider).id();
		String powerConsumption = specOtdr.addSpec("Power Supply", "Consumption", SpecTypeEnum.Numeric, "Watts", Better.Lower).id();

		String w850 = specOtdr.addSpec("Wavelength", "850nm", SpecTypeEnum.Boolean).id();
		String w850_accuracy = specOtdr.addSpec("Wavelength", "850nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();
		
		String w1300 = specOtdr.addSpec("Wavelength", "1300nm", SpecTypeEnum.Boolean).id();
		String w1300_accuracy = specOtdr.addSpec("Wavelength", "1300nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();
		
		String w1490 = specOtdr.addSpec("Wavelength", "1490nm", SpecTypeEnum.Boolean).id();
		String w1490_accuracy = specOtdr.addSpec("Wavelength", "1490nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();
		
		String w1310 = specOtdr.addSpec("Wavelength", "1310nm", SpecTypeEnum.Boolean).id();
		String w1310_accuracy = specOtdr.addSpec("Wavelength", "1310nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();

		
		String w1550 = specOtdr.addSpec("Wavelength", "1550nm", SpecTypeEnum.Boolean).id();
		String w1550_accuracy = specOtdr.addSpec("Wavelength", "1550nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();

		String w1625 = specOtdr.addSpec("Wavelength", "1625nm", SpecTypeEnum.Boolean).id();
		String w1625f = specOtdr.addSpec("Wavelength", "1625nm (Filter)", SpecTypeEnum.Boolean).id();
		String w1625_accuracy = specOtdr.addSpec("Wavelength", "1625nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();

		
		String w1650f = specOtdr.addSpec("Wavelength", "1650nm (filtered)", SpecTypeEnum.Boolean).id();
		String w1650_accuracy = specOtdr.addSpec("Wavelength", "1650nm accuracy", SpecTypeEnum.Range, "nm", Better.Narrower).id();

		String dr850 = specOtdr.addSpec("Dynamic Range", "850nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		String dr1300 = specOtdr.addSpec("Dynamic Range", "1300nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();		
		String dr1310 = specOtdr.addSpec("Dynamic Range", "1310nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		String dr1490 = specOtdr.addSpec("Dynamic Range", "1490nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		String dr1550 = specOtdr.addSpec("Dynamic Range", "1550nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		String dr1625 = specOtdr.addSpec("Dynamic Range", "1625nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		String dr1650 = specOtdr.addSpec("Dynamic Range", "1650nm", SpecTypeEnum.Numeric, "dB", Better.Higher).id();
		
		String filterSpecHighpass = specOtdr.addSpec("Built-in Filter (1625nm)", "Highpass", SpecTypeEnum.Numeric, "nm", Better.Higher).id();
		String filterSpecIsolation = specOtdr.addSpec("Built-in Filter (1625nm)", "Isolation", SpecTypeEnum.Numeric, "dB", Better.Higher).id();

		String eventDeadZone = specOtdr.addSpec("Event dead zone", "", SpecTypeEnum.Numeric, "m", Better.Higher).id();
		String attenuationDeadZone = specOtdr.addSpec("Attenuation dead zone", "", SpecTypeEnum.Numeric, "m", Better.Higher).id();

		
		ProductSpec maxTester715B = specOtdr.addProduct("EXFO", "MaxTester 715B");
		maxTester715B.guarantee(displayDevice, "LCD");
		maxTester715B.guarantee(resolutionId, 800, 480);
		maxTester715B.guarantee(lcdSize, 7);
		
		maxTester715B.guarantee(usb,  2.0);
		maxTester715B.guarantee(eth, 100);
		maxTester715B.guarantee(storageSize, 20);
		
		maxTester715B.guarantee(batteryType, "lithium-polymer");
		maxTester715B.guarantee(runtime, 12);
		
		maxTester715B.guarantee(powerSupply, 100, 240);
		maxTester715B.guarantee(powerConsumption, 15);
		
		maxTester715B.guarantee(w850, false);
				
		maxTester715B.guarantee(w1300, false);
		
		maxTester715B.guarantee(w1310, true);
		maxTester715B.typical(w1310_accuracy, -30, 30);
		
		maxTester715B.guarantee(w1550, true);
		maxTester715B.typical(w1550_accuracy, -30, 30);
		
		maxTester715B.guarantee(w1490, false);
		
		maxTester715B.guarantee(w1625, false);
		maxTester715B.guarantee(w1625f, true);
		maxTester715B.typical(w1625_accuracy, -10, 10);
		
		maxTester715B.guarantee(w1650f, false);
		
		maxTester715B.typical(dr1310, 30);
		maxTester715B.typical(dr1550, 28);
		maxTester715B.typical(dr1625, 28);
		
		maxTester715B.guarantee(filterSpecHighpass, 1595);
		maxTester715B.guarantee(filterSpecIsolation, 50);
		
		maxTester715B.typical(eventDeadZone, 1);
		maxTester715B.typical(attenuationDeadZone, 4);
		
		this.specOtdr.init();
	}

	protected void saveToFile(SpecSheet specOtdr) {
		try {
			new ObjectMapper().writeValue(new File("otdr.spec"), specOtdr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateModel(SpecSheet specOtdr, AbstractTableModel model, boolean structureChanged) {
		this.list.clear();
		this.title.clear();
		
		Map<String, ProductSpec> ps = specOtdr.products();
		for (String category : specOtdr.categories()) {
			Map<String, SpecDef> specs = specOtdr.getSpecs(category);
			for (String specName : specs.keySet()) {
				SpecDef spec = specs.get(specName);
				List<String> line = new ArrayList<>();
				line.add(spec.id());
//				line.add(spec.getSpecType().toString());
				line.add(category);
				String specWithUnit = specName;
				if (!spec.getUnit().isBlank()) {
					specWithUnit = specName + " (" + spec.getUnit() + ")";
				}
				line.add(specWithUnit);
//				line.add(spec.unit());
//				line.add(spec.id());
				
				
				for (ProductSpec pn : ps.values()) {
					line.add(pn.value(spec.id()) /*+ " " + spec.unit()*/);
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

	}
}
