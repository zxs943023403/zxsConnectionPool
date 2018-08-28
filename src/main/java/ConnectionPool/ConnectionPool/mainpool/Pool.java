package ConnectionPool.ConnectionPool.mainpool;

import java.io.IOException;
import java.security.interfaces.RSAKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.w3c.dom.Node;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import ConnectionPool.ConnectionPool.mainpool.dom.DataSource;
import ConnectionPool.ConnectionPool.mainpool.dom.DomFactory;
import ConnectionPool.ConnectionPool.mainpool.dom.models.SelectModel;
import ConnectionPool.ConnectionPool.mainpool.dom.models.Sqls;
import ConnectionPool.ConnectionPool.util.PoolUtil;
import ConnectionPool.ConnectionPool.util.PrintLog;

public class Pool {
	
	private LinkedList<Conn> conns = new LinkedList<Pool.Conn>();
	private static ScheduledExecutorService executor;
	private static DomFactory factory;
	
	private String maxConnNum;
	private String DbDriver;
	private String ConnUrl;
	private String DbUserName;
	private String DbPassword;
	
	public static enum EXEC_TYPE{
		TYPE_QUERY,
		TYPE_UPDATE
	}
	
	protected Pool(DataSource source) {
		try {
			factory = DomFactory.getFactory();
			maxConnNum = source.get("maxConnNum", "20");
			DbDriver = source.get("driver");
			ConnUrl = source.get("url");
			DbUserName = source.get("username");
			DbPassword = source.get("password");
			Class.forName(DbDriver);
			initPool();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void initPool() throws Exception {
		if (!PoolUtil.isNumber(maxConnNum)) {
			throw new Exception("最大連接數必須是數字！！");
		}
		int num = Integer.valueOf(maxConnNum);
		executor = Executors.newScheduledThreadPool(num);
		for (int i = 0; i < num; i++) {
			conns.add(new Conn(ConnUrl, DbUserName, DbPassword, i));
		}
		new InitPoolSqls("sqls",factory);
	}
	
	private synchronized void returnConn(Conn c) {
		if (null != c) {
			conns.offer(c);
		}
	}
	
	private synchronized Conn getConn() {
		return conns.poll();
	}
	
	public Object exec(String namespace,String id,Map<String, Object> args) throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		long start = System.currentTimeMillis();
		Node node = factory.getNode(namespace, id);
		Sqls sql = (Sqls) factory.readDom(node.getNodeName(),node,args);
		start = System.currentTimeMillis();
		Object result = exec(sql.sql, "select".equals(node.getNodeName())?EXEC_TYPE.TYPE_QUERY:EXEC_TYPE.TYPE_UPDATE, sql.args);
		System.out.println("query cost:"+(System.currentTimeMillis() - start));
		return PoolUtil.changeResultType(result, sql.resultType);
	}
	
	public Object exec(String sql,EXEC_TYPE type,Object ...args) throws InterruptedException {
//		Conn conn = getConn();
		RunExec e = new RunExec(sql,type,args);
		executor.schedule(e, 0, TimeUnit.MILLISECONDS);
		return e.getResult();
	}
	
	public static void stop() {
		while (true) {
			if (executor.isTerminated()) {
				executor.shutdown();
			}
		}
	}
	
	private class RunExec implements Runnable{
		private Object result;
		private Conn conn;
		private String sql;
		private EXEC_TYPE type;
		private Object[] args;
		
		public Object getResult() throws InterruptedException {
			while (result == null) {
				Thread.sleep(1);
			}
			return result;
		}
		
		public RunExec(String sql,EXEC_TYPE type,Object ...args) {
			this.sql = sql;
			this.type = type;
			this.args = args;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while (true) {
					try {
						conn = getConn();
						if (conn != null) {
							break;
						}
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				result = conn.exec(sql,type,args);
				returnConn(conn);
				conn.notify();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private class ConnHeartBeat extends TimerTask{
		private Conn conn;
		private String sql = "select 1";
		
		public ConnHeartBeat(Conn conn) {
			this.conn = conn;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				PrintLog.log("发送心跳请求！");
				conn.exec(sql, EXEC_TYPE.TYPE_QUERY);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private class Conn{
		private int index;
		private Connection connection;
		private Timer timer;
		
		public Conn(String url,String id,String pwd,int index){
			try {
				connection = DriverManager.getConnection(url,id,pwd);
				this.index = index;
				timer = new Timer();
				initTimer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public synchronized Object exec(String sql,EXEC_TYPE type,Object ...args) throws SQLException {
			dropTimer();
			Object result = null;
			sql = checkSql(sql);
			PrintLog.log("sql:"+sql);
			switch (type) {
			case TYPE_UPDATE:
				result = update(sql,args);
				break;
			case TYPE_QUERY:
				result = query(sql,args);
				break;
			default:
				break;
			}
			initTimer();
			return result;
		}
		
		private synchronized boolean update(String sql,Object ...args) throws SQLException {
			PreparedStatement pst = connection.prepareStatement(sql);
			String arg = "[";
			for (int i = 0; i < args.length; i++) {
				pst.setObject(i+1, args[i]);
				arg += args[i]+",";
			}
			arg = arg.substring(0,arg.length() - 1) + "]";
			PrintLog.log("args:"+arg);
			return pst.execute();
		}
		
		private synchronized Object query(String sql,Object ...args) throws SQLException {
			PreparedStatement pst = connection.prepareStatement(sql);
			if (null != args) {
				String arg = "[";
				for (int i = 0; i < args.length; i++) {
					pst.setObject(i+1, args[i]);
					arg += args[i]+",";
				}
				arg = arg.substring(0,arg.length() - 1) + "]";
				PrintLog.log("args:"+arg);
			}
			ResultSet set = pst.executeQuery();
			JSONArray array = new JSONArray();
			ResultSetMetaData data = set.getMetaData();
			List<String> cnames = new ArrayList<String>();
			for (int i = 0; i < data.getColumnCount(); i++) {
				cnames.add(data.getColumnName(i+1));
			}
			while (set.next()) {
				JSONObject obj = new JSONObject();
				for (String c : cnames) {
					obj.put(c, set.getObject(c));
				}
				array.add(obj);
			}
			return array;
		}
		
		private String checkSql(String sql) {
			
			 return sql;
		}
		
		private void initTimer() {
			timer = new Timer();
			timer.schedule(new ConnHeartBeat(this), 600000,600000);
		}
		
		private void dropTimer() {
			timer.cancel();
			timer = null;
		}
		
	}
	
}
