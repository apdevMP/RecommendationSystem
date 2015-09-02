import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DBManager {

	private static final String SERVER_ADDRESS = "localhost";
	private static final int PORT = 27017;
	private static final String DATABASE_NAME = "user_data";
	private static MongoClient client = null;

	private MongoDatabase database;
	
	public DBManager() {

	}

	public void startConnection(){
		if(client == null){
			client = new MongoClient(SERVER_ADDRESS, PORT);
		}
		else{
			System.out.println("Connection alrady started");
		}
	}

	public void closeConnection(){
		client.close();
	}
	
	public void setDatabase(String databaseName){
		
		database = client.getDatabase(databaseName);
	}
	
	public MongoDatabase getDatabase(){
		return database;
	}

	public MongoCollection<Document> getCollectionByName(String collectionName){
		MongoCollection<Document> collection = database.getCollection(collectionName);
		return collection;
	}
	
	public static void main(String[] args) {

		// connection with Mongodb Server
		DBManager manager = new DBManager();
		manager.startConnection();
		
		manager.setDatabase(DATABASE_NAME);

		MongoCollection<Document> collection = manager.getCollectionByName("log");
		System.out.println("Count: " + collection.count());

		Document doc = collection.find().first();
		System.out.println(doc.toJson());

		manager.closeConnection();
	}

}
