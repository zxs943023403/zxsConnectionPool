package ConnectionPool.ConnectionPool.mainpool.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConnectionPool.ConnectionPool.mainpool.dom.models.MainModel;
import ConnectionPool.ConnectionPool.mainpool.dom.models.Sqls;
import ConnectionPool.ConnectionPool.mainpool.dom.resultmapmodel.ResultMap;
import ConnectionPool.ConnectionPool.proxy.PoolProxy;
import ConnectionPool.ConnectionPool.util.PoolUtil;

public class DomFactory {
	
	private static ConcurrentHashMap<String, Map<String,Node>> namespaceDom;
	private static ConcurrentHashMap<String, ResultMap> resultMaps;
	private static ConcurrentHashMap<String, String> publicSqls;
	private static ConcurrentHashMap<String, Node> domMap;
	private static PoolProxy proxys = PoolProxy.getProxyFactory();
	private static final String MAIN_MODEL_CLASS = "ConnectionPool.ConnectionPool.mainpool.dom.models.MainModel";
	
	public void addDom(String namespace,String id,Node node) {
		if (namespaceDom.containsKey(namespace)) {
			Map<String, Node> nodes = namespaceDom.get(namespace);
			nodes.put(id, node);
		}else {
			Map<String, Node> nodes = new HashMap<String, Node>();
			nodes.put(id, node);
			namespaceDom.put(namespace, nodes);
		}
		proxys.newMapper(namespace);
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
	
	public void initSqlDom(Node doms) {
		try {
			NodeList dom = doms.getChildNodes();
			for (int i = 0; i < dom.getLength(); i++) {
				Node domNode = dom.item(i);
				if (domNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				Map<String, String> attr = PoolUtil.readNodeAttrs(domNode);
				Class clazz = Class.forName(attr.get("class"));
				String className = clazz.getName();
				if (!MAIN_MODEL_CLASS.equals(clazz.getSuperclass().getName())) {
					throw new RuntimeException("class:"+className+" must extend "+MAIN_MODEL_CLASS);
				}
				MainModel mm = (MainModel) clazz.newInstance();
				mm.clazz = clazz;
				mm.domParam = domNode;
				mm.factory = this;
				domMap.put(attr.get("id"), domNode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized Object readDom(String id,Node node,Map<String, Object> args) {
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return null;
		}
		Node domNode = domMap.get(id);
		Map<String, String> attrNode = PoolUtil.readNodeAttrs(domNode);
		MainModel mm = null;
		Class clazz = null;
		try {
			clazz = Class.forName(attrNode.get("class"));
			mm = (MainModel) clazz.newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null == mm) {
			throw new RuntimeException(id+" 标签暂未配置！");
		}
		mm.clazz = clazz;
		mm.domParam = domNode;
		mm.factory = this;
		Node dom = mm.domParam;
		mm.node = node;
		mm.args = args;
		NodeList params = dom.getChildNodes();
		for (int i = 0; i < params.getLength(); i++) {
			Node param = params.item(i);
			if (param.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Map<String, String> attr = PoolUtil.readNodeAttrs(param);
			attr.put("value",null == args.get(attr.get("key"))?attr.get("value"):args.get(attr.get("key"))+"");
			try {
				mm.getClass().getField(attr.get("key")).set(mm, attr.get("value"));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mm.read();
	}
	
	private static DomFactory factory = new DomFactory();
	
	public synchronized static DomFactory getFactory() {
		return factory;
	}
	
	private DomFactory() {
		namespaceDom = new ConcurrentHashMap<String, Map<String,Node>>();
		resultMaps = new ConcurrentHashMap<String, ResultMap>();
		publicSqls = new ConcurrentHashMap<String,String>();
		domMap = new ConcurrentHashMap<String, Node>();
	}
	
}
