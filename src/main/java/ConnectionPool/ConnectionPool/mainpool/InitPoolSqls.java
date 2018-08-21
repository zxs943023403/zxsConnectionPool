package ConnectionPool.ConnectionPool.mainpool;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ConnectionPool.ConnectionPool.mainpool.dom.DomFactory;
import ConnectionPool.ConnectionPool.mainpool.dom.DomReader;
import ConnectionPool.ConnectionPool.mainpool.dom.resultmapmodel.ResultMap;
import ConnectionPool.ConnectionPool.mainpool.dom.resultmapmodel.ResultMapResult;

public class InitPoolSqls {
	private String sqlPath;
	private DomFactory factory;
	private static DocumentBuilderFactory documentFactory ;
	private static DocumentBuilder builder ;
	
	static {
		documentFactory = DocumentBuilderFactory.newInstance();
		try {
			builder = documentFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected InitPoolSqls(String sqlPath,DomFactory factory) throws SAXException, IOException{
		this.factory = factory;
		this.sqlPath = sqlPath;
		Enumeration<URL> e = this.getClass().getClassLoader().getResources(sqlPath);
		Document document = builder.parse(this.getClass().getClassLoader().getResourceAsStream("doms.xml"));
		Node doms = document.getChildNodes().item(0);
		factory.initSqlDom(doms);
		while (e.hasMoreElements()) {
			URL url = (URL) e.nextElement();
			readFile(new File(url.getFile()));
		}
	}
	
	protected void readFile(File f) throws SAXException, IOException {
		if (f.isDirectory()) {
			File[] listSon = f.listFiles();
			for (File file : listSon) {
				readFile(file);
			}
		}else {
			if (!f.getName().endsWith(".xml")) {
				return ;
			}
			Document document = builder.parse(f);
			
			Node configurationNode = document.getChildNodes().item(0);
			
			NodeList list = configurationNode.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				factory.readDom(n.getNodeName(), n, null);
			}
		}
	}
	
}
