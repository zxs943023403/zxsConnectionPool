package ConnectionPool.ConnectionPool.test;

import java.util.List;
import java.util.Map;

public interface TestTableMapper {
	public void insert(TestTable vo);
	public void update(TestTable vo);
	public void updates(Map<String, Object> args);
}
