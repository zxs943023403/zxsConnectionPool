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
		System.out.println(sql);
	}
	
	/*public static void main(String[] args) {
		String txt = "asd#{aa.bb}123";
		Map<String, String> map = new HashMap<>();
		map.put("aa.bb", "123");
		System.out.println(muiltChangeLang(txt, map));
	}*/
	
	//大括号正则表达式{xx}
	private static String pattern = "(?<=#\\{)(.+?)(?=\\})";

	/**
	 * context多语言文字
	 * 多语言code-value对应map
	 * @param context
	 * @param values
	 * @return
	 */
	private String changeSqlParams(String context, Map<String, Object> values) {
		Pattern p = Pattern.compile(pattern);
		StringBuffer svg = new StringBuffer(context);
		if (sqlHasParams(svg.toString(), pattern) && values != null) {
			Matcher m = p.matcher(svg);
			List<String> ls = new ArrayList<String>();
			while (m.find()) {
				ls.add(m.group());
			}
			args = new Object[ls.size()];
			int index = 0;
			for (String string : ls) {
				svg = new StringBuffer(svg.toString().replaceAll("#\\{" + string + "\\}", " ? "));
				args[index++] = values.get(string);
			}
		}
		return svg.toString();
	}

	private boolean sqlHasParams(String context, String pattern) {
		if (context == null || "".equals(context)) {
			return false;
		}
		return Pattern.compile(pattern).matcher(context).find();
	}

}
