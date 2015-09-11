import java.sql.SQLException;

import org.bson.Document;

import com.mongodb.client.FindIterable;

public class QueryManager {

	private DBManager manager;

	public QueryManager() {
		manager = DBManager.getIstance();
	}

	public int retrieveRegionCodeByRegionName(String regionName) {
		int code = 0;
		Document document = manager.retrieveRegionCodeByName(regionName);
		code = document.getInteger("region_id");

		return code;
	}

	public int retrieveProvinceCodeByProvinceName(String provinceName) {
		int code = 0;
		Document document = manager.retrieveProvinceCodeByName(provinceName);
		code = document.getInteger("province_id");

		return code;
	}

	public int retrieveCityCodeByCityName(String cityName) {
		int code = 0;
		Document document = manager.retrieveCityCodeByName(cityName);
		code = document.getInteger("municipality_id");

		return code;
	}

	public void findWatchesByScore(int score) {
		FindIterable<Document> iterable = manager.findWatchesByScore(score);
		for (Document doc : iterable) {
			System.out.println(doc.toJson());
		}
	}

	public FindIterable<Document> findWatchesByTeachingRole(String teachingRole) {
		FindIterable<Document> iterable = manager
				.findWatchesByTeachingRole(teachingRole);
		return iterable;
	}

	public FindIterable<Document> findLogsByTeachingRole(String teachingRole) {
		FindIterable<Document> iterable = manager
				.findLogsByTeachingRole(teachingRole);

		for (Document doc : iterable) {
			System.out.println(doc.toJson());
		}
		return iterable;
	}

	public int isProvinceInRegion(String region, String province) {

		Document doc = manager.findDocWithProvince(province);
		if (doc == null)
			return 1;
		String documentRegion = doc.getString("region_name");
		if (documentRegion.equals(region))
			return 0;
		else
			return 1;
	}

	// TODO sicuramente da modificare: il codice lo si trova all'interno dei
	// grafi
	public int isMunicipalityInRegion(String region, String municipality_code) {
		GraphManager gManager = new GraphManager();
		gManager.connectToGraph("neo4j", "ilaria10");
		String municipality = null;
		try {
			municipality = gManager.queryMunicipalityName(municipality_code);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (municipality == null)
			return 1;

		Document doc = manager.findDocWithMunicpality(municipality);
		if (doc == null)
			return 1;
		
		String documentRegion = doc.getString("region_name");
		if (documentRegion.equals(region))
			return 0;
		else
			return 1;
	}

	public int isSchoolInRegion(String region, String school) {
		Document doc = manager.findDocWithSchool(school);
		if (doc == null)
			return 1;
		String documentRegion = doc.getString("regionName");
		if (documentRegion.equals(region))
			return 0;
		else
			return 1;
	}

	public void closeConnection() {
		manager.closeConnection();
	}

}
