package ConnectionPool.ConnectionPool.mainpool.dom.resultmapmodel;

public class ResultMapResult {
	public String type;
	public String properties;
	public String column;
	public String javaType;
	public String jdbcType;
	@Override
	public String toString() {
		return "ResultMapResult [type=" + type + ", properties=" + properties + ", column=" + column + ", javaType="
				+ javaType + ", jdbcType=" + jdbcType + "]";
	}
	
	
	
}
