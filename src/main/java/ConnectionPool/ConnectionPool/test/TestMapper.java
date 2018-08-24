package ConnectionPool.ConnectionPool.test;

import java.util.List;
import java.util.Map;

import ConnectionPool.ConnectionPool.anno.VO;

@VO("ConnectionPool.ConnectionPool.test.vo")
public interface TestMapper {
	public void updateVo(vo v);
	public vo getUser(Map<String, Object> map);
	public vo getUser1(Map<String, Object> map);
	public vo getUser2(Map<String, Object> map);
	public List<vo> getUser3(Map<String, Object> map);
}
