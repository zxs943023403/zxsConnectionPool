package ConnectionPool.ConnectionPool.mainpool.dom.models;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONArray;

import ConnectionPool.ConnectionPool.util.PoolUtil;

public class ForeachModel extends MainModel {

	@Override
	public Object read() {
		Map<String, String> attr = PoolUtil.readNodeAttrs(node);
		String listKey = attr.get("collection");
		String item = attr.get("item");
		String index = attr.get("index");
		String open = attr.get("open");
		String separator = attr.get("separator");
		String close = attr.get("close");
		Object obj = args.get(listKey);
		if (null == obj) {
			return null;
		}
		NodeList childern = node.getChildNodes();
		String foreach = "";
		for (int i = 0; i < childern.getLength(); i++) {
			Node child = childern.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				foreach += child.getTextContent();
			}else {
				foreach += factory.readDom(child.getNodeName(), child, args);
			}
		}
		foreach = PoolUtil.trimStr(foreach);
		String forStr = " "+open+" ";
		if (obj instanceof Map) {
			Map m = (Map) obj;
			for (Object key : m.keySet()) {
				Map<String, Object> forattr = args;
				forattr.put(index, key);
				forattr.put(item, m.get(key));
				Sqls sqls = new Sqls();
				sqls.sql = foreach;
				sqls.sqlParams(forattr,UUID.randomUUID().toString());
				forStr += sqls.sql;
			}
		}
		if (obj instanceof JSONArray) {
			JSONArray array = (JSONArray) obj;
			for (int i = 0; i < array.size(); i++) {
				Map<String, Object> forattr = args;
				forattr.put(item, array.get(i));
				Sqls sqls = new Sqls();
				sqls.sql = foreach;
				sqls.sqlParams(forattr,UUID.randomUUID().toString());
				forStr += sqls.sql;
				if (i != array.size() -1) {
					forStr += separator;
				}
			}
		}
		forStr += close;
		return forStr;
	}

}
