import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.FindIterable;


public class TestMain {
	
	public static void main(String[] args) {

		QueryManager queryManager = new QueryManager();
		
		FindIterable<Document> iterable = queryManager.findWatchesByTeachingRole("EEEE_SOST_G");
		for(Document d : iterable){
			System.out.println(d.toJson());
		}
		
		ArrayList<Document> list = Filter.filterByRegion(iterable, "Lazio");
		for(Document d : list){
			System.out.println(d.toJson());
		}
	
		queryManager.closeConnection();
	}

}
