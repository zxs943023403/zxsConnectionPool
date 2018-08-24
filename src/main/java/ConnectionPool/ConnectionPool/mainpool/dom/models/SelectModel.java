package ConnectionPool.ConnectionPool.mainpool.dom.models;


import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConnectionPool.ConnectionPool.mainpool.dom.DomReader;

public class SelectModel extends MainModel {
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public Sqls read() {
		NamedNodeMap attrs = node.getAttributes();
		Sqls sqls = new Sqls();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			if ("resultType".equals(attr.getNodeName())) {
				sqls.resultType = attr.getTextContent();
			}
			if ("parameterType".equals(attr.getNodeName())) {
				sqls.paramType = attr.getTextContent();
			}
		}
		NodeList childern = node.getChildNodes();
		String sql = "";
		for (int i = 0; i < childern.getLength(); i++) {
			Node child = childern.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				sql += child.getTextContent();
			}
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				args.put("sql", sql);
				sql += (String) factory.readDom(child.getNodeName(), child, args);
			}
		}
		sqls.sql = sql;
		sqls.sqlParams(args);
		return sqls;
	}

}
