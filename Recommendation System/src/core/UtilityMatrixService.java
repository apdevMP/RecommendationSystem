package core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;

/**
 * Classe di Servizio per il riempimento ed il salvataggio su file della matrice
 * di utilità
 * 
 * 
 * 
 */
public class UtilityMatrixService {

	private Profile userProfile;
	private QueryManager queryManager;
	private Map<String, Long> itemsMap;
	private Map<String, Long> categoriesMap;

	private static final Logger LOGGER = Logger
			.getLogger(UtilityMatrixService.class.getName());

	private static final String[] actions = { "webapi_school_aggregates",
			"webapi_municipality_aggregates", "webapi_get_best_schools" };

	/**
	 * Costruttore di default
	 */
	public UtilityMatrixService() {
		this.categoriesMap = new HashMap<String, Long>();
	}

	/**
	 * Costruttore che utilizza il profilo utente
	 * 
	 * @param profile
	 */
	public UtilityMatrixService(Profile profile) {
		this.userProfile = profile;
		this.categoriesMap = new HashMap<String, Long>();

	}

	/**
	 * Crea una {@link UtilityMatrix} utilizzando le preferenze recuperate dalle
	 * collezioni di Watch e Log
	 * 
	 * @return
	 */
	public UtilityMatrix createPreferencesWithPagination() {
		/* Istanzio un queryManager per recuperare dati dalle collezioni */
		queryManager = new QueryManager();

		/* Si recupera il profile dell'utente */
		long profileId = userProfile.getId();

		/*
		 * Si inizializzano le condizioni di usciti e gli iterable di Watch e
		 * Log
		 */
		FindIterable<Document> itWatches = null;
		FindIterable<Document> itLogs = null;
		boolean finishLog = false;
		boolean finishWatch = false;
		int cont = 0;

		LOGGER.info("[" + UtilityMatrixService.class.getName()
				+ "] Finding watches...");
		LOGGER.info("[" + UtilityMatrixService.class.getName()
				+ "] Finding logs...");

		/* Si inizializza la UtilityMatrix */
		UtilityMatrix utilityMatrix = new UtilityMatrix();
		for (;;) {

			/* Se i Watch non sono finiti, si recupera una pagina di Watch */
			if (!finishWatch) {
				itWatches = queryManager.findWatches(cont);
			}

			/* Se i Log non sono finiti, si recupera una pagina di Log */
			if (!finishLog) {
				itLogs = queryManager.getLogsByAction(actions, cont);
			}
			/* Si utilizzano le liste convertendo gli oggetti FindIterable */
			List<Document> listWatches = Lists.newArrayList(itWatches);
			List<Document> listLogs = Lists.newArrayList(itLogs);

			/* Se la lista dei Log è vuota imposta il flag a true */
			if (listLogs.isEmpty()) {
				finishLog = true;
			}

			/* Se la lista dei Watch è vuota imposta il flag a true */
			if (listWatches.isEmpty()) {
				finishWatch = true;
			}

			/* Se entrambi i flag di uscita sono true si esce dal ciclo */
			if (finishLog && finishWatch) {
				break;
			}

			/* Se la lista dei Log non è vuota,si riempie la matrice di utilità */
			if (!finishLog) {
				utilityMatrix.fillPreferencesWithLogs(listLogs, profileId);
			}

			/* Se la lista dei Log non è vuota,si riempie la matrice di utilità */
			if (!finishWatch) {
				utilityMatrix
						.fillPreferencesWithWatches(listWatches, profileId);
			}
			cont++;
		}

		LOGGER.info("[" + UtilityMatrixService.class.getName()
				+ "] Watches and Logs examined.");

		/*
		 * Si aggiunge la lista ricavata dal profilo utente a quella della
		 * matrice di utilità
		 */
		utilityMatrix.addListToPreferences(userProfile.getUserPreferences());

		/* Si ordinano le preferenze all'interno della matrice di utilità */
		utilityMatrix.sortForPlacePreferences();

		/* Restituisce la matrice di utilità */
		return utilityMatrix;

	}

	public Map<String, Long> getItemsMap() {
		return this.itemsMap;
	}

	public Map<String, Long> getCategoriesMap() {
		return this.categoriesMap;
	}

	/**
	 * Metodo che permette di salvare le preferenze su un apposito file
	 * 
	 * @param matrix
	 */
	public void savePreferences(UtilityMatrix matrix) {
		/* Prelevo la lista delle preferenze dalla UtilityMatrix */
		List<UtilityMatrixPreference> preferenceList = matrix.getPreferences();
		LOGGER.info("[" + UtilityMatrixService.class.getName()
				+ "] Saving data..");
		try {
			/* Si crea il file nel quale verranno salvate le preferenze */
			FileWriter writer = new FileWriter("matrix_value.csv");

			long counter = 0;
			this.itemsMap = new HashMap<String, Long>();

			for (int j = 0; j < matrix.getContProvince(); j++) {
				String provinceCodeString = preferenceList.get(j).getPlaceId();

				Long i = itemsMap.get(provinceCodeString);

				if (i == null) {
					itemsMap.put(provinceCodeString, counter);

					i = (long) counter;
					++counter;
				}
			}

			this.categoriesMap.put("province", counter);
			for (int j = matrix.getContProvince(); j < matrix
					.getContMunicipality(); j++) {

				String municipalityCodeString = preferenceList.get(j)
						.getPlaceId();
				Long i = itemsMap.get(municipalityCodeString);
				if (i == null) {
					itemsMap.put(municipalityCodeString, counter);

					i = (long) counter;
					++counter;
				}
			}

			this.categoriesMap.put("comuni", counter);

			for (int j = matrix.getContMunicipality(); j < matrix
					.getContSchool(); j++) {

				String schoolCodeString = preferenceList.get(j).getPlaceId();
				Long i = itemsMap.get(schoolCodeString);
				if (i == null) {
					itemsMap.put(schoolCodeString, counter);
					// System.out.println("MAP: "+schoolCodeString+","+counter);
					i = (long) counter;
					++counter;
				}
			}

			this.categoriesMap.put("scuole", counter);

			for (UtilityMatrixPreference p : preferenceList) {
				{
					double value = p.getScore();
					/*
					 * Dato che il Recommender di Mahout richiede che i dati
					 * presenti nel csv siano di tipo Long, al posto della sigla
					 * della procincia si recupera e scrive il codice
					 * identificativo, utilizzando il file municipality.csv
					 */

					if (value > 0)
						writer.append(String.valueOf(p.getUserId()) + ","
								+ itemsMap.get(p.getPlaceId()) + ","
								+ String.valueOf(value) + "\n");
				}

			}
			/* Si chiude il file */
			writer.flush();
			writer.close();

			LOGGER.info("[" + UtilityMatrixService.class.getName()
					+ "] Data successfully saved");
			// System.out.println("Data saved!");
		} catch (IOException e) {
			LOGGER.severe("[" + UtilityMatrixService.class.getName()
					+ "] Exception: " + e.getMessage());
			// System.out.println("File not Found!!!");

		}

	}

}
