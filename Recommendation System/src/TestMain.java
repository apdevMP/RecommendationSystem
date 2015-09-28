import java.sql.SQLException;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.FindIterable;

public class TestMain {

	private final static String REGION = "Lazio";
	
	public static void main(String[] args) {

		QueryManager queryManager = new QueryManager();

		FindIterable<Document> itWatches = queryManager
				.findWatchesByTeachingRole("EEEE_SOST_G");
		FindIterable<Document> itLogs = queryManager
				.findLogsByTeachingRole("EEEE_SOST_G");
		for (Document d : itLogs) {
			System.out.println(d.toJson());
		}

		/*
		 * ArrayList<String> list = queryManager.getLogAction(); for(String s :
		 * list){ System.out.println(s); }
		 */
		// ueryManager.getLogsByAction("webapi_municipality_aggregates");

		ArrayList<Document> listWatches = Filter.filterWatchesByRegion(
				itWatches, REGION);
		for (Document d : listWatches) {
			System.out.println(d.toJson());
		}

		ArrayList<Document> listLogs = Filter.filterLogsByRegion(itLogs,
				REGION);
		for (Document d : listLogs) {
			System.out.println(d.toJson());
		}
		System.out.println("--------->SIZE:" + listLogs.size()
				+ "<---------------");
		/*
		 * GraphManager gManager = new GraphManager();
		 * gManager.connectToGraph("neo4j", "ilaria10"); try {
		 * gManager.queryMunicipalityName("H194");
		 * //gManager.queryMostQuotedSchoolInProvince("AG");
		 * //gManager.queryMostQuotedSchoolInRegion(258);
		 * //gManager.queryMostQuotedSchoolInMunicipality("H194"); } catch
		 * (SQLException e) {
		 * 
		 * e.printStackTrace(); }
		 */

		UtilityMatrix um = new UtilityMatrix();
		um.fillMatrixWithWatches(listWatches, 45);
		um.printUtilityMatrix();

		UtilityMatrix um2 = new UtilityMatrix();
		um2.fillMatrixWithLogs(listLogs, 45);
		um2.printUtilityMatrix();
		
		queryManager.closeConnection();

	}

}
