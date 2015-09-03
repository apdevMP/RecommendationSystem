import org.bson.Document;


public class QueryManager {
	
	private DBManager manager;
	
	public QueryManager(){
		manager = DBManager.getIstance();
	}
	
	public int retrieveRegionCodeByRegionName(String regionName){
		int code=0;
		Document document = manager.retrieveRegionCodeByName(regionName);
		code = document.getInteger("region_id");
		
		return code;
	}
	
	public void closeConnection(){
		manager.closeConnection();
	}
}
