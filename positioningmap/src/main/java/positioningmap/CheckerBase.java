package positioningmap;

import java.util.HashMap;
import java.util.Map;

public class CheckerBase<T> {
	private Map<String, Map<String, T>> mapMap = new HashMap<>();

	public void put(String product, String id, T ret2) {
		if (!this.mapMap.containsKey(product)) {
			this.mapMap.put(product, new HashMap<String, T>());
		}
		this.mapMap.get(product).put(id, ret2);
	}

	public T get(String product, String id) throws Exception {
		Map<String, T> idEnabled = this.mapMap.get(product);
		if (idEnabled == null) {
			throw new Exception();
		}
		T ret = idEnabled.get(id);
		if (ret == null) {
			throw new Exception();
		}
		return ret;
	}

	public void remove(String product) {
		this.mapMap.get(product).clear();
	}

	public void clear() {
		this.mapMap.clear();
	}
}
