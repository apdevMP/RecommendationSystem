import java.sql.SQLException;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.FindIterable;


public class TestMain {
	
	public static void main(String[] args) {

//		QueryManager queryManager = new QueryManager();
//		
//		FindIterable<Document> iterable = queryManager.findWatchesByTeachingRole("A345");
//		//FindIterable<Document> it = queryManager.findLogsByTeachingRole("EEEE_SOST_G");
//		for(Document d : iterable){
//			System.out.println(d.toJson());
//		}
//		
//		ArrayList<Document> list = Filter.filterWatchesByRegion(iterable, "Piemonte");
//		for(Document d : list){
//			System.out.println(d.toJson());
//		}
//	
//		UtilityMatrix um =new UtilityMatrix();
//		um.fillMatrixWithWatches(list, 45);
//		um.printUtilityMatrix();
//		
//		queryManager.closeConnection();
		
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
		
	}

}
