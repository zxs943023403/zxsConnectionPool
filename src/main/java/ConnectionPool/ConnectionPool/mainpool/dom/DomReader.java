package ConnectionPool.ConnectionPool.mainpool.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConnectionPool.ConnectionPool.mainpool.Pool;
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
	 * 读取if,when之类的标签中的test，并加上之前的sql
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
	
	public static void readPublicSql(Node n,DomFactory factory) {
		NamedNodeMap attr = n.getAttributes();
		String id = "";
		for (int i = 0; i < attr.getLength(); i++) {
			if ("id".equals(attr.item(i).getNodeName())) {
				id = attr.item(i).getTextContent();
				break;
			}
		}
		NodeList childern = n.getChildNodes();
		String sql = "";
		for (int i = 0; i < childern.getLength(); i++) {
			Node child = childern.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				sql += child.getTextContent();
			}
		}
		factory.addPublicSql(id, sql);
	}
	
	public static String readIncludeSql(Node include,String sql,Map<String, Object> args,DomFactory factory) {
		NamedNodeMap attr = include.getAttributes();
		String id = "";
		for (int i = 0; i < attr.getLength(); i++) {
			if ("refid".equals(attr.item(i).getNodeName())) {
				id = attr.item(i).getTextContent();
				break;
			}
		}
		NodeList childern = include.getChildNodes();
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
	
	public static Properties readProperties(Node node,Properties properties) throws IOException {
		Map<String, String> attr = readNodeAttrs(node);
		String resources = attr.get("resource");
		properties.load(Pool.class.getClassLoader().getResourceAsStream(resources));
		NodeList childern = node.getChildNodes();
		for (int i = 0; i < childern.getLength(); i++) {
			Node child = childern.item(i);
			if ("property".equals(child.getNodeName())) {
				Map<String, String> values = readNodeAttrs(child);
				properties.putAll(values);
			}
		}
		return properties;
	}
	
	public static DataSource readDatasource(Node node,Properties properties) {
		Map<String, String> attrs = readNodeAttrs(node);
		DataSource source = new DataSource();
		source.id = attrs.get("id");
		NodeList childern = node.getChildNodes();
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < childern.getLength(); i++) {
			Node child = childern.item(i);
			if ("property".equals(child.getNodeName())) {
				Map<String, String> childAttr = readNodeAttrs(child);
				map.put(childAttr.get("name"), replaceAttr(childAttr.get("value"), properties));
			}
		}
		source.attrs = map;
		return source;
	}
	
	private static Map<String, String> readNodeAttrs(Node node){
		Map<String, String> attrs = new HashMap<String, String>();
		NamedNodeMap nodeAttr = node.getAttributes();
		for (int i = 0; i < nodeAttr.getLength(); i++) {
			attrs.put(nodeAttr.item(i).getNodeName(), nodeAttr.item(i).getTextContent());
		}
		return attrs;
	}

	private static String strPattern = "(?<=\\$\\{)(.+?)(?=\\})";
	private static String replaceAttr(String context,Properties properties) {
		Pattern p = Pattern.compile(strPattern);
		StringBuffer sql = new StringBuffer(context);
		if (hasParams(context, strPattern) && null != properties ) {
			Matcher m = p.matcher(sql);
			List<String> ls = new ArrayList<String>();
			while (m.find()) {
				ls.add(m.group());
			}
			for (String string : ls) {
				sql = new StringBuffer(sql.toString().replaceAll("\\$\\{" + string + "\\}", properties.get(string)+""));
			}
		}
		return sql.toString();
	}

	private static boolean hasParams(String context, String pattern) {
		if (context == null || "".equals(context)) {
			return false;
		}
		return Pattern.compile(pattern).matcher(context).find();
	}
	
}
