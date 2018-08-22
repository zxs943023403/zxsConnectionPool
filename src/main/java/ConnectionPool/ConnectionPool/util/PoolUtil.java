package ConnectionPool.ConnectionPool.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;

import ConnectionPool.ConnectionPool.anno.Column;

public class PoolUtil {
	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        return pattern.matcher(str).matches(); 
	}
	
	public static Map<String, String> readNodeAttrs(org.w3c.dom.Node node){
		Map<String, String> attrs = new HashMap<String, String>();
		if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
			return null;
		}
		NamedNodeMap nodeAttr = node.getAttributes();
		for (int i = 0; i < nodeAttr.getLength(); i++) {
			org.w3c.dom.Node attr = nodeAttr.item(i);
			if (null == attr) {
				System.out.println("null node attr at"+node.getNodeName()+":i="+i);
			}
			attrs.put(nodeAttr.item(i).getNodeName(), nodeAttr.item(i).getTextContent());
		}
		return attrs;
	}
	
	public static Object changeResultType(Object results,String type) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		List list = new ArrayList<>();
		if (results instanceof JSONArray) {
			Class c = Class.forName(type);
			Field[] fs = c.getDeclaredFields();
			List<Field> annoFs = new ArrayList<Field>();
			for (Field f : fs) {
				Column column = f.getAnnotation(Column.class);
				if (null != column) {
					annoFs.add(f);
				}
			}
			for (int i = 0; i < ((JSONArray)results).size(); i++) {
				JSONObject obj = (JSONObject) ((JSONArray) results).get(i);
				for (Field f : annoFs) {
					((JSONObject) ((JSONArray) results).get(i)).put(f.getName(), obj.get(f.getAnnotation(Column.class).value()));
				}
			}
			list = ((JSONArray) results).toJavaList(c);
		}
		return list.size()==1?list.get(0):list;
	}
	
	private static String[] calData = new String[] {" != "," == "," >= "," <= "," > "," < "};
	
	public static boolean Cal(String txt,Map<String, Object> params) {
		txt = txt.trim();
		Node todo = new Node();
		StringBuffer tmpMsg = new StringBuffer();
		for (int i = 0; i < txt.toCharArray().length; i++) {
			char c = txt.toCharArray()[i];
			Node tmp = new Node();
			switch (c) {
			case '(':
				todo.left = tmp;
				tmp.head = todo;
				break;
			case ' ':
				if (isCal(tmpMsg.toString())) {
					todo.msg.append(tmpMsg.toString());
					tmpMsg = new StringBuffer("");
				}else if (" and".equals(tmpMsg.toString()) || " or".equals(tmpMsg.toString())) {
					tmp.msg.append(tmpMsg.toString());
					tmpMsg = new StringBuffer("");
					tmp.left = todo;
					todo.head = tmp;
					todo = new Node();
					tmp.right = todo;
					todo.head = tmp;
				}
				tmpMsg.append(c);
				break;
			case ')' :
				todo.msg.append(tmpMsg.toString());
				todo = todo.head;
				tmpMsg = new StringBuffer("");
				break;
			default:
				tmpMsg.append(c);
				break;
			}
			if (i == txt.toCharArray().length - 1) {
				todo.msg.append(tmpMsg.toString());
				tmpMsg = new StringBuffer("");
			}
		}
		todo = getHead(todo);
		return calNode(todo, params);
	}
	
	private static Node getHead(Node head) {
		if (head.head == null) {
			return head;
		}
		return getHead(head.head);
	}
	
	private static boolean calNode(Node node,Map<String, Object> params) {
		String msg = node.msg.toString().trim();
		if (StringUtils.isNullOrEmpty(msg)) {
			return true;
		}
		if ("and".equals(msg)) {
			return calNode(node.left,params) && calNode(node.right,params);
		}else if ("or".equals(msg)) {
			return calNode(node.left,params) || calNode(node.right,params);
		}else {
			if (null == params) {
				return false;
			}
			String[] txts = msg.split(" ");
			Object p = params.get(txts[0]);
			//替换为BSH？？
			if ("null".equals(txts[2])) {
				return "!=".equals(txts[1])?(null != p):(null == p);
			}else {
				switch (txts[1]) {
				case "!=":
					return !txts[2].equals(p);
				case "==":
					return txts[2].equals(p);
				case ">":
					if (isNumber(p.toString()) && isNumber(txts[2])) {
						return Long.valueOf(p.toString()) > Long.valueOf(txts[2]);
					}
				case "<":
					if (isNumber(p.toString()) && isNumber(txts[2])) {
						return Long.valueOf(p.toString()) < Long.valueOf(txts[2]);
					}
				case ">=":
					if (isNumber(p.toString()) && isNumber(txts[2])) {
						return Long.valueOf(p.toString()) >= Long.valueOf(txts[2]);
					}
				case "<=":
					if (isNumber(p.toString()) && isNumber(txts[2])) {
						return Long.valueOf(p.toString()) <= Long.valueOf(txts[2]);
					}
				default:
					break;
				}
			}
		}
		return false;
	}
	
	private static boolean isCal(String str) {
		for (String s : calData) {
			if (str.indexOf(s) != -1 && !str.endsWith(s)) {
				return true;
			}
		}
		return false;
	}
	
	public static String replaceAttr(String context,String pattern,Map properties) {
		Properties ps = new Properties();
		ps.putAll(properties);
		return replaceAttr(context, pattern, ps);
	}
	
	public static String replaceAttr(String context,String pattern,Properties properties) {
		Pattern p = Pattern.compile(pattern);
		StringBuffer sql = new StringBuffer(context);
		if (hasParams(context, pattern) && null != properties ) {
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

	public static boolean hasParams(String context, String pattern) {
		if (context == null || "".equals(context)) {
			return false;
		}
		return Pattern.compile(pattern).matcher(context).find();
	}
	
	private static class Node{
		public StringBuffer msg = new StringBuffer();
		public Node left;
		public Node right;
		public Node head;
		@Override
		public String toString() {
			return msg.toString();
		}
	}
	
}
