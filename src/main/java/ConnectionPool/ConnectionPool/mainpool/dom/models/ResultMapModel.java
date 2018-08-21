package ConnectionPool.ConnectionPool.mainpool.dom.models;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConnectionPool.ConnectionPool.mainpool.dom.resultmapmodel.ResultMap;
import ConnectionPool.ConnectionPool.mainpool.dom.resultmapmodel.ResultMapResult;

public class ResultMapModel extends MainModel {
	
	public ResultMapModel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object read() {
		NodeList columns = node.getChildNodes();
		NamedNodeMap columnsNNM = node.getAttributes();
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
		return null;
	}

}
