package ConnectionPool.ConnectionPool.util;

import java.util.HashMap;
import java.util.Map;

public class CalText {
	public static String txt = "(aa != null and bb != null) or cc == null";
	
	public static void main(String[] args) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("aa", "bb");
		params.put("bb", "cc");
		params.put("cc", "aa");
		params.put("dd", "a");
		Cal(txt,params);
	}
	private static String[] calData = new String[] {" != "," == "," >= "," <= "," > "," < "};
	
	public static void Cal(String txt,Map<String, Object> params) {
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
		long start = System.currentTimeMillis();
		System.out.println(calNode(todo, params));
		System.out.println("cost:"+(System.currentTimeMillis() - start));
	}
	
	public static Node getHead(Node head) {
		if (head.head == null) {
			return head;
		}
		return getHead(head.head);
	}
	
	public static boolean calNode(Node node,Map<String, Object> params) {
		String msg = node.msg.toString().trim();
		if ("and".equals(msg)) {
			return calNode(node.left,params) && calNode(node.right,params);
		}else if ("or".equals(msg)) {
			return calNode(node.left,params) || calNode(node.right,params);
		}else {
			String[] txts = msg.split(" ");
			Object p = params.get(txts[0]);
			//替换为BSH？？
			/*Interpreter interpreter = new Interpreter();
			Object result = new Object();
			try {
				interpreter.set(txts[0], p);
				result = interpreter.eval(msg);
			} catch (EvalError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if ("true".equals(result) || true == (boolean)result) {
				return true;
			}*/
			if ("null".equals(txts[2])) {
				return "!=".equals(txts[1])?(null != p):(null == p);
			}else {
				switch (txts[1]) {
				case "!=":
					return !txts[2].equals(p);
				case "==":
					return txts[2].equals(p);
				case ">":
					break;
				case "<":
					break;
				case ">=":
					break;
				case "<=":
					break;
				default:
					break;
				}
			}
		}
		return false;
	}
	
	public static boolean isCal(String str) {
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
