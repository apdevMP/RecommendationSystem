package core.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;

import core.persistence.GraphManager;
import core.persistence.MongoDBManager;
import core.service.profile.Profile;

/**
 * Classe di servizio che si frappone tra il sistema e il DBManager
 * 
 */
public class PersistenceService
{

	private MongoDBManager	dbManager;
	private GraphManager	graphManager;

	// private static Configuration configuration = null;

	/**
	 * Costruttore di default
	 */
	public PersistenceService()
	{
		dbManager = MongoDBManager.getIstance();
		graphManager = GraphManager.getIstance();
	}

	/**
	 * Chiude la connessione col DBManager e con il database di MongoDB
	 * @throws SQLException 
	 */
	public void closeConnection() throws SQLException
	{
		dbManager.closeConnection();
		graphManager.closeConnection();
	}

	/**
	 * Recupera i documenti per punteggio e li stampa
	 * 
	 * @param score
	 */
	public void findWatchesByScore(int score)
	{
		FindIterable<Document> iterable = dbManager.findWatchesByScore(score);
		for (Document doc : iterable)
		{
			System.out.println(doc.toJson());
		}
	}

	/**
	 * Recupera i documenti dai watches filtrati per materia insegnata
	 * 
	 * @param teachingRole materia insegnata
	 * @return lista di documenti filtrati
	 */
	public FindIterable<Document> findWatchesByTeachingRole(String teachingRole)
	{
		FindIterable<Document> iterable = dbManager.findWatchesByTeachingRole(teachingRole);
		return iterable;
	}

	/**
	 * Recupera la lista di azioni eseguite dagli utenti(log),filtrate per la
	 * materia insegnata
	 * 
	 * @param teachingRole materia insegnata
	 * @return lista di documenti filtrati
	 */
	public FindIterable<Document> findLogsByTeachingRole(String teachingRole)
	{
		FindIterable<Document> iterable = dbManager.findLogsByTeachingRole(teachingRole);

		return iterable;
	}

	/**
	 * Verifica se la provincia "province" � presente all'interno della regione
	 * "region"
	 * 
	 * @param region regione
	 * @param province provincia
	 * @return 0 se presente,1 altrimenti
	 */
	public int isProvinceInRegion(String region, String province)
	{

		Document doc = dbManager.findDocWithProvince(province);
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
	 * @param region regione
	 * @param municipality_code codice del comune
	 * @return 0 se presente,1 altrimenti
	 */
	public int isMunicipalityInRegion(String region, String municipality_code)
	{

		// Si recupera il nome del comune dal codice
		String municipality = null;
		try
		{
			municipality = graphManager.queryMunicipalityName(municipality_code);
			System.out.println("sto qua");

		} catch (SQLTimeoutException e)
		{
			return 1;
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Se la stringa � null,restituisco non presente
		if (municipality == null)
		{
			System.out.println("municipality null, restituisco 1");
			return 1;
		}
		// Viene recuperato il documento avente il comune trovato in precedenza
		Document doc = dbManager.findDocWithMunicpality(municipality);
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
	 * @param region regione
	 * @param school codice della scuola
	 * @return 0 se presente,1 altrimenti
	 */
	public int isSchoolInRegion(String region, String school)
	{
		// recupero il documento che contiene il codice della scuola e verifico
		// se esiste
		System.out.println("vedo se la scuola è nella regione");
		Document doc = dbManager.findDocWithSchool(school);
		if (doc == null)
			return 1;

		// recupero la regione dal documento ed effettuo il confronto tra
		// stringhe
		String documentRegion = doc.getString("regionName");
		System.out.println("documentRegion="+documentRegion);
		if (documentRegion.equals(region))
		{
			System.out.println("ritorno 0");
			return 0;
		}
		else{
			System.out.println("ritorno 1");
			return 1;
		}
	}

	/**
	 * Recupera la lista delle possibili azioni che si possono eseguire dal log
	 * e le stampa su file
	 * 
	 * @return lista di azioni
	 */
	public ArrayList<String> getLogAction()
	{

		// Viene inizializzata la lista delle azioni e vengono recuperati tutti
		// documentii del log
		ArrayList<String> actionList = new ArrayList<String>();
		FindIterable<Document> iterable = dbManager.findLogs();

		// Si inizializzano le classi per la scrittura su file
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream("azioni.txt");
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		@SuppressWarnings("resource")
		PrintStream write = new PrintStream(fos);

		// vengono salavate tutte le azioni una volta sola
		for (Document doc : iterable)
		{
			String action = doc.getString("action");

			if (!actionList.contains(action))
			{
				actionList.add(action);
				write.println(doc.toJson());
			}
		}
		return actionList;

	}

	/**
	 * Scrive su file i log filtrati per un'azione specifica
	 * 
	 * @param action azione
	 */
	public FindIterable<Document> getLogsByAction(String[] action)
	{

		FindIterable<Document> iterable = dbManager.findLogsByAction(action);
		return iterable;

	}

	/**
	 * Recupera il profilo dell'utente dal db o altrimenti ne crea uno
	 * 
	 * @param id
	 * @return
	 */
	public Profile retrieveProfile(long id, String teachingRole, double score, String position)
	{
		Profile profile = null;

		Document doc = dbManager.retrieveProfile(id);
		if (doc == null)
		{
			profile = Profile.createProfile(id, teachingRole, score, position);
			this.saveProfile(profile);
		} else
		{
			String profileString = doc.toJson();
			Gson gson = new Gson();
			profile = gson.fromJson(profileString, Profile.class);

		}
		return profile;
	}

	public void saveProfile(Profile profile)
	{
		Gson gson = new Gson();
		String jsonProfile = gson.toJson(profile);
		Document document = Document.parse(jsonProfile);

		dbManager.saveProfile(document);
	}

	/**
	 * Recupera il codice identificativo della provincia passata per argomento,
	 * effettuando una query sul file municipality.csv
	 * 
	 * @param provinceCode sigla della provincia
	 * @return codice identificativo della provincia
	 */
	public int retrieveProvinceId(String provinceCode)
	{
		Document document = dbManager.findDocWithProvince(provinceCode);
		return document.getInteger("province_id");
	}

	/**
	 * Recupera il codice identificativo del comune il cui codice istat è
	 * passato per argomento, effettuando una query sul file municipality.csv
	 * 
	 * @param municipalityCode codice istat del comune
	 * @return codice identificativo del comune
	 */
	public long retrieveMunicipalityId(String municipalityCode)
	{

		long id = 0;
		try
		{
			id = graphManager.queryMunicipalityId(municipalityCode);
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//
		return id;
	}

	/**
	 * Controlla se ci sono posizioni aperte nella scuola per il dato
	 * teachingRole
	 * 
	 * @param schoolId
	 * @param teachingRole
	 * @return true se ci sono posizioni aperte, false altrimenti
	 * @throws SQLException
	 */
	public boolean freePositionAvailableAtSchool(String schoolId, String teachingRole) throws SQLException
	{
		System.out.println("vedo se ci sono posizioni libere");

		boolean available = false;

		if (graphManager.queryFreePositionsInSchool(schoolId, teachingRole) > 0)
			available = true;

		return available;
	}

	/**
	 * Controlla se ci sono scuole all'interno della provincia con posizioni
	 * aperte per una materia di insegnamento
	 * 
	 * @param provinceCode
	 * @param teachingRole
	 * @return true se ci sono posizioni aperte, false altrimenti
	 * @throws SQLException
	 */
	public boolean freePositionAvailableInProvince(String provinceCode, String teachingRole) throws SQLException
	{

		return graphManager.queryFreePositionInProvince(provinceCode, teachingRole);
	}

	/**
	 * Controlla se ci sono scuole all'interno del comune con posizioni aperte
	 * per una materia di insegnamento
	 * 
	 * @param municipalityCode
	 * @param teachingRole
	 * @return
	 * @throws SQLException
	 */
	public boolean freePositionAvailableInMunicipality(String municipalityCode, String teachingRole) throws SQLException
	{

		return graphManager.queryFreePositionInMunicipality(municipalityCode, teachingRole);
	}

	/**
	 * Usato in fase di classificazione Controlla sul grafo se la scuola è
	 * adatta per il teaching role dell'utente
	 * 
	 * @return true se la scuola prevede il teachingRole passato come materia di
	 * insegnamento
	 * @throws SQLException
	 */
	public boolean isSchoolQuotedForTeachingRole(String schoolId, String teachingRole) throws SQLException
	{
		System.out.println("vedo se c'è il teaching role");
		if (graphManager.queryTeachingRoleInSchool(schoolId, teachingRole) > 0)
			return true;
		else
			return false;

	}

	/**
	 * Usato in fase di classificazione Controlla sul grafo se tra gli utenti
	 * trasferiti da quella scuola ce ne sono alcuni con score relazionabile a
	 * quello dell'utente
	 * 
	 * @return true se la scuola prevede trasferimenti in uscita con score
	 * minore o uguale a quello dell'utente
	 * @throws SQLException
	 */
	public boolean isScoreMatchingTransfers(String schoolId, String teachingRole, Double score) throws SQLException
	{

		System.out.println("vedo se ci sono posti per il mio score");
		if (graphManager.queryScoreMatching(schoolId, teachingRole, score) > 0)
			return true;
		else
			return false;

	}

	/**
	 * Recupera i documenti dai watches
	 * 
	 * @param teachingRole materia insegnata
	 * @return lista di documenti filtrati
	 */
	public FindIterable<Document> findWatches()
	{
		FindIterable<Document> iterable = dbManager.findWatches();
		return iterable;
	}

	/**
	 * Recupera la lista di azioni eseguite dagli utenti(log)
	 * 
	 * @param teachingRole materia insegnata
	 * @return lista di documenti filtrati
	 */
	public FindIterable<Document> findLogs()
	{
		FindIterable<Document> iterable = dbManager.findLogs();

		return iterable;
	}

	public FindIterable<Document> findWatches(int index)
	{
		FindIterable<Document> iterable = dbManager.findWatches(index);
		return iterable;
	}

	public FindIterable<Document> getLogsByAction(String[] actions, int index)
	{
		FindIterable<Document> iterable = dbManager.findLogsByAction(actions, index);
		return iterable;
	}

	public FindIterable<Document> getLogsByActionAndId(long id, String[] actions)
	{
		FindIterable<Document> iterable = dbManager.findLogsByIdAndAction(id, actions);
		return iterable;
	}

	public FindIterable<Document> getWatchById(long id)
	{
		FindIterable<Document> iterable = dbManager.findWatchById(id);
		return iterable;
	}

	public String getMunicipalityFromSchool(String school) throws SQLException
	{

		String municipalityString = graphManager.queryMunicipalityCodeFromSchool(school);
		return municipalityString;
	}

	public String getProvinceFromSchool(String school) throws SQLException
	{
		String province = null;
		/*
		 * se la lunghezza della stringa è 10, allora siamo in un caso standard
		 * e basta estrapolare i primi due caratteri per ottenere il codice
		 * della provincia
		 */
		if (school.length() == 10)

			province = school.substring(0, 3);

		else
			province = graphManager.queryProvinceCodeFromSchool(school);
		
		return province;

	}
}
