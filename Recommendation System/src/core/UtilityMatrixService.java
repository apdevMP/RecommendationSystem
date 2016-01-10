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

public class UtilityMatrixService {

	private Profile userProfile;
	@SuppressWarnings("unused")
	private String region;
	private QueryManager queryManager;
	private Map<String, Long> itemsMap;
	private Map<String, Long> categoriesMap;

	private static final Logger LOGGER = Logger
			.getLogger(UtilityMatrixService.class.getName());

	private static final String[] actions = { "webapi_school_aggregates",
			"webapi_municipality_aggregates", "webapi_get_best_schools" };

	public UtilityMatrixService() {

	}

	public UtilityMatrixService(Profile profile, String region) {
		this.userProfile = profile;
		this.region = region;

		this.categoriesMap = new HashMap<String, Long>();

	}

	/**
	 * Recupera le liste di log e watch per creare la matrice di utilit�
	 * composta da UtilityMatrixPreference
	 * 
	 * @return
	 */
	public UtilityMatrix createPreferences() {
		// istanzio un queryManager per recuperare dati dalle collezioni
		queryManager = new QueryManager();

		// si recupera la materia di insegnamento dell'utente
		@SuppressWarnings("unused")
		String teachingRole = userProfile.getTeachingRole();

		// si recupera la lista di watch e di log in base alla materia di
		// insegnamento
		// FindIterable<Document> itWatches =
		// queryManager.findWatchesByTeachingRole(teachingRole);
		LOGGER.info("[" + UtilityMatrixService.class.getName()
				+ "] Finding watches...");

		FindIterable<Document> itWatches = queryManager.findWatches();
		// FindIterable<Document> itLogs =
		// queryManager.findLogsByTeachingRole(teachingRole);
		LOGGER.info("[" + UtilityMatrixService.class.getName()
				+ "] Finding logs...");
		;
		FindIterable<Document> itLogs = queryManager.getLogsByAction(actions);

		// le liste di watch e log vengono filtrate per regione
		// ArrayList<Document> listWatches =
		// Filter.filterWatchesByRegion(itWatches, region);
		// ArrayList<Document> listLogs = Filter.filterLogsByRegion(itLogs,
		// region);

		// Utilizzo le liste per semplicit�
		List<Document> listWatches = Lists.newArrayList(itWatches);
		List<Document> listLogs = Lists.newArrayList(itLogs);

		LOGGER.info("[" + UtilityMatrixService.class.getName()
				+ "] Creating utility matrix..");
		// dalle liste viene creata la matrice di utilit� che viene restituita
		UtilityMatrixCreator ums = new UtilityMatrixCreator(userProfile);
		UtilityMatrix uMatrix = ums
				.fillMatrixPreferences(listWatches, listLogs);

		return uMatrix;
	}

	public UtilityMatrix createPreferencesWithPagination() {
		// istanzio un queryManager per recuperare dati dalle collezioni
		queryManager = new QueryManager();

		long profileId = userProfile.getId();
		
		FindIterable<Document> itWatches = null;
		FindIterable<Document> itLogs = null;
		boolean finishLog = false;
		boolean finishWatch = false;
		int cont = 0;
		LOGGER.info("[" + UtilityMatrixService.class.getName()
				+ "] Finding watches...");
		LOGGER.info("[" + UtilityMatrixService.class.getName()
				+ "] Finding logs...");
		UtilityMatrix utilityMatrixPreference = new UtilityMatrix();
		for (;;) {

			// si recupera la lista di watch e di log
			if (!finishWatch) {
				itWatches = queryManager.findWatches(cont);
			}

			if (!finishLog) {
				itLogs = queryManager.getLogsByAction(actions, cont);
			}
			// Utilizzo le liste per semplicit�
			List<Document> listWatches = Lists.newArrayList(itWatches);
			List<Document> listLogs = Lists.newArrayList(itLogs);

			if (listLogs.isEmpty()) {
				finishLog = true;
			}
			if (listWatches.isEmpty()) {
				finishWatch = true;
			}

			if (finishLog && finishWatch) {
				break;
			}

			if (!finishLog) {
				utilityMatrixPreference.fillPreferencesWithLogs(listLogs,profileId);
			}
			if (!finishWatch) {
				utilityMatrixPreference.fillPreferencesWithWatches(listWatches,profileId);
			}
			cont++;
		}

		LOGGER.info("[" + UtilityMatrixService.class.getName()
				+ "] Watches and Logs examined.");
		System.out.println(utilityMatrixPreference.getPreferences().size());
		utilityMatrixPreference.addListToPreferences(userProfile.getUserPreferences());
		System.out.println(utilityMatrixPreference.getPreferences().size());
		utilityMatrixPreference.sortForPlacePreferences();

		return utilityMatrixPreference;

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
