package core;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Classe che gestisce la connessione con il database e le query da effettuare
 * 
 */
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

	/**
	 * Restituisce l'stanza del manager del database di MongoDB
	 * 
	 * @return
	 */
	public static DBManager getIstance() {

		if (manager == null)
			manager = new DBManager();
		return manager;

	}

	/**
	 * Avvia la connessione
	 */
	private void startConnection() {
		// Se client è null allora occorre inizializzare la connessione,
		// altrimenti già esiste
		if (client == null) {
			client = new MongoClient(SERVER_ADDRESS, PORT);

		} else {
			System.out.println("Connection alrady started");
		}
	}

	/**
	 * Chiude la connessione al database
	 */
	public void closeConnection() {
		if (client != null) {
			client.close();
			client = null;
		}
	}

	/**
	 * Imposta il database con quello denominato con DATABASE_NAME
	 */
	public void setDatabase() {
		database = client.getDatabase(DATABASE_NAME);
	}

	/**
	 * Recupera il database sul quale è possibile effettuare query
	 * 
	 * @return
	 */
	public MongoDatabase getDatabase() {
		return database;
	}

	/**
	 * Recupera la collezione di documenti di MongoDB dal nome
	 * 
	 * @param collectionName
	 *            nome della collezione
	 * @return collezione di documenti di MongoDB
	 */
	public MongoCollection<Document> getCollectionByName(String collectionName) {
		MongoCollection<Document> collection = database
				.getCollection(collectionName);
		return collection;
	}

	/**
	 * Recupera i watches con punteggio pari a quello passato come parametro
	 * 
	 * @param score
	 *            punteggio dell'utente
	 * @return lista dei documenti filtrati
	 */
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

	/**
	 * Recupera una lista di documenti dai Watches filtrati per la materia
	 * insegnata
	 * 
	 * @param teachingRole
	 *            codice della materia insegnata
	 * @return lista di documenti
	 */
	public FindIterable<Document> findWatchesByTeachingRole(String teachingRole) {

		MongoCollection<Document> collection = getCollectionByName(WATCHES_COLLECTION);
		FindIterable<Document> iterable = collection.find(new Document(
				"lastEventData.context.teachingRoleCode", teachingRole));

		return iterable;
	}

	/**
	 * Recupera una lista di documenti dal Log filtrato per la materia insegnata
	 * 
	 * @param teachingRole
	 *            codice della materia insegnata
	 * @return lista di documenti
	 */
	public FindIterable<Document> findLogsByTeachingRole(String teachingRole) {

		MongoCollection<Document> collection = getCollectionByName(LOG_COLLECTION);
		FindIterable<Document> iterable = collection.find(new Document(
				"attributes.teachingRoleCodeTo", teachingRole));

		return iterable;
	}

	/**
	 * Recupera un documento contenente la provincia passata
	 * 
	 * @param province
	 *            provincia da cercare
	 * @return documento contente province
	 */
	public Document findDocWithProvince(String province) {
		MongoCollection<Document> collection = getCollectionByName(MUNICIPALITIES_COLLECTION);
		Document doc = collection.find(new Document("province_code", province))
				.first();
		return doc;
	}

	/**
	 * Recupera un documento contenente il comune passato
	 * 
	 * @param municipality
	 *            comune da cercare
	 * @return documento contente municipality
	 */
	public Document findDocWithMunicpality(String municipality) {
		MongoCollection<Document> collection = getCollectionByName(MUNICIPALITIES_COLLECTION);
		Document doc = collection.find(
				new Document("municipality_name", municipality)).first();
		return doc;
	}

	/**
	 * Recupera un documento contenente la scuola passata
	 * 
	 * @param school
	 *            scuola da cercare
	 * @return documento contente school
	 */
	public Document findDocWithSchool(String school) {
		MongoCollection<Document> collection = getCollectionByName(SCHOOL_COLLECTION);
		Document doc = collection.find(new Document("code", school)).first();
		return doc;

	}

	/**
	 * Recupera la lista di documenti contenuti nel Log
	 * 
	 * @return lista di documenti del log
	 */
	public FindIterable<Document> findLogs() {
		MongoCollection<Document> collection = getCollectionByName(LOG_COLLECTION);
		FindIterable<Document> iterable = collection.find();
		return iterable;
	}

	/**
	 * Recupera la lista di documenti contenuti nel log, filtrati per l'azione
	 * compiuta
	 * 
	 * @param action
	 *            azione compiuta dagli utenti
	 * @return lista di documenti
	 */
	public FindIterable<Document> findLogsByAction(String action) {
		MongoCollection<Document> collection = getCollectionByName(LOG_COLLECTION);
		FindIterable<Document> iterable = collection.find(new Document(
				"action", action));
		return iterable;
	}
}
