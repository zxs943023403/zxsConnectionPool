package ConnectionPool.ConnectionPool.mainpool;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mysql.jdbc.StringUtils;

import ConnectionPool.ConnectionPool.mainpool.Pool.EXEC_TYPE;
import ConnectionPool.ConnectionPool.mainpool.dom.DataSource;
import ConnectionPool.ConnectionPool.mainpool.dom.DomReader;


public class PoolConfig {
	private static ConcurrentHashMap<String, Pool> pools = new ConcurrentHashMap<String, Pool>();
	private static Pool signlePool;
	private static DocumentBuilderFactory documentFactory ;
	private static DocumentBuilder builder ;
	
	private static PoolConfig config = new PoolConfig();
	
	private PoolConfig() {
		try {
			documentFactory = DocumentBuilderFactory.newInstance();
			builder = documentFactory.newDocumentBuilder();
			Document document = builder.parse(Pool.class.getClassLoader().getResourceAsStream("resources.xml"));
			Node configurationNode = document.getChildNodes().item(0);
			NodeList nodes = configurationNode.getChildNodes();
			Properties properties = new Properties();
			//read properties
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if ("properties".equals(node.getNodeName())) {
					properties = DomReader.readProperties(node,properties);
				}
			}
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if ("dataSource".equals(node.getNodeName())) {
					DataSource source = DomReader.readDatasource(node,properties);
					Pool p = new Pool(source);
					pools.put(source.id, p);
					if (null == signlePool) {
						signlePool = p;
					}
				}
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static PoolConfig getConfig() {
		return config;
	}
	
	public Object poolExec(String namespace,String id,Map<String, Object> args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException {
		return poolExec("", namespace, id, args);
	}
	
	public Object poolExec(String datasourceid,String namespace,String id,Map<String, Object> args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException {
		Pool p = null;
		long start = System.currentTimeMillis();
		if (StringUtils.isNullOrEmpty(datasourceid)) {
			p = signlePool;
		}else {
			p = pools.get(datasourceid);
		}
		System.out.println("get pool cost:"+(System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		Object obj = p.exec(namespace, id, args);
		System.out.println("get result cost:"+(System.currentTimeMillis() - start));
		return obj;
	}
	
	public Object poolExec(String sql,EXEC_TYPE type,Object ...args) throws InterruptedException {
		return poolExec("", sql, type, args);
	}
	
	public Object poolExec(String datasourceid,String sql,EXEC_TYPE type,Object ...args) throws InterruptedException {
		Pool p = null;
		if (StringUtils.isNullOrEmpty(datasourceid)) {
			p = signlePool;
		}else {
			p = pools.get(datasourceid);
		}
		return p.exec(sql, type, args);
	}
	
}
