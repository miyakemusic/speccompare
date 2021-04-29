package positioningmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import positioningmap.PMConfigValue.Level;

class MyList {
	private List<MyRow> list = new ArrayList<>();
	public MyRow newRow(String category) {
		MyRow row = new MyRow();
		row.add(category);
		list.add(row);
		return row;
	}
	public int rowCount() {
		return list.size();
	}
	public MyRow row(int rowIndex) {
		return this.list.get(rowIndex);
	}
	public void clear() {
		list.clear();
	}
	
}
class MyRow {
	private List<String> row = new ArrayList<>();
//	private List<PMConfigValue> values = new ArrayList<>();
	private String id;
	public void add(String string) {
		row.add(string);
	}

	public String text(int index) {
		return row.get(index);
	}
	public void id(String id) {
		this.id = id;
	}
	public String id() {
		return this.id;
	}

	public int size() {
		return row.size();
	}
}

public class PMConfigModel extends AbstractTableModel {

	private MyList myList = new MyList();
	private PMDefContainer pmdefs = new PMDefContainer();
	private List<String> title = new ArrayList<>();
	private SpecSheet specSheet;
	public PMConfigModel(SpecSheet specSheet) {
		pmdefs.add("MFH/FTTH(G-PON/GE-PON)");
		pmdefs.add("MFH(NG-PON2)");
		pmdefs.add("MFH/DAA/RPHY(WDM)");
		pmdefs.add("MFH(OADM)");
		pmdefs.add("Core");
		pmdefs.add("Metro");
		pmdefs.add("Submarine");
		pmdefs.add("DCI");
		pmdefs.add("DCI(ROADM)");
		pmdefs.add("Fiber Manufacturing");
		
		this.specSheet = specSheet;
		update();
	}

	private void update() {
		title.clear();
		myList.clear();
		
		specSheet.getCategories().forEach((category, specCategory) -> {
			specCategory.getSpecs().forEach((specName, specDef) -> {
				MyRow row = myList.newRow(category);
				row.add(specName);
				row.id(specDef.id());
			});
		});
		System.out.println();
		
		title.add("Category");
		title.add("Spec");
		pmdefs.defs().forEach(v -> {
			title.add(v);
		});
	}

	@Override
	public int getRowCount() {
		return myList.rowCount();
	}

	@Override
	public int getColumnCount() {
		return title.size();
	}

	@Override
	public String getColumnName(int column) {
		return title.get(column);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		MyRow row = myList.row(rowIndex);
		
		if (columnIndex < row.size()) {
			return row.text(columnIndex);
		}
		else {
			String id = myList.row(rowIndex).id();
			return pmdefs.get(title.get(columnIndex)).value(id).toString();
		}
	}

	public PMConfigValue getValue(int selectedRow, int selectedColumn) {
		PMConfigValue ret =  pmdefs.get(title.get(selectedColumn)).value(myList.row(selectedRow).id());
		return ret;
	}

}
