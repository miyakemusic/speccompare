package positioningmap;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class MapCopier<K,V> {
	public MapCopier(Map<K, V> productSpecs) {
		Map<K, V> newMap = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : productSpecs.entrySet()) {
			handle(entry.getKey(), entry.getValue(), newMap);
		}
		productSpecs.clear();
		productSpecs.putAll(newMap);
	}

	abstract protected void handle(K key, V value, Map<K, V> newMap);
}
