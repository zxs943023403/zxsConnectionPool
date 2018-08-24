package ConnectionPool.ConnectionPool.mainpool.dom.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mysql.jdbc.StringUtils;

import ConnectionPool.ConnectionPool.util.PoolUtil;

public class SetModel extends MainModel {

	@Override
	public Object read() {
		Map<String, String> attr = PoolUtil.readNodeAttrs(node);
		String split = attr.get("split");
		NodeList childern = node.getChildNodes();
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < childern.getLength(); i++) {
			Node child = childern.item(i);
			Object result = factory.readDom(child.getNodeName(), child, args);
			if (result instanceof String) {
				if (!StringUtils.isEmptyOrWhitespaceOnly(result+"")) {
					sqls.add(result+"");
				}
			}
		}
		String sql = "";
		if (sqls.size() != 0) {
			sql = " set ";
		}
		for (int i = 0; i < sqls.size(); i++) {
			sql += " "+sqls.get(i)+" ";
			if (i != sqls.size() -1) {
				sql += ",";
			}
		}
		return sql;
	}

}
