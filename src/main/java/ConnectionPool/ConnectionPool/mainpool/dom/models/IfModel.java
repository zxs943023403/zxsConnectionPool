package ConnectionPool.ConnectionPool.mainpool.dom.models;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConnectionPool.ConnectionPool.util.PoolUtil;

public class IfModel extends MainModel {

	public String sql;
	
	@Override
	public Object read() {
		NamedNodeMap childAttr = node.getAttributes();
		String test = "";
		for (int j = 0; j < childAttr.getLength(); j++) {
			if ("test".equals(childAttr.item(j).getNodeName())) {
				test = childAttr.item(j).getTextContent();
			}
		}
		if (PoolUtil.Cal(test, args)) {
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
		}
		return sql;
	}

}
