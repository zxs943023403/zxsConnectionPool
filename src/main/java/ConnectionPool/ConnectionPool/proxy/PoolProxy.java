package ConnectionPool.ConnectionPool.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

public class PoolProxy {
	
	private ConcurrentHashMap<String, Class> mappers = new ConcurrentHashMap<String, Class>();
	
	public static synchronized PoolProxy getProxyFactory() {
		return proxy;
	}
	
	public <T> T getProxy(Class<T> c) throws InstantiationException, IllegalAccessException {
		InvocationHandler handler = new InvokeHandler(c);
		Class[] cs = new Class[1];
		cs[0] = c;
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), cs, handler);
	}
	
	public void newMapper(String className) {
		try {
			Class c = Class.forName(className);
			mappers.put(className, c);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static PoolProxy proxy = new PoolProxy();
	private PoolProxy() {}
}
