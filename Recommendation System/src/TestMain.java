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
		}
	
		UtilityMatrix um =new UtilityMatrix();
		um.fillMatrixWithWatches(list, 45);
		um.printUtilityMatrix();
		
		queryManager.closeConnection();
		
		
	}

}
