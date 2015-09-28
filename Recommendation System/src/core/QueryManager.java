package core;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;

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

	/**
	 * 
	 * @param score
	 */
	public void findWatchesByScore(int score) {
		FindIterable<Document> iterable = manager.findWatchesByScore(score);
		for (Document doc : iterable) {
			System.out.println(doc.toJson());
		}
	}

	/**
	 * 
	 * @param teachingRole
	 * @return
	 */
	public FindIterable<Document> findWatchesByTeachingRole(String teachingRole) {
		FindIterable<Document> iterable = manager
				.findWatchesByTeachingRole(teachingRole);
		return iterable;
	}

	/**
	 * Recupera la lista di azioni eseguite dagli utenti,filtrate per la materia insegnata
	 * @param teachingRole
	 * @return
	 */
	public FindIterable<Document> findLogsByTeachingRole(String teachingRole) {
		FindIterable<Document> iterable = manager
				.findLogsByTeachingRole(teachingRole);

		return iterable;
	}

	/**
	 * 
	 * @param region
	 * @param province
	 * @return
	 */
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

	/**
	 * 
	 * @param region
	 * @param municipality_code
	 * @return
	 */
	public int isMunicipalityInRegion(String region, String municipality_code) {
		GraphManager gManager = new GraphManager();
		gManager.connectToGraph("neo4j", "vanessa");
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

	/**
	 * 
	 * @param region
	 * @param school
	 * @return
	 */
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

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getLogAction() {
		ArrayList<String> actionList = new ArrayList<String>();
		FindIterable<Document> iterable = manager.findLogs();

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("azioni.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		@SuppressWarnings("resource")
		PrintStream write = new PrintStream(fos);

		for (Document doc : iterable) {
			String action = doc.getString("action");

			if (!actionList.contains(action)) {
				actionList.add(action);
				write.println(doc.toJson());
			}
		}
		return actionList;

	}

	public void closeConnection() {
		manager.closeConnection();
	}

	public void getLogsByAction(String stringAction) {
		FindIterable<Document> iterable = manager
				.findLogsByAction(stringAction);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("prova.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PrintStream write = new PrintStream(fos);

		for (Document doc : iterable) {

			write.println(doc.toJson());

		}

	}

}
