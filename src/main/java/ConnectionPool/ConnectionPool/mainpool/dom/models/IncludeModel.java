package ConnectionPool.ConnectionPool.mainpool.dom.models;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IncludeModel extends MainModel {
	
	public String sql;

	@Override
	public Object read() {
		NamedNodeMap attr = node.getAttributes();
		String id = "";
		for (int i = 0; i < attr.getLength(); i++) {
			if ("refid".equals(attr.item(i).getNodeName())) {
				id = attr.item(i).getTextContent();
				break;
			}
		}
		NodeList childern = node.getChildNodes();
		for (int i = 0; i < childern.getLength(); i++) {
			Node child = childern.item(i);
			if ("property".equals(child.getNodeName())) {
				NamedNodeMap childattr = child.getAttributes();
				String name = "";
				String value = "";
				for (int j = 0; j < childattr.getLength(); j++) {
					if ("name".equals(childattr.item(j).getNodeName())) {
						name = childattr.item(j).getTextContent();
					}
					if ("value".equals(childattr.item(j).getNodeName())) {
						value = childattr.item(j).getTextContent();
					}
				}
				args.put(name, args.get(value));
			}
		}
		String pubsql = factory.getPublicSql(id);
		return sql +pubsql;
	}

}
