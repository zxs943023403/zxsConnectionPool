package ConnectionPool.ConnectionPool.test;

import ConnectionPool.ConnectionPool.anno.Column;

public class TestTable {
	@Column("primarykey")
	public String id;
	public String txt1;
	public String txt2;
	public int in1;
	public int in2;
	@Override
	public String toString() {
		return "TestTable [id=" + id + ", txt1=" + txt1 + ", txt2=" + txt2 + ", in1=" + in1 + ", in2=" + in2 + "]";
	}
	
}
