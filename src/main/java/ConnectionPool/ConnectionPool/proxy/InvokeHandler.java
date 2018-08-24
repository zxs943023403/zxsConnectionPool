package ConnectionPool.ConnectionPool.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;

import ConnectionPool.ConnectionPool.anno.VO;
import ConnectionPool.ConnectionPool.mainpool.PoolConfig;
import ConnectionPool.ConnectionPool.util.PoolUtil;
import ConnectionPool.ConnectionPool.util.PrintLog;

public class InvokeHandler implements InvocationHandler {
	private Object obj;
	
	public InvokeHandler(Object obj) {
		this.obj = obj;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		PrintLog.log("class:"+className+";method:"+methodName);
		JSONObject obj = new JSONObject();
		for (Object arg : args) {
			String str = JSON.toJSONString(arg);
			JSONObject jsonobj = JSON.parseObject(str);
			obj.putAll(jsonobj.toJavaObject(Map.class));
		}
		JSONObject newobj = new JSONObject();
		changeObject(obj, "",newobj);
		Object result = PoolConfig.getConfig().poolExec(className, methodName, newobj.toJavaObject(Map.class));
		if ("void".equals(method.getReturnType().getName())) {
			return null;
		}
		if (method.getReturnType() != List.class) {
			result = ((List)result).get(0);
		}
		return result;
	}
	
	public void changeObject(JSONObject map,String key,JSONObject newObj) {
		for (Object k : map.keySet()) {
			Object obj = map.get(k);
			if (obj instanceof JSONObject) {
				changeObject((JSONObject) obj,(StringUtils.isNullOrEmpty(key)?"":key+".")+k,newObj);
			}else {
				newObj.put((StringUtils.isNullOrEmpty(key)?"":key+".")+k, obj);
			}
		}
	}
	
}
