package core;

import java.util.Arrays;
import java.util.logging.Logger;

import org.bson.Document;

import utils.Configuration;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Classe che gestisce la connessione con il database e le query da effettuare
 * 
 */
public class MongoDBManager {

	private static MongoClient client = null;
	private static Configuration configuration = null;

	private MongoDatabase database;
	private static MongoDBManager manager = null;

	private static final Logger LOGGER = Logger.getLogger(MongoDBManager.class
			.getName());

	private MongoDBManager() {

		startConnection();
		setDatabase();
	}

	/**
	 * Restituisce l'stanza del manager del database di MongoDB
	 * 
	 * @return
	 */
	public static MongoDBManager getIstance() {
		if (configuration == null) {
			configuration = Configuration.getIstance();

		}
		if (manager == null)
			manager = new MongoDBManager();
		return manager;

	}

	/**
	 * Avvia la connessione
	 */
	private void startConnection() {
		// Se client � null allora occorre inizializzare la connessione,
		// altrimenti gi� esiste
		if (client == null) {
			client = new MongoClient(configuration.getMongo_server_address(),
					configuration.getMongo_port());

		} else {
			LOGGER.warning("[" + MongoDBManager.class.getName()
					+ "]Connection already started");
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
		database = client.getDatabase(configuration.getMongodb_name());
	}

	/**
	 * Recupera il database sul quale � possibile effettuare query
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

		MongoCollection<Document> collection = getCollectionByName(configuration
				.getWatches_collection());
		FindIterable<Document> iterable = collection.find(new Document(
				"lastEventData.context.userScore", score));

		return iterable;
	}

	/**
	 * Recupera una lista di documenti dai Watches filtrati per la materia
	 * insegnata
	 * 
	 * @param teachingRole
	 *            codice della materia insegnata
	 * @return lista di documenti
	 */
	public FindIterable<Document> findWatchesByTeachingRole(String teachingRole) {

		MongoCollection<Document> collection = getCollectionByName(configuration
				.getWatches_collection());
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

		MongoCollection<Document> collection = getCollectionByName(configuration
				.getLog_collection());
		FindIterable<Document> iterable = collection.find(new Document("$or",
				Arrays.asList(new Document("attributes.teachingRoleCodeTo",
						teachingRole), new Document(
						"attributes.codTeachingRoles", teachingRole).append(
						"action", "webapi_get_best_schools"))));

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
		MongoCollection<Document> collection = getCollectionByName(configuration
				.getMunicipalities_collection());
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
		MongoCollection<Document> collection = getCollectionByName(configuration
				.getMunicipalities_collection());
		Document doc = collection.find(
				new Document("municipality_name", municipality)).first();
		return doc;
	}

	public Document findDocWithMunicipalityCode(String municipalityCode) {

		MongoCollection<Document> collection = getCollectionByName(configuration
				.getMunicipalities_collection());
		Document doc = collection.find(
				new Document("municipality_istat_code", municipalityCode))
				.first();
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
		MongoCollection<Document> collection = getCollectionByName(configuration
				.getSchool_collection());
		Document doc = collection.find(new Document("code", school)).first();

		return doc;

	}

	/**
	 * Recupera la lista di documenti contenuti nel Log
	 * 
	 * @return lista di documenti del log
	 */
	public FindIterable<Document> findLogs() {
		MongoCollection<Document> collection = getCollectionByName(configuration
				.getLog_collection());
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
	public FindIterable<Document> findLogsByAction(String[] action) {
		MongoCollection<Document> collection = getCollectionByName(configuration
				.getLog_collection());
		FindIterable<Document> iterable = collection
				.find(new Document("$or", Arrays.asList(new Document("action",
						action[0]), new Document("action", action[1]),
						new Document("action", action[2]))));
		return iterable;
	}

	/**
	 * Recupera il profilo dell'utente all'interno del sistema
	 * 
	 * @param id
	 * @return profilo utente, altrimenti null
	 */
	public Document retrieveProfile(long id) {
		MongoCollection<Document> collection = getCollectionByName(configuration
				.getProfile_collection());
		Document document = collection.find(new Document("id", id)).first();
		return document;
	}

	public void saveProfile(Document doc) {

		MongoCollection<Document> collection = getCollectionByName(configuration
				.getProfile_collection());
		collection.insertOne(doc);
		System.out.println("salvato document");
	}

	/**
	 * Recupera una lista di documenti dai Watches insegnata
	 * 
	 * @param teachingRole
	 *            codice della materia insegnata
	 * @return lista di documenti
	 */
	public FindIterable<Document> findWatches() {

		MongoCollection<Document> collection = getCollectionByName(configuration
				.getWatches_collection());
		FindIterable<Document> iterable = collection.find();

		return iterable;
	}

	public FindIterable<Document> findWatches(int index) {
		MongoCollection<Document> collection = getCollectionByName(configuration
				.getWatches_collection());
		FindIterable<Document> iterable = collection
				.find(new Document("lastEventData.context.year", new Document(
						"$gt", 2000))).limit(configuration.getDoc_per_page())
				.skip(index * configuration.getDoc_per_page());

		return iterable;
	}

	public FindIterable<Document> findLogsByAction(String[] action, int index) {
		MongoCollection<Document> collection = getCollectionByName(configuration
				.getLog_collection());
		FindIterable<Document> iterable = collection
				.find(new Document("$and",
						Arrays.asList(
								new Document("$or", Arrays.asList(new Document(
										"action", action[0]), new Document(
										"action", action[1]), new Document(
										"action", action[2]))), new Document(
										"attributes.year", new Document("$gt",
												2000)))))
				.limit(configuration.getDoc_per_page())
				.skip(index * configuration.getDoc_per_page());
		return iterable;
	}

	public FindIterable<Document> findLogsByIdAndAction(long userId,
			String[] actions) {
		MongoCollection<Document> collection = getCollectionByName(configuration
				.getLog_collection());
		FindIterable<Document> iterable = collection.find(new Document("$and",
				Arrays.asList(
						new Document("$or", Arrays
								.asList(new Document("action", actions[0]),
										new Document("action", actions[1]),
										new Document("action", actions[2]))),
						new Document("userId", userId))));
		return iterable;
	}

	public FindIterable<Document> findWatchById(long userId) {
		MongoCollection<Document> collection = getCollectionByName(configuration
				.getWatches_collection());
		FindIterable<Document> iterable = collection.find(new Document(
				"userId", userId));

		return iterable;
	}

}
