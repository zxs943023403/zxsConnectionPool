package ConnectionPool.ConnectionPool.mainpool.dom.models;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MapperModel extends MainModel {

	@Override
	public Object read() {

		NamedNodeMap params = node.getAttributes();
		String namespace = "";
		for (int i = 0; i < params.getLength(); i++) {
			if ("namespace".equals(params.item(i).getNodeName())) {
				namespace = params.item(i).getTextContent();
				break;
			}
		}
		NodeList sqls = node.getChildNodes();
		for (int i = 0; i < sqls.getLength(); i++) {
			Node sql = sqls.item(i);
			if (sql.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			NamedNodeMap sqlparams = sql.getAttributes();
			String id = "";
			for (int j = 0; j < sqlparams.getLength(); j++) {
				if ("id".equals(sqlparams.item(j).getNodeName())) {
					id = sqlparams.item(j).getTextContent();
					factory.addDom(namespace, id, sql);
					break;
				}
			}
		}
		return null;
	}

}
