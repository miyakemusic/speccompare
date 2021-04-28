package positioningmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

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
	
}
class MyRow {
	private List<String> row = new ArrayList<>();
	private String id;
	public void add(String string) {
		row.add(string);
	}
	public String col(int columnIndex) {
		return row.get(columnIndex);
	}
	public void id(String id) {
		this.id = id;
	}
	public String id() {
		return this.id;
	}
	
}
public class PMConfigModel extends AbstractTableModel {

	private MyList myList = new MyList();
	private PMDef pmdefs = new PMDef();
	private List<String> title = new ArrayList<>();
	public PMConfigModel(SpecSheet specSheet) {
		pmdefs.add("MFH");
		pmdefs.add("DCI");
		
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
		if (columnIndex < 2) {
			return myList.row(rowIndex).col(columnIndex);
		}
		else {
			String id = myList.row(rowIndex).id();
			return pmdefs.get(columnIndex -2).id(id);
		}
	}

}
