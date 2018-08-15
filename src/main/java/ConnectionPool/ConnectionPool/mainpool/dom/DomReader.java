package ConnectionPool.ConnectionPool.mainpool.dom;

import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConnectionPool.ConnectionPool.mainpool.dom.resultmapmodel.ResultMap;
import ConnectionPool.ConnectionPool.mainpool.dom.resultmapmodel.ResultMapResult;
import ConnectionPool.ConnectionPool.util.PoolUtil;

public class DomReader {
	public static void readResultMap(Node n,DomFactory factory) {
		NodeList columns = n.getChildNodes();
		NamedNodeMap columnsNNM = n.getAttributes();
		ResultMap map = new ResultMap();
		for (int i = 0; i < columnsNNM.getLength(); i++) {
			if ("id".equals(columnsNNM.item(i).getNodeName())) {
				map.mapId = columnsNNM.item(i).getTextContent();
			}
			if ("type".equals(columnsNNM.item(i).getNodeName())) {
				map.mapType = columnsNNM.item(i).getTextContent();
			}
		}
		for (int i = 0; i < columns.getLength(); i++) {
			Node n1 = columns.item(i);
			if (n1.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			ResultMapResult r = new ResultMapResult();
			NamedNodeMap nnm = n1.getAttributes();
			for (int j = 0; j < nnm.getLength(); j++) {
				if ("property".equals(nnm.item(j).getNodeName())) {
					r.properties = nnm.item(j).getTextContent();
				}
				if ("column".equals(nnm.item(j).getNodeName())) {
					r.column = nnm.item(j).getTextContent();
				}
			}
			if ("id".equals(n1.getNodeName())) {
				r.type = "id";
				map.id = r;
			}else {
				r.type = "result";
				map.results.add(r);
			}
		}
		factory.addResultMap(map.mapId, map);
	}
	
	public static void readSql(Node n,DomFactory factory) {
		NamedNodeMap params = n.getAttributes();
		String namespace = "";
		for (int i = 0; i < params.getLength(); i++) {
			if ("namespace".equals(params.item(i).getNodeName())) {
				namespace = params.item(i).getTextContent();
				break;
			}
		}
		NodeList sqls = n.getChildNodes();
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
	}
	
	/**
	 * 读取if,when之类的标签中的test
	 * @param node
	 * @param sql
	 * @param args
	 * @return
	 */
	public static String readNodeTest(Node node,String sql,Map<String, Object> args) {
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
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					sql = readNodeTest(child,sql,args);
				}
				if (child.getNodeType() == Node.TEXT_NODE) {
					sql += child.getTextContent();
				}
			}
		}
		return sql;
	}
	
}
