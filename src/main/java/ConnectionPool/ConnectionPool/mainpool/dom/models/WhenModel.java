package ConnectionPool.ConnectionPool.mainpool.dom.models;

import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConnectionPool.ConnectionPool.util.PoolUtil;

public class WhenModel extends MainModel {

	public String sql;
	
	@Override
	public Object read() {
		Map<String, String> attr = PoolUtil.readNodeAttrs(node);
		String test = attr.get("test");
		String result = "";
		if (PoolUtil.Cal(test, args)) {
			NodeList childern = node.getChildNodes();
			for (int i = 0; i < childern.getLength(); i++) {
				Node child = childern.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					result += child.getTextContent();
				}
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					result += (String) factory.readDom(child.getNodeName(), child, args);
				}
			}
		}
		return result;
	}

}
