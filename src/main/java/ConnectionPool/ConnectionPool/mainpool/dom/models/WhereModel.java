package ConnectionPool.ConnectionPool.mainpool.dom.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mysql.jdbc.StringUtils;

import ConnectionPool.ConnectionPool.util.PoolUtil;

public class WhereModel extends MainModel {

	@Override
	public Object read() {
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
			sql = " where ";
		}
		for (int i = 0; i < sqls.size(); i++) {
			if (i == 0) {
				String str = PoolUtil.trimStr(sqls.get(0));
				if (str.startsWith("and ")) {
					sqls.set(0, str.replaceFirst("and ", ""));
				}
			}
			sql += " "+sqls.get(i) + " ";
		}
		return sql;
	}

}
