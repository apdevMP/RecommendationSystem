import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class DBManager
{

	public static void main(String[] args)
	{
		//connection with Mongodb Server
		MongoClient client = new MongoClient("localhost", 27017);
		
		MongoDatabase db = client.getDatabase("user_data");
		
		MongoCollection<Document> collection = db.getCollection("log");
		System.out.println("Count: " + collection.count());
		
		Document doc = collection.find().first();
		System.out.println(doc.toJson());
		
		client.close();
	}

}
