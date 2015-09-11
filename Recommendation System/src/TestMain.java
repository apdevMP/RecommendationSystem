import java.sql.SQLException;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.FindIterable;


public class TestMain {
	
	public static void main(String[] args) {

		QueryManager queryManager = new QueryManager();
		
		FindIterable<Document> iterable = queryManager.findWatchesByTeachingRole("EEEE_SOST_G");
//		//FindIterable<Document> it = queryManager.findLogsByTeachingRole("");
		for(Document d : iterable){
			System.out.println(d.toJson());
		}
		
		ArrayList<Document> list = Filter.filterWatchesByRegion(iterable, "Sicilia");
		for(Document d : list){
			System.out.println(d.toJson());
		GraphManager gManager = new GraphManager();
		gManager.connectToGraph("neo4j", "vanessa");
		try
		{
			//gManager.queryMunicipalityName("H194");
			//gManager.queryMostQuotedSchoolInProvince("AG");
			//gManager.queryMostQuotedSchoolInRegion(258);
			gManager.queryMostQuotedSchoolInMunicipality("H194");
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		UtilityMatrix um =new UtilityMatrix();
		um.fillMatrixWithWatches(list, 45);
		um.printUtilityMatrix();
		
		queryManager.closeConnection();
		
		
	}

}
