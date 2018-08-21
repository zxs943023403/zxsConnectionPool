package ConnectionPool.ConnectionPool.mainpool.dom.models;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import ConnectionPool.ConnectionPool.mainpool.dom.DomFactory;

public abstract class MainModel {
	
	public MainModel clone() {
		MainModel mm = null;
		try {
			mm = (MainModel) this.clazz.newInstance();
			mm.node = this.node;
			mm.domParam = this.domParam;
			mm.clazz = this.clazz;
			mm.factory = this.factory;
			mm.args = this.args;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mm;
	}
	
	public abstract Object read();
	public Node domParam;
	public Node node;
	public Class clazz;
	public DomFactory factory;
	public Map<String, Object> args;
}
