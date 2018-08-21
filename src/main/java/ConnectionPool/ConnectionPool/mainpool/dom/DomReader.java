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
				map.put(childAttr.get("name"),PoolUtil.replaceAttr(childAttr.get("value"),strPattern, properties));
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

}
