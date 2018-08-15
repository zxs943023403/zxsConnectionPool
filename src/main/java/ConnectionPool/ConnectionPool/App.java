package ConnectionPool.ConnectionPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import ConnectionPool.ConnectionPool.mainpool.Pool;
import ConnectionPool.ConnectionPool.test.vo;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ){
    	chaxunsql();
    }
    
    public static void chaxunsql() {
    	try {
			Object results;
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cGuid", "109230319479134175");
				long start =System.currentTimeMillis();
				results = Pool.getInstance().exec("connpool.test.selectmap", "getUser1",map);
				System.out.println("all cost:"+(System.currentTimeMillis() - start));
				if (results instanceof JSONArray) {
					System.out.println("jsonobject?");
				}else if (results instanceof List) {
					for (Object obj : (List)results) {
						System.out.println("list:"+obj);
					}
				}else {
					System.out.println(results);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void chaxun(){
    	try {
    		String sql = "select * from aos_rms_user";
			Object results = Pool.getInstance().exec(sql,Pool.EXEC_TYPE.TYPE_QUERY);
			if (results instanceof JSONArray) {
				for (int i = 0; i < ((JSONArray)results).size(); i++) {
					JSONObject obj = (JSONObject) ((JSONArray) results).get(i);
					System.out.println(obj);
					vo v = obj.toJavaObject(vo.class);
					System.out.println(v.toString());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void bingfaceshi(){
    	ScheduledExecutorService pool = Executors.newScheduledThreadPool(50);
    	for (int i = 0; i < 1; i++) {
    		pool.schedule(()->{
    			try {
    				String sql = "insert into indexs values(?)";
					Pool.getInstance().exec(sql,Pool.EXEC_TYPE.TYPE_UPDATE,new Random().nextInt());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}, 0, TimeUnit.MILLISECONDS);
		}
    	pool.shutdown();
    	Pool.stop();
    }
    
}
