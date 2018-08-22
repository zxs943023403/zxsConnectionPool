package ConnectionPool.ConnectionPool.mainpool.dom.models;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OtherwiseModel extends MainModel {

	public String sql;
	public String oldsql;
	
	@Override
	public Object read() {
		if (!sql.trim().equals(oldsql.trim())) {
			return sql;
		}
		NodeList childern = node.getChildNodes();
		for (int i = 0; i < childern.getLength(); i++) {
			Node child = childern.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				sql += child.getTextContent();
			}
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				sql = (String) factory.readDom(child.getNodeName(), child, args);
			}
		}
		return sql;
	}

}
