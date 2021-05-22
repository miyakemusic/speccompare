package positioningmap;

public abstract class EnableChecker {

	private boolean ret = true;
	abstract protected SpecSheet getSpecSheet();
		
	private CheckerBase<Boolean> mapMap = new CheckerBase<>();
	
	public boolean check(String id, String product) {
		try {
			return calculated(product, id);
		} catch (Exception e) {
			ret = true;
			check2(getSpecSheet(), id, product);
			setCalculated(product, id, ret);
			return ret;
		}
	}	

	private void setCalculated(String product, String id, boolean ret2) {
		this.mapMap.put(product, id, ret2);
	}

	private boolean calculated(String product, String id) throws Exception {
		return this.mapMap.get(product, id);
	}

	public void clearProduct(String product) {
		this.mapMap.remove(product);
	}
	public void clear() {
		this.mapMap.clear();
	}
	
	private Result check2(SpecSheet specSheet, String id, String product) {
		SpecDef specDef = specSheet.find(id);
		if (specDef == null) {
			System.out.println("specDef = null " + id + " product:" + product);
			return Result.FALSE;
		}
		String parentId = specDef.getParentId();
		if (parentId == null || parentId.isEmpty()) {
			return Result.NO_PARENT;
		}
		else {
			if (!specSheet.getValue(parentId, product).guarantee().getAvailable()) {
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
