package ConnectionPool.ConnectionPool.mainpool.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConnectionPool.ConnectionPool.mainpool.dom.resultmapmodel.ResultMap;
import ConnectionPool.ConnectionPool.util.PoolUtil;

public class DomFactory {
	
	private static ConcurrentHashMap<String, Map<String,Node>> namespaceDom;
	private static ConcurrentHashMap<String, ResultMap> resultMaps;
	private static ConcurrentHashMap<String, String> publicSqls;
	
	public void addDom(String namespace,String id,Node node) {
		if (namespaceDom.containsKey(namespace)) {
			Map<String, Node> nodes = namespaceDom.get(namespace);
			nodes.put(id, node);
		}else {
			Map<String, Node> nodes = new HashMap<String, Node>();
			nodes.put(id, node);
			namespaceDom.put(namespace, nodes);
		}
	}
	
	public void addResultMap(String id,ResultMap map) {
		resultMaps.put(id, map);
	}
	
	public void addPublicSql(String id,String sql) {
		publicSqls.put(id, sql);
	}
	
	public Node getNode(String namespace,String id) {
		return namespaceDom.get(namespace).get(id);
	}
	public ResultMap getResultMap(String id) {
		return resultMaps.get(id);
	}
	
	public String getPublicSql(String id) {
		return publicSqls.get(id);
	}
	
	public Sqls getSqlFromNode(Node n,Map<String, Object> args) {
		NamedNodeMap attrs = n.getAttributes();
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
		NodeList childern = n.getChildNodes();
		String sql = "";
		for (int i = 0; i < childern.getLength(); i++) {
			Node child = childern.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				sql += child.getTextContent();
			}
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if ("if".equals(child.getNodeName())) {
					sql = DomReader.readNodeTest(child, sql,args);
				}
				if ("include".equals(child.getNodeName())) {
					sql = DomReader.readIncludeSql(child, sql, args,factory);
				}
			}
		}
		sqls.sql = sql;
		sqls.sqlParams(args);
		return sqls;
	}
	
	private static DomFactory factory = new DomFactory();
	
	public synchronized static DomFactory getFactory() {
		return factory;
	}
	
	private DomFactory() {
		namespaceDom = new ConcurrentHashMap<String, Map<String,Node>>();
		resultMaps = new ConcurrentHashMap<String, ResultMap>();
		publicSqls = new ConcurrentHashMap<String,String>();
	}
	
}
