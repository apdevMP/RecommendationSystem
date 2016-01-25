package core.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.client.FindIterable;

import core.persistence.GraphManager;
import core.persistence.MongoDBManager;

/**
 * Classe di servizio che si frappone tra il sistema e lo strato di persistenza
 * ({@link MongoDBManager} e {@link GraphManager})
 * 
 * 
 * @author apdev
 * 
 */
public class PersistenceService {

	private MongoDBManager dbManager;
	private GraphManager graphManager;
	private static final Logger LOGGER = Logger
			.getLogger(PersistenceService.class.getName());

	// private static Configuration configuration = null;

	/**
	 * Costruttore di default
	 */
	public PersistenceService() {
		dbManager = MongoDBManager.getIstance();
		graphManager = GraphManager.getIstance();
	}

	/**
	 * Chiude la connessione col DBManager e con il database di MongoDB
	 * 
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException {
		dbManager.closeConnection();
		graphManager.closeConnection();
	}

	/**
	 * Recupera i documenti dai watches filtrati per materia insegnata
	 * 
	 * @param teachingRole
	 *            materia insegnata
	 * @return lista di documenti filtrati
	 */
	public FindIterable<Document> findWatchesByTeachingRole(String teachingRole) {
		FindIterable<Document> iterable = dbManager
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
		FindIterable<Document> iterable = dbManager
				.findLogsByTeachingRole(teachingRole);

		return iterable;
	}

	/**
	 * Verifica se la provincia {@code province} è presente all'interno della
	 * regione {@code region}
	 * 
	 * @param region
	 *            regione
	 * @param province
	 *            provincia
	 * @return 0 se presente,1 altrimenti
	 */
	public int isProvinceInRegion(String region, String province) {

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
	 * Verifica se il comune rapppresentato da {@code municipalityCode} è
	 * all'interno della regione {@code region}
	 * 
	 * @param region
	 *            regione
	 * @param municipalityCode
	 *            codice del comune
	 * @return 0 se presente,1 altrimenti
	 */
	public int isMunicipalityInRegion(String region, String municipalityCode) {

		// Si recupera il nome del comune dal codice
		String municipality = null;
		try {
			municipality = graphManager.queryMunicipalityName(municipalityCode);

		} catch (SQLTimeoutException e) {
			return 1;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "[" + PersistenceService.class.getName()
					+ "] Cannot execute statement.");
		}

		// Se la stringa è null,restituisco non presente
		if (municipality == null) {
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
	 * Recupera il nome di un comune a partire dal suo {@code municipalityCode}
	 * 
	 * @param municipalityCode codice identificativo del comune
	 * @return stringa contenente il nome del comune o null in caso di fallimento
	 */
	public String queryMunicipalityName(String municipalityCode){
		
		// Si recupera il nome del comune dal codice
				String municipalityName = null;
				try {
					municipalityName = graphManager.queryMunicipalityName(municipalityCode);

				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, "[" + PersistenceService.class.getName()
							+ "] Cannot execute statement.");
				}
				
				return municipalityName;
	}

	/**
	 * Verifica se la scuola rapppresentata dal suo codice è presente
	 * all'interno di {@code region}
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
		Document doc = dbManager.findDocWithSchool(school);
		if (doc == null)
			return 1;

		// recupero la regione dal documento ed effettuo il confronto tra
		// stringhe
		String documentRegion = doc.getString("regionName");
		if (documentRegion.equals(region)) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Recupera la lista delle possibili azioni che si possono eseguire dal log
	 * e le stampa su file
	 * 
	 * @return lista di azioni
	 */
	public ArrayList<String> getLogAction() {

		// Viene inizializzata la lista delle azioni e vengono recuperati tutti
		// documentii del log
		ArrayList<String> actionList = new ArrayList<String>();
		FindIterable<Document> iterable = dbManager.findLogs();

		// Si inizializzano le classi per la scrittura su file
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("azioni.txt");
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, "[" + PersistenceService.class.getName()
					+ "] Cannot find file azioni.txt .");
		}
		@SuppressWarnings("resource")
		PrintStream write = new PrintStream(fos);

		// vengono salavate tutte le azioni una volta sola
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
	 * Recupera i log filtrati per una lista di azioni
	 * 
	 * @param actions
	 *            azioni su cui filtrare
	 * @return lista di documenti
	 */
	public FindIterable<Document> getLogsByAction(String[] actions) {

		FindIterable<Document> iterable = dbManager.findLogsByAction(actions);
		return iterable;

	}

	/**
	 * Recupera il codice identificativo della provincia passata per argomento,
	 * effettuando una query sul file municipality.csv
	 * 
	 * @param provinceCode
	 *            sigla della provincia
	 * @return codice identificativo della provincia
	 */
	public int retrieveProvinceId(String provinceCode) {
		Document document = dbManager.findDocWithProvince(provinceCode);
		return document.getInteger("province_id");
	}

	/**
	 * Recupera il codice identificativo del comune il cui codice istat è
	 * passato per argomento, effettuando una query sul file municipality.csv
	 * 
	 * @param municipalityCode
	 *            codice istat del comune
	 * @return codice identificativo del comune
	 */
	public long retrieveMunicipalityId(String municipalityCode) {

		long id = 0;
		try {
			id = graphManager.queryMunicipalityId(municipalityCode);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "[" + PersistenceService.class.getName()
					+ "] Cannot execute statement.");
		}

		return id;
	}

	/**
	 * Controlla se ci sono posizioni aperte in {@code schoolId} per il dato
	 * {@code teachingRole}
	 * 
	 * @param schoolId
	 *            id della scuola
	 * @param teachingRole
	 *            materia insegnata
	 * @return true se ci sono posizioni aperte, false altrimenti
	 * @throws SQLException
	 */
	public boolean freePositionAvailableAtSchool(String schoolId,
			String teachingRole) throws SQLException {

		boolean available = false;

		if (graphManager.queryFreePositionsInSchool(schoolId, teachingRole) > 0)
			available = true;

		return available;
	}

	/**
	 * Controlla se ci sono scuole all'interno di {@code provinceCode} con
	 * posizioni aperte per una {@code teachingRole}
	 * 
	 * @param provinceCode
	 *            provincia
	 * @param teachingRole
	 *            materia insegnata
	 * @return true se ci sono posizioni aperte, false altrimenti
	 * @throws SQLException
	 */
	public boolean freePositionAvailableInProvince(String provinceCode,
			String teachingRole) throws SQLException {

		return graphManager.queryFreePositionInProvince(provinceCode,
				teachingRole);
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
	public boolean freePositionAvailableInMunicipality(String municipalityCode,
			String teachingRole) throws SQLException {

		return graphManager.queryFreePositionInMunicipality(municipalityCode,
				teachingRole);
	}

	/**
	 * Usato in fase di classificazione Controlla sul grafo se la scuola è
	 * adatta per il teaching role dell'utente
	 * 
	 * @param schoolId
	 *            scuola
	 * @param teachingRole
	 *            materia insegnata
	 * @return true se la scuola prevede il teachingRole passato come materia di
	 *         insegnamento
	 * @throws SQLException
	 */
	public boolean isSchoolQuotedForTeachingRole(String schoolId,
			String teachingRole) throws SQLException {
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
	 * @param schoolId
	 *            id della scuola
	 * @param teachingRole
	 *            materia insegnata
	 * @param score
	 *            punteggio dell'utente
	 * @return true se la scuola prevede trasferimenti in uscita con score
	 *         minore o uguale a quello dell'utente
	 * @throws SQLException
	 */
	public boolean isScoreMatchingTransfers(String schoolId,
			String teachingRole, Double score) throws SQLException {

		if (graphManager.queryScoreMatching(schoolId, teachingRole, score) > 0)
			return true;
		else
			return false;

	}

	/**
	 * Recupera i documenti dalla collezione dei Watches
	 * 
	 * @param teachingRole
	 *            materia insegnata
	 * @return lista di documenti filtrati
	 */
	public FindIterable<Document> findWatches() {
		FindIterable<Document> iterable = dbManager.findWatches();
		return iterable;
	}

	/**
	 * Recupera la lista di azioni eseguite dagli utenti(log)
	 * 
	 * @param teachingRole
	 *            materia insegnata
	 * @return lista di documenti filtrati
	 */
	public FindIterable<Document> findLogs() {
		FindIterable<Document> iterable = dbManager.findLogs();

		return iterable;
	}

	/**
	 * Recupera una lista di Wacthes paginata
	 * 
	 * @param index
	 *            numero di pagina
	 * @return lista di documenti
	 */
	public FindIterable<Document> findWatches(int index) {
		FindIterable<Document> iterable = dbManager.findWatches(index);
		return iterable;
	}

	/**
	 * Restituisce una lista paginata di documenti provenienete dal log di
	 * navigazione e filtrata per tipologia di azione
	 * 
	 * @param actions
	 *            azioni per il filtraggio
	 * @param index
	 *            pagina
	 * @return lista di documenti
	 */
	public FindIterable<Document> getLogsByAction(String[] actions, int index) {
		FindIterable<Document> iterable = dbManager.findLogsByAction(actions,
				index);
		return iterable;
	}

	/**
	 * Restituisce una lista paginata di documenti provenienete dal log di
	 * navigazione e filtrata per tipologia di azione e per id dell'utente
	 * 
	 * @param id
	 *            id dell'utente
	 * @param actions
	 *            azioni per il filtraggio
	 * @return lista di documenti
	 */
	public FindIterable<Document> getLogsByActionAndId(long id, String[] actions) {
		FindIterable<Document> iterable = dbManager.findLogsByIdAndAction(id,
				actions);
		return iterable;
	}

	/**
	 * Restituisce una lista di documenti proveneienti dalla collezione dei
	 * Watches,filtrata per id
	 * 
	 * @param id
	 *            id dell'utente
	 * @return lista di documenti
	 */
	public FindIterable<Document> getWatchById(long id) {
		FindIterable<Document> iterable = dbManager.findWatchById(id);
		return iterable;
	}

	/**
	 * 
	 * Recupera il comune di appartenenza di {@code school}
	 * 
	 * @param school
	 *            scuola
	 * @return comune di appartenenza della scuola
	 * @throws SQLException
	 */
	public String getMunicipalityFromSchool(String school) throws SQLException {

		String municipalityString = graphManager
				.queryMunicipalityCodeFromSchool(school);
		return municipalityString;
	}

	/**
	 * Recupera una provincia data {@code school}
	 * 
	 * @param school
	 *            scuola
	 * @return provincia dove si trova {@code school}
	 * @throws SQLException
	 */
	public String getProvinceFromSchool(String school) throws SQLException {
		String province = null;
		/*
		 * se la lunghezza della stringa è 10, allora siamo in un caso standard
		 * e basta estrapolare i primi due caratteri per ottenere il codice
		 * della provincia
		 */
		if (school.length() == 10)

			province = school.substring(0, 2);

		else
			province = graphManager.queryProvinceCodeFromSchool(school);

		return province;

	}
}
