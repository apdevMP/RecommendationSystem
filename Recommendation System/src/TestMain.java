
public class TestMain {
	
	public static void main(String[] args) {

		QueryManager queryManager = new QueryManager();
		int code = queryManager.retrieveRegionCodeByRegionName("Piemonte");
		System.out.println("Piemonte Code: " + code);
		
		queryManager.closeConnection();
	}

}
