package ConnectionPool.ConnectionPool.mainpool.dom;

import java.util.Map;

public class DataSource {
	public String id;
	public Map<String, String> attrs;
	public String get(String key,String defaults) {
		if (attrs.containsKey(key)) {
			return attrs.get(key);
		}
		return defaults;
	}
	public String get(String key) {
		return get(key,null);
	}
}
