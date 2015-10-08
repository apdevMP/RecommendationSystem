package core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.util.JSON;

/**
 * Classe di servizio che si frappone tra il sistema e il DBManager
 * 
 */
public class QueryManager {

	private DBManager manager;

	/**
	 * Costruttore di default
	 */
	public QueryManager() {
		manager = DBManager.getIstance();
	}

	/**
	 * Chiude la connessione col DBManager e con il database di MongoDB
	 */
	public void closeConnection() {
		manager.closeConnection();
	}

	/**
	 * Recupera i documenti per punteggio e li stampa
	 * 
	 * @param score
	 */
	public void findWatchesByScore(int score) {
		FindIterable<Document> iterable = manager.findWatchesByScore(score);
		for (Document doc : iterable) {
			System.out.println(doc.toJson());
		}
	}

	/**
	 * Recupera i documenti dai watches filtrati per materia insegnata
	 * 
	 * @param teachingRole
	 *            materia insegnata
	 * @return lista di documenti filtrati
	 */
	public FindIterable<Document> findWatchesByTeachingRole(String teachingRole) {
		FindIterable<Document> iterable = manager
				.findWatchesByTeachingRole(teachingRole);
		return iterable;
	}

	/**
	 * Recupera la lista di azioni eseguite dagli utenti(log),filtrate per la
	 * materia insegnata
	 * 
	 * @param teachingRole
	 *            materia insegnata
	 * @return lista di documenti filtrati
	 */
	public FindIterable<Document> findLogsByTeachingRole(String teachingRole) {
		FindIterable<Document> iterable = manager
				.findLogsByTeachingRole(teachingRole);

		return iterable;
	}

	/**
	 * Verifica se la provincia "province" � presente all'interno della regione
	 * "region"
	 * 
	 * @param region
	 *            regione
	 * @param province
	 *            provincia
	 * @return 0 se presente,1 altrimenti
	 */
	public int isProvinceInRegion(String region, String province) {

		Document doc = manager.findDocWithProvince(province);
		// se non viene recuperato alcun documento restituisce 1
		if (doc == null)
			return 1;

		// recupero la regione dal documento ed effettuo il confronto tra
		// stringhe
		String documentRegion = doc.getString("region_name");
		if (documentRegion.equals(region))
			return 0;
		else
			return 1;
	}

	/**
	 * Verifica se il comune rapppresentato dal suo codice � presente
	 * all'interno della regione "region"
	 * 
	 * @param region
	 *            regione
	 * @param municipality_code
	 *            codice del comune
	 * @return 0 se presente,1 altrimenti
	 */
	public int isMunicipalityInRegion(String region, String municipality_code) {
		// Si effettua la connessione al grafo presente in neo4j
		GraphManager gManager = GraphManager.getIstance();
		//gManager.connectToGraph("neo4j", "vanessa");

		// Si recupera il nome del comune dal codice
		String municipality = null;
		try {
			municipality = gManager.queryMunicipalityName(municipality_code);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Se la stringa � null,restituisco non presente
		if (municipality == null)
			return 1;

		// Viene recuperato il documento avente il comune trovato in precedenza
		Document doc = manager.findDocWithMunicpality(municipality);
		if (doc == null)
			return 1;

		// recupero la regione dal documento ed effettuo il confronto tra
		// stringhe
		String documentRegion = doc.getString("region_name");
		if (documentRegion.equals(region))
			return 0;
		else
			return 1;
	}

	/**
	 * Verifica se la scuola rapppresentata dal suo codice � presente
	 * all'interno della regione "region"
	 * 
	 * @param region
	 *            regione
	 * @param school
	 *            codice della scuola
	 * @return 0 se presente,1 altrimenti
	 */
	public int isSchoolInRegion(String region, String school) {
		// recupero il documento che contiene il codice della scuola e verifico
		// se esiste
		Document doc = manager.findDocWithSchool(school);
		if (doc == null)
			return 1;

		// recupero la regione dal documento ed effettuo il confronto tra
		// stringhe
		String documentRegion = doc.getString("regionName");
		if (documentRegion.equals(region))
			return 0;
		else
			return 1;
	}

	/**
	 * Recupera la lista delle possibili azioni che si possono eseguire dal log
	 * e le stampa su file
	 * 
	 * @return lista di azioni
	 */
	public ArrayList<String> getLogAction() {
		
		//Viene inizializzata la lista delle azioni e vengono recuperati tutti documentii del log
		ArrayList<String> actionList = new ArrayList<String>();
		FindIterable<Document> iterable = manager.findLogs();

		//Si inizializzano le classi per la scrittura su file 
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("azioni.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		@SuppressWarnings("resource")
		PrintStream write = new PrintStream(fos);

		//vengono salavate tutte le azioni una volta sola
		for (Document doc : iterable) {
			String action = doc.getString("action");

			if (!actionList.contains(action)) {
				actionList.add(action);
				write.println(doc.toJson());
			}
		}
		return actionList;

	}

	/**
	 * Scrive su file i log filtrati per un'azione specifica
	 * @param action azione
	 */
	public void getLogsByAction(String action) {
		FindIterable<Document> iterable = manager
				.findLogsByAction(action);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("prova.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PrintStream write = new PrintStream(fos);

		for (Document doc : iterable) {

			write.println(doc.toJson());

		}

	}

	/**
	 * Recupera il profilo dell'utente dal db o altrimenti ne crea uno
	 * @param id
	 * @return
	 */
	public Profile retrieveProfile(long id,String teachingRole,double score) {
		Profile profile = null;
		
		Document doc = manager.retrieveProfile(id);
		if(doc == null){
			profile = Profile.createProfile(id, teachingRole, score);
			this.saveProfile(profile);
		}
		else{
			String profileString = doc.toJson();
			Gson gson = new Gson();
			profile = gson.fromJson(profileString, Profile.class);
		
		}
		return profile;
	}

	public void saveProfile(Profile profile) {
		Gson gson = new Gson();
		String jsonProfile = gson.toJson(profile);
		Document document = Document.parse(jsonProfile); 
		
		manager.saveProfile(document);
	}

}
