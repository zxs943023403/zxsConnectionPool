package ConnectionPool.ConnectionPool.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;

import ConnectionPool.ConnectionPool.anno.Column;

public class PoolUtil {
	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        return pattern.matcher(str).matches(); 
	}
	
	public static Object changeResultType(Object results,String type) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		long start = System.currentTimeMillis();
		List list = new ArrayList<>();
		if (results instanceof JSONArray) {
			Class c = Class.forName(type);
			Field[] fs = c.getDeclaredFields();
			List<Field> annoFs = new ArrayList<Field>();
			long cal = System.currentTimeMillis();
			for (Field f : fs) {
				Column column = f.getAnnotation(Column.class);
				if (null != column) {
					annoFs.add(f);
				}
			}
			System.out.println("anno cost:"+(System.currentTimeMillis() - cal));
			cal = System.currentTimeMillis();
			for (int i = 0; i < ((JSONArray)results).size(); i++) {
				JSONObject obj = (JSONObject) ((JSONArray) results).get(i);
				for (Field f : annoFs) {
					((JSONObject) ((JSONArray) results).get(i)).put(f.getName(), obj.get(f.getAnnotation(Column.class).value()));
				}
			}
			System.out.println("change json cost:"+(System.currentTimeMillis() - cal));
			cal = System.currentTimeMillis();
			list = ((JSONArray) results).toJavaList(c);
			System.out.println("to json cost:"+(System.currentTimeMillis() - cal));
		}
		System.out.println("change type cost:"+(System.currentTimeMillis() - start));
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
