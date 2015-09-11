import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DBManager {

	private static final String SERVER_ADDRESS = "localhost";
	private static final int PORT = 27017;
	private static final String DATABASE_NAME = "user_data";
	private static final String LOG_COLLECTION = "log";
	private static final String MUNICIPALITIES_COLLECTION = "municipalities";
	private static final String SCHOOL_COLLECTION = "school";
	private static final String WATCHES_COLLECTION = "watch";

	private static MongoClient client = null;

	private MongoDatabase database;
	private static DBManager manager = null;

	private DBManager() {
		startConnection();
		setDatabase();
	}

	public static DBManager getIstance() {

		if (manager == null)
			manager = new DBManager();
		return manager;

	}

	private void startConnection() {
		if (client == null) {
				client = new MongoClient(SERVER_ADDRESS, PORT);
			
		} else {
			System.out.println("Connection alrady started");
		}
	}

	public void closeConnection() {
		client.close();
		client = null;
	}

	public void setDatabase() {

		database = client.getDatabase(DATABASE_NAME);
	}

	public MongoDatabase getDatabase() {
		return database;
	}

	public MongoCollection<Document> getCollectionByName(String collectionName) {
		MongoCollection<Document> collection = database
				.getCollection(collectionName);
		return collection;
	}

	public Document retrieveRegionCodeByName(String regionName) {

		MongoCollection<Document> collection = getCollectionByName(MUNICIPALITIES_COLLECTION);
		Document doc = collection.find(new Document("region_name", regionName))
				.first();
		return doc;
	}

	public Document retrieveProvinceCodeByName(String provinceName) {

		MongoCollection<Document> collection = getCollectionByName(MUNICIPALITIES_COLLECTION);
		Document doc = collection.find(
				new Document("province_name", provinceName)).first();
		return doc;
	}

	public Document retrieveCityCodeByName(String cityName) {

		MongoCollection<Document> collection = getCollectionByName(MUNICIPALITIES_COLLECTION);
		Document doc = collection.find(
				new Document("municipality_name", cityName)).first();
		return doc;
	}

	public FindIterable<Document> findWatchesByScore(int score) {

		MongoCollection<Document> collection = getCollectionByName(WATCHES_COLLECTION);
		FindIterable<Document> iterable = collection.find(new Document(
				"lastEventData.context.userScore", score));

		return iterable;
	}

	/*
	 * public FindIterable<Document> findNearCities(){ FindIterable<Document>
	 * iterable = null; return iterable; }
	 */
	public FindIterable<Document> findWatchesByTeachingRole(String teachingRole) {

		MongoCollection<Document> collection = getCollectionByName(WATCHES_COLLECTION);
		FindIterable<Document> iterable = collection.find(new Document(
				"lastEventData.context.teachingRoleCode", teachingRole));

		return iterable;
	}

	public FindIterable<Document> findLogsByTeachingRole(String teachingRole) {

		MongoCollection<Document> collection = getCollectionByName(LOG_COLLECTION);
		FindIterable<Document> iterable = collection.find(new Document(
				"attributes.score", 30));

		return iterable;
	}

	public Document findDocWithProvince(String province) {
		MongoCollection<Document> collection = getCollectionByName(MUNICIPALITIES_COLLECTION);
		Document doc = collection.find(new Document("province_code", province))
				.first();
		return doc;
	}

	public Document findDocWithMunicpality(String municipality) {
		MongoCollection<Document> collection = getCollectionByName(MUNICIPALITIES_COLLECTION);
		Document doc = collection.find(
				new Document("municipality_name", municipality)).first();
		return doc;
	}

	public Document findDocWithSchool(String school) {
		MongoCollection<Document> collection = getCollectionByName(SCHOOL_COLLECTION);
		Document doc = collection.find(new Document("code", school))
				.first();
		return doc;

	}

}
