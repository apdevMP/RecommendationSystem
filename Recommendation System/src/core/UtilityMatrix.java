package core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;

import utils.Utils;

/**
 * Questa classe rappresenta la matrice di utilitï¿½ ricavata dai dati
 * 
 */
public class UtilityMatrix {

	private static final String TARGET = "target";
	private static final String TYPE_ID = "typeId";
	private static final String KEY = "key";
	private static final String LAST_EVENT_DATA = "lastEventData";
	private static final String USER_ID = "userId";
	@SuppressWarnings("unused")
	private static final String CONTEXT = "context";
	@SuppressWarnings("unused")
	private static final String USER_SCORE = "userScore";
	private static final String EVENT_TYPE = "eventType";
	@SuppressWarnings("unused")
	private static final String SCORE = "score";
	private static final String CODE_MUNICIPALITY = "codeMunicipality";
	private static final String CODE_PROVINCE = "codeProvince";
	private static final String PROVINCE_CODE = "provinceCode";
	private static final String ACTION = "action";
	private static final String ATTRIBUTES = "attributes";

	private static final Logger LOGGER = Logger.getLogger(UtilityMatrix.class
			.getName());

	
	private List<UtilityMatrixPreference> preferences;
	// private QueryManager queryManager;
	private int contProvince;
	private int contMunicipality;
	private int contSchool;

	/**
	 * Costruttore di default per la matrice di utilitï¿½
	 */
	public UtilityMatrix() {
		
		preferences = new ArrayList<UtilityMatrixPreference>();
		// queryManager = new QueryManager();
		// Azzero i conatatori
		contProvince = 0;
		contMunicipality = 0;
		contSchool = 0;
	}

	public List<UtilityMatrixPreference> getPreferences() {
		return preferences;
	}
	

	/**
	 * Calcola il valore da assegnare all'interno della matrice
	 * 
	 * @param score
	 * @param userScore
	 * @param eventType
	 * @return valore calcolato
	 */
	private int computeValue(long eventType) {
		int value = 0;

		if (eventType == 2) {
			value = 0;
		}
		if (eventType == 1) {
			value = 2;
		}
		// vale per i log
		if (eventType == 3) {
			value = 1;
		}
		return value;
	}

	/**
	 * Riempie la lista di preferenze utilizzando i watches
	 * 
	 * @param list
	 */
	public void fillPreferencesWithWatches(List<Document> list) {
		/*
		 * se la lista dei documenti è vuota, non viene riempita la lista di
		 * preferenze
		 */
		if (list.size() < 1) {
			LOGGER.severe("[" + UtilityMatrix.class.getName()
					+ "] You must fill matrix with not empty list");
			return;
		}

		/*
		 * Si effettua un ciclo su ogni documento presente nella lista dei
		 * Watches
		 */
		for (Document doc : list) {

			/*
			 * Si recupera l'id dell'utente e il target del watch
			 */
			long userId = doc.getLong(USER_ID);
			Document target = (Document) doc.get(TARGET);
			long typeId = target.getLong(TYPE_ID);

			int value = 0;
			long eventType = 1;
			switch ((int) typeId) {
			case 1:
				/*
				 * La preferenza è sulle provincia quindi la si recupera dal
				 * documento
				 */
				String province = target.getString(KEY);
				if (province == null)
					break;

				/*
				 * TODO Si calcola il punteggio in base al fatto che il watch
				 * sia aggiunto o rimosso
				 */
				Document ledProvince = (Document) doc.get(LAST_EVENT_DATA);
				if (ledProvince != null) {
					eventType = ledProvince.getLong(EVENT_TYPE);
				}
				value = computeValue(eventType);

				/*
				 * Si aggiunge la preferenza sulla provincia, le si assegna il
				 * tag relativo alle province(1) e il valore calcolato in
				 * precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId, province,
						1, value));
				break;

			case 2:
				/*
				 * La preferenza è sul comune quindi lo si recupera dal
				 * documento
				 */
				String municipality = target.getString(KEY);
				if (municipality == null)
					break;

				/*
				 * TODO Si calcola il punteggio in base al fatto che il watch
				 * sia aggiunto o rimosso
				 */
				Document ledMunicipality = (Document) doc.get(LAST_EVENT_DATA);
				if (ledMunicipality != null) {
					eventType = ledMunicipality.getLong(EVENT_TYPE);
				}
				value = computeValue(eventType);

				/*
				 * Si aggiunge la preferenza sul comune, le si assegna il tag
				 * relativo ai comuni(2) e il valore calcolato in precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId,
						municipality, 2, value));
				break;

			case 3:
				/*
				 * La preferenza è sulle scuola quindi la si recupera dal
				 * documento
				 */
				String school = target.getString(KEY);
				if (school == null)
					break;

				/*
				 * TODO Si calcola il punteggio in base al fatto che il watch
				 * sia aggiunto o rimosso
				 */
				Document ledSchool = (Document) doc.get(LAST_EVENT_DATA);
				if (ledSchool != null) {
					eventType = ledSchool.getLong(EVENT_TYPE);
				}
				value = computeValue(eventType);

				/*
				 * Si aggiunge la preferenza sulla scuola, le si assegna il tag
				 * relativo alle scuole(3) e il valore calcolato in precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId, school, 3,
						value));

				/*
				 * TODO String provinceFromSchool =
				 * queryManager.getProvinceFromSchool(); String
				 * municipalityFromSchool =
				 * queryManager.getMunicipalityFromSchool(); preferences.add(new
				 * UtilityMatrixPreference(userId, provinceFromSchool, 1,
				 * value)); preferences.add(new UtilityMatrixPreference(userId,
				 * municipalityFromSchool, 2, value));
				 */
				break;
			}
		}
	}

	/**
	 * Riempie la lista delle preferenze utilizzando i log recuperati
	 * 
	 * @param list
	 */
	public void fillPreferencesWithLogs(List<Document> list) {
		// se la lista dei documenti è vuota, non viene riempita la matrice di
		// utility
		if (list.size() < 1) {
			System.out.println("You must fill matrix with not empty list");
			return;
		}

		/* Vengono iterati tutti i documenti */
		for (Document doc : list) {
			/* Si recupera l'id dell'utente */
			Long userId = doc.getLong(USER_ID);
			if (userId == null)
				continue;
			/*
			 * Si recupera la tipologia di azione presente nel log in maniera da
			 * effettuare uno switch su di essa
			 */
			Document attributes = (Document) doc.get(ATTRIBUTES);
			String action = doc.getString(ACTION);

			int value = 0;
			switch (action) {
			case "webapi_municipality_aggregates":
				/*
				 * La preferenza è sulle provincia quindi la si recupera dal
				 * documento
				 */
				String province = attributes.getString(CODE_PROVINCE);
				if (province == null)
					break;

				/* Calcola il valore da attribuire alla preferenza */
				value = computeValue(3);

				/*
				 * Si aggiunge la preferenza sulla provincia, le si assegna il
				 * tag relativo alle province(1) e il valore calcolato in
				 * precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId, province,
						1, value));
				break;

			case "webapi_school_aggregates":
				/*
				 * La preferenza è sul comune quindi lo si recupera dal
				 * documento
				 */
				String municipality = attributes.getString(CODE_MUNICIPALITY);
				if (municipality == null)
					break;

				/* Calcola il valore da attribuire alla preferenza */
				value = computeValue(3);

				/*
				 * Si aggiunge la preferenza sul comune, le si assegna il tag
				 * relativo ai comuni(2) e il valore calcolato in precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId,
						municipality, 2, value));
				break;
			case "webapi_get_best_schools":
				/*
				 * La preferenza è sulle provincia quindi la si recupera dal
				 * documento
				 */
				String provinceFromBestSchool = attributes
						.getString(CODE_PROVINCE);
				if (provinceFromBestSchool == null)
					break;

				/* Calcola il valore da attribuire alla preferenza */
				value = computeValue(3);

				/*
				 * Si aggiunge la preferenza sulla provincia, le si assegna il
				 * tag relativo alle province(1) e il valore calcolato in
				 * precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId,
						provinceFromBestSchool, 1, value));
				break;
			}
		}
	}

	public int getContProvince() {
		return contProvince;
	}

	public void setContProvince(int contProvince) {
		this.contProvince = contProvince;
	}

	public int getContMunicipality() {
		return contMunicipality;
	}

	public void setContMunicipality(int contMunicipality) {
		this.contMunicipality = contMunicipality;
	}

	public int getContSchool() {
		return contSchool;
	}

	public void setContSchool(int contSchool) {
		this.contSchool = contSchool;
	}

	/**
	 * Ordina gli elementi all'interno della lista di preferenza in ordine
	 * crescente per tag
	 */
	public void sortForPlacePreferences() {
		/* Istanzia una nuova lista dove verranno ordinati gli elementi */
		List<UtilityMatrixPreference> sortedPreferences = new ArrayList<UtilityMatrixPreference>();
		/*
		 * Il ciclo va da 1 a 3, ovvero i diversi possibili tag che si possono
		 * avere
		 */
		for (int i = 1; i < 4; ++i) {
			for (int j = 0; j < preferences.size(); j++) {
				/*
				 * Per ogni elemento presente all'interno della lista viene
				 * verificato se esso è già presente nella lista delle
				 * preferenze ordinate
				 */
				UtilityMatrixPreference preference = preferences.get(j);
				if (preference.getTag() == i) {
					int present = Utils.isInList(sortedPreferences, preference);
					if (present == -1) {
						/* Se non presente si aggiunge */
						sortedPreferences.add(preference);
					} else {
						/* Se presente si aggiorna il punteggio */
						if(sortedPreferences.get(present).getScore() == preference.getScore())
							continue;
						if(sortedPreferences.get(present).getScore() == 3)
							continue;
						
						int score = sortedPreferences.get(present).getScore()
								+ preference.getScore();
						sortedPreferences.get(present).setScore(score);
					}
				}

			}
			/* Vengono recuperati i contatori delle tipologie di tag */
			if (i == 1) {
				contProvince = sortedPreferences.size();
			}
			if (i == 2) {
				contMunicipality = sortedPreferences.size();
			}
			if (i == 3) {
				contSchool = sortedPreferences.size();
			}
		}
		/*
		 * Si assegna al campo preferences la lista degli elementi ordinati per
		 * tag
		 */
		preferences = sortedPreferences;
	}
}
