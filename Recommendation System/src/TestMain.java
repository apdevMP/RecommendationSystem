
public class TestMain {
	
	public static void main(String[] args) {

		QueryManager queryManager = new QueryManager();
		
		System.out.println("Piemonte Code: " + queryManager.retrieveRegionCodeByRegionName("Piemonte"));
		System.out.println("TO Code: " + queryManager.retrieveProvinceCodeByProvinceName("Torino"));
		System.out.println("Agli� Code: " + queryManager.retrieveCityCodeByCityName("Agli�"));
		queryManager.findWatchByScore(42);		
		queryManager.closeConnection();
	}

}
