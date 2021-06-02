package positioningmap;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

interface CombinationModelInterface {

	List<String> getConditions();

	String productName();

	ConditionContainer container();
	
}

public class CombinationModel extends AbstractTableModel {
	private List<String> headers;
	
	public CombinationModel(CombinationModelInterface combinationModelInterface) {
		ConditionContainer conditionContainer = combinationModelInterface.container();//new ConditionContainer(combinationModelInterface.getConditions());
		ProductName productName = new ProductName(combinationModelInterface.productName());

		headers = analyze(conditionContainer, productName);
	}

	private List<String> analyze(ConditionContainer conditionContainer, ProductName productName) {
		List<String> ret = new ArrayList<>();
		for (int i = 0; i < conditionContainer.conditionNameList().size(); i++) {
			String conditionName = conditionContainer.conditionNameList().get(i);
			List<String> values = conditionContainer.getValues(conditionName);
			if (i == 0) {
				ret.addAll(values);
			}
			else {
				List<String> tmp = new ArrayList<>();
				ret.forEach(s ->{
					values.forEach(ss -> {
						tmp.add(s + "-" + ss);
					});
				});
				ret.clear();
				ret.addAll(tmp);
			}
			//System.out.println(values);
			//analyze(conditionContainer, startIndex + 1);
		}
		return ret;
	}

	private List<String> recursive(ConditionContainer conditionContainer, String productName, int startIndex, String conditionName) {
		List<String> ret = new ArrayList<>();
		for (String value : conditionContainer.getValues(conditionName)) {
			for (int j = startIndex; j < conditionContainer.conditionNameList().size(); j++) {
				String conditionName2 = conditionContainer.conditionNameList().get(j);
				
				List<String> subRet = recursive(conditionContainer, productName, startIndex + 1, conditionName2);
				if (subRet.size() > 0) {
					subRet.forEach(s -> {
						ret.add(value + "-" + s);
					});
				}
				else {
					ret.add(value);
				}
			}
		}
		return ret;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public String getColumnName(int column) {
		return this.headers.get(column);
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return headers.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return "";
	}

}
