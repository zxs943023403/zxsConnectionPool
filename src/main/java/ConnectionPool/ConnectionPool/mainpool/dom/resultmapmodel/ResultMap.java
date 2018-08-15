package ConnectionPool.ConnectionPool.mainpool.dom.resultmapmodel;

import java.util.ArrayList;
import java.util.List;

public class ResultMap {
	public String mapId;
	public String mapType;
	public ResultMapResult id;
	public List<ResultMapResult> results;
	
	public ResultMap() {
		results = new ArrayList<ResultMapResult>();
	}

	@Override
	public String toString() {
		return "ResultMap [mapiD=" + mapId + ", mapType=" + mapType + ", id=" + id + ", results=" + results + "]";
	}
	
	
	
}
