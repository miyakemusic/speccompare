package positioningmap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapMover<K, V> {
	private Map<K, V> map;

	public MapMover(Map<K, V> map) {
		this.map = map;
	}
	
	public void moveUp(V v) {
		if (new ArrayList<V>(map.values()).indexOf(v) == 0) {
			return;
		}
		Map<K, V> tmp = new LinkedHashMap<>();
		K lastKey = null;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(v)) {
				V lastValue = tmp.get(lastKey);
				tmp.remove(lastKey);
				tmp.put(entry.getKey(), entry.getValue());
				tmp.put(lastKey, lastValue);
			}
			else {
				tmp.put(entry.getKey(), entry.getValue());
			}
			lastKey = entry.getKey();
		}		
		
		map.clear();
		map.putAll(tmp);
	}
	
	public void moveDown(V v) {
		if (new ArrayList<V>(map.values()).indexOf(v) == map.size()-1) {
			return;
		}
		Map<K, V> tmp = new LinkedHashMap<>();
		K key = null;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(v)) {
				key = entry.getKey();
				continue;
			}
			tmp.put(entry.getKey(), entry.getValue());	
			if (key != null)  {
				tmp.put(key, map.get(key));
				key = null;
			}
		}
		map.clear();
		map.putAll(tmp);
	}
}