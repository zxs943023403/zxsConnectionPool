package ConnectionPool.ConnectionPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import ConnectionPool.ConnectionPool.mainpool.Pool;
import ConnectionPool.ConnectionPool.mainpool.PoolConfig;
import ConnectionPool.ConnectionPool.proxy.PoolProxy;
import ConnectionPool.ConnectionPool.test.TestMapper;
import ConnectionPool.ConnectionPool.test.TestTable;
import ConnectionPool.ConnectionPool.test.TestTableMapper;
import ConnectionPool.ConnectionPool.test.vo;

/**
 * Hello world!
 *
 */
public class App {
	
	private static PoolConfig config;
	static {
		config = PoolConfig.getConfig();
	}
	
    public static void main( String[] args ){
//    	chaxunsql();
//    	xiugaisql();
    	chaxunmapper2with10000();
//    	updatesmap();
    }
    
    public static void updatesmap() {
    	try {
    		TestTableMapper mapper = PoolProxy.getMapper(TestTableMapper.class);
    		TestTable vo = new TestTable();
    		vo.id = "2418d1bd-9ea7-44aa-8298-7d27558e82bd";
    		vo.txt1 = "asdas";
    		vo.txt2 = "gthtr";
    		vo.in1 = 432;
    		vo.in2 = 54;
    		List list = new ArrayList();
    		list.add("2418d1bd-9ea7-44aa-8298-7d27558e82bd");
    		list.add("3f87497e-c6bf-4702-81f1-4c9e6e92cc99");
    		list.add("43a3056b-4ee8-415e-8d2a-dcb941aa0435");
    		Map<String, Object> args = new HashMap<String, Object>();
    		args.put("vo", vo);
    		args.put("list", list);
    		long start = System.currentTimeMillis();
    		mapper.updates(args);
    		System.out.println("all cost:"+(System.currentTimeMillis() - start));
    	} catch (InstantiationException | IllegalAccessException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
    
    public static void updatemap() {
    	try {
    		TestTableMapper mapper = PoolProxy.getMapper(TestTableMapper.class);
			TestTable vo = new TestTable();
			vo.id = "2418d1bd-9ea7-44aa-8298-7d27558e82bd";
			vo.txt1 = "votxt1";
			vo.txt2 = "votxt2";
			vo.in1 = 11;
			vo.in2 = 22;
			long start = System.currentTimeMillis();
			mapper.update(vo);
    		System.out.println("all cost:"+(System.currentTimeMillis() - start));
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void insert(){
    	try {
    		String sql = "insert into test_table(primarykey,txt1,txt2,in1,in2) values(?,?,?,?,?)";
    		Object[] args = new Object[5];
    		args[0] = UUID.randomUUID().toString();
    		args[1] = "t1";
    		args[2] = "t2";
    		args[3] = 1;
    		args[4] = 2;
			Object results = PoolConfig.getConfig().poolExec(sql,Pool.EXEC_TYPE.TYPE_UPDATE,args);
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
    
    public static void xiugaisql() {
    	vo v = new vo();
    	v.code="123";
    	v.iden="456";
    	v.id=null;
    	System.out.println(JSON.toJSONString(v));
    }
    
    public static void chaxunmapper2with10000() {
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	ScheduledExecutorService pool = Executors.newScheduledThreadPool(50);
    	for (int i = 0; i < 1; i++) {
    		final int in = i;
    		pool.schedule(()->{
    			try {
    				TestMapper mapper = PoolProxy.getMapper(TestMapper.class);
    				Map<String, Object> map = new HashMap<String, Object>();
    				map.put("cGuid", "109230319479134175");
    				map.put("tname", "aos_rms_user");
    				long start = System.currentTimeMillis();
    				int ran = new Random().nextInt();
//    				int ran = 1;
    				switch (ran%4) {
					case 0:
						System.out.println(config.poolExec("mysqlDB2","ConnectionPool.ConnectionPool.test.TestMapper", "getUser1",map));
						break;
					case 1:
						System.out.println(mapper.getUser1(map));
						break;
					case 2:
						System.out.println(mapper.getUser2(map));
						break;
					case 3:
						map.remove("cGuid");
						map.put("cPwdType", "1");
						List list = mapper.getUser3(map);
						for (Object object : list) {
							System.out.println(object);
						}
						break;
					default:
						break;
					}
    	    		System.out.println(in+"all cost:"+(System.currentTimeMillis() - start));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}, 0, TimeUnit.MILLISECONDS);
		}
    	pool.shutdown();
    }
    
    public static void chaxunmapper2() {
    	try {
    		TestMapper mapper = PoolProxy.getMapper(TestMapper.class);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cGuid", "109230319479134175");
			map.put("tname", "aos_rms_user");
			long start = System.currentTimeMillis();
    		System.out.println(mapper.getUser2(map));
    		System.out.println("all cost:"+(System.currentTimeMillis() - start));
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void chaxunsql() {
    	try {
			Object results;
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cGuid", "109230319479134175");
				map.put("tname", "aos_rms_user");
				long start =System.currentTimeMillis();
				results = config.poolExec("mysqlDB2","connpool.test.selectmap", "getUser1",map);
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
			Object results = PoolConfig.getConfig().poolExec(sql,Pool.EXEC_TYPE.TYPE_QUERY);
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
    				PoolConfig.getConfig().poolExec(sql,Pool.EXEC_TYPE.TYPE_UPDATE,new Random().nextInt());
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
