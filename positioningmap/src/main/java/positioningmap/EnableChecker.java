package positioningmap;

public class EnableChecker {

	private boolean ret = true;

//	private boolean ret = true;
	public boolean check(SpecSheet specSheet, String id, String product) {
		check2(specSheet, id, product);
		return ret;
	}

	private Result check2(SpecSheet specSheet, String id, String product) {
		SpecDef specDef = specSheet.find(id);
		String parentId = specDef.getParentId();
		if (parentId == null || parentId.isEmpty()) {
			return Result.NO_PARENT;
		}
		else {
			if (!specSheet.getValue(parentId, product).getGuarantee().getAvailable()) {
				this.ret  = false;
				return Result.FALSE;
			}		
			else {
				Result result = check2(specSheet, parentId, product);
				if (result.compareTo(Result.NO_PARENT) == 0) {
					return Result.NO_PARENT;
				}
				else if (result.compareTo(Result.TRUE) == 0) {
					return Result.TRUE;
				}
				else if (result.compareTo(Result.FALSE) == 0) {
					this.ret  = false;
					return Result.FALSE;
				}
			}
		}
		return Result.TRUE;
	}
	
	enum Result {
		NO_PARENT,
		TRUE,
		FALSE
	}

}
