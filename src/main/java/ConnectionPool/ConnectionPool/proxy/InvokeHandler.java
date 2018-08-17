package ConnectionPool.ConnectionPool.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import ConnectionPool.ConnectionPool.mainpool.PoolConfig;

public class InvokeHandler implements InvocationHandler {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Object obj;
	
	public InvokeHandler(Object obj) {
		this.obj = obj;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		System.out.println("["+sdf.format(new Date())+"]class:"+className+";method:"+methodName);
		JSONObject obj = new JSONObject();
		for (Object arg : args) {
			String str = JSONObject.toJSONString(arg);
			JSONObject jsonobj = JSONObject.parseObject(str);
			obj.putAll(jsonobj.toJavaObject(Map.class));
		}
		return PoolConfig.getConfig().poolExec(className, methodName, obj.toJavaObject(Map.class));
	}
	
}
