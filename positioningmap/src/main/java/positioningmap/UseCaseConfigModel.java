package positioningmap;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

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

public abstract class UseCaseConfigModel extends AbstractTableModel {

	private MyList myList = new MyList();
	
	private List<String> title = new ArrayList<>();
	private SpecSheet specSheet;

	private UseCaseContainer pmdefs;
	public UseCaseConfigModel(SpecSheet specSheet, UseCaseContainer pmdefs2) {
		this.pmdefs =pmdefs2;
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

	public UseCaseDefElement getValue(int selectedRow, int selectedColumn) {
		UseCaseDefElement ret =  pmdefs.get(title.get(selectedColumn)).value(myList.row(selectedRow).id());
		return ret;
	}


	abstract protected void save();

}
