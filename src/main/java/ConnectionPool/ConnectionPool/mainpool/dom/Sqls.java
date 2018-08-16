package ConnectionPool.ConnectionPool.mainpool.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sqls {
	public String sql;
	public String resultType;
	public String paramType;
	public Object[] args;
	
	public void sqlParams(Map<String, Object> map) {
		sql = changeSqlParams(sql, map);
		sql = changeSqlStrs(sql, map);
		System.out.println(sql);
	}
	
	public static void main(String[] args) {
		String txt = "asd${aa.bb}123";
		Map<String, Object> map = new HashMap<>();
		map.put("aa.bb", "123");
		System.out.println(new Sqls().changeSqlStrs(txt, map));
	}
	
	//大括号正则表达式#{xx}
	private static String pattern = "(?<=#\\{)(.+?)(?=\\})";
	//大括号正则表达式${xx}
	private static String strPattern = "(?<=\\$\\{)(.+?)(?=\\})";

	/**
	 * context多语言文字
	 * 多语言code-value对应map
	 * @param context
	 * @param values
	 * @return
	 */
	private String changeSqlParams(String context, Map<String, Object> values) {
		Pattern p = Pattern.compile(pattern);
		StringBuffer sql = new StringBuffer(context);
		if (sqlHasParams(sql.toString(), pattern) && values != null) {
			Matcher m = p.matcher(sql);
			List<String> ls = new ArrayList<String>();
			while (m.find()) {
				ls.add(m.group());
			}
			args = new Object[ls.size()];
			int index = 0;
			for (String string : ls) {
				sql = new StringBuffer(sql.toString().replaceAll("#\\{" + string + "\\}", " ? "));
				args[index++] = values.get(string);
			}
		}
		return sql.toString();
	}
	
	private String changeSqlStrs(String context, Map<String, Object> values) {
		Pattern p = Pattern.compile(strPattern);
		StringBuffer sql = new StringBuffer(context);
		if (sqlHasParams(context, strPattern) && null != values ) {
			Matcher m = p.matcher(sql);
			List<String> ls = new ArrayList<String>();
			while (m.find()) {
				ls.add(m.group());
			}
			for (String string : ls) {
				sql = new StringBuffer(sql.toString().replaceAll("\\$\\{" + string + "\\}", values.get(string)+""));
			}
		}
		return sql.toString();
	}

	private boolean sqlHasParams(String context, String pattern) {
		if (context == null || "".equals(context)) {
			return false;
		}
		return Pattern.compile(pattern).matcher(context).find();
	}

}
