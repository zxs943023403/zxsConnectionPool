package ConnectionPool.ConnectionPool.mainpool.dom.models;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PublicSqlModel extends MainModel {

	@Override
	public Object read() {
		NamedNodeMap attr = node.getAttributes();
		String id = "";
		for (int i = 0; i < attr.getLength(); i++) {
			if ("id".equals(attr.item(i).getNodeName())) {
				id = attr.item(i).getTextContent();
				break;
			}
		}
		NodeList childern = node.getChildNodes();
		String sql = "";
		for (int i = 0; i < childern.getLength(); i++) {
			Node child = childern.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				sql += child.getTextContent();
			}
		}
		factory.addPublicSql(id, sql);
		return null;
	}

}
