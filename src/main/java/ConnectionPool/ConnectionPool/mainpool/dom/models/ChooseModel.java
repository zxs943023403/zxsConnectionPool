package ConnectionPool.ConnectionPool.mainpool.dom.models;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ChooseModel extends MainModel {

	public String sql;
	
	@Override
	public Object read() {
		args.put("oldsql", sql);
		NodeList whens = node.getChildNodes();
		for (int i = 0; i < whens.getLength(); i++) {
			Node when = whens.item(i);
			if (when.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (!"when".equals(when.getNodeName()) && !"otherwise".equals(when.getNodeName())) {
				throw new RuntimeException("choose标签内"+when.getNodeName()+"子标签非法！");
			}
			args.put("sql", sql);
			sql = (String) factory.readDom(when.getNodeName(), when, args);
		}
		return sql;
	}

}
