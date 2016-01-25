package core.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import core.service.PersistenceService;
import core.service.profile.Profile;
import utils.Utils;

/**
 * Classe che rappresenta la matrice di utilità ricavata dai dati. Essa si basa
 * su una lista di {@link UtilityMatrixPreference} che viene riempita tramite
 * {@link UtilityMatrixService}
 * 
 */
public class UtilityMatrix {

	private static final String TARGET = "target";
	private static final String TYPE_ID = "typeId";
	private static final String KEY = "key";
	private static final String LAST_EVENT_DATA = "lastEventData";
	private static final String USER_ID = "userId";
	private static final String EVENT_TYPE = "eventType";
	private static final String CODE_MUNICIPALITY = "codeMunicipality";
	private static final String CODE_PROVINCE = "codeProvince";
	private static final String ACTION = "action";
	private static final String ATTRIBUTES = "attributes";
	private static final String ID = "id";

	private static final int PROVINCE_ID = 1;
	private static final int MUNICIPALITY_ID = 2;
	private static final int SCHOOL_ID = 3;

	private static final Logger LOGGER = Logger.getLogger(UtilityMatrix.class
			.getName());

	private List<UtilityMatrixPreference> preferences;
	private int indexProvince;
	private int indexMunicipality;
	private int indexSchool;

	private PersistenceService queryManager;

	/**
	 * Costruttore di default per la matrice di utilità
	 */
	public UtilityMatrix() {
		/*
		 * Si istanzia la lista delle preferenze che verr� riempita richiamando
		 * gli opportuni metodi
		 */
		preferences = new ArrayList<UtilityMatrixPreference>();
		/* Si azzerano i contatori */
		indexProvince = 0;
		indexMunicipality = 0;
		indexSchool = 0;
		queryManager = new PersistenceService();
	}

	/**
	 * Recupera la lista di {@link UtilityMatrixPreference} contenuta nella
	 * matrice di utilità
	 * 
	 * @return lista di {@link UtilityMatrixPreference}
	 */
	public List<UtilityMatrixPreference> getPreferences() {
		return preferences;
	}

	/**
	 * Aggiunge la lista di {@link UtilityMatrixPreference} appartenente a
	 * {@link Profile} a quella presente all'interno della matrice di utility
	 * 
	 * @param userPreferences
	 *            lista di {@link UtilityMatrixPreference} proveniente
	 *            dall'utente
	 */
	public void addProfileListToPreferences(
			List<UtilityMatrixPreference> userPreferences) {
		preferences.addAll(userPreferences);
	}

	/**
	 * 
	 * Restituisce il contatore delle province presenti all'interno della lista
	 * 
	 * @return numero di province presenti
	 */
	public int getIndexProvince() {
		return indexProvince;
	}

	/**
	 * Imposta il contatore delle province presenti all'interno della lista
	 * 
	 * @param indexProvince
	 *            numero di province da settare
	 */
	public void setIndexProvince(int indexProvince) {
		this.indexProvince = indexProvince;
	}

	/**
	 * 
	 * Restituisce l'indice dove finiscono i comuni presenti all'interno della
	 * lista
	 * 
	 * @return indice dei comuni all'interno della lista
	 */
	public int getIndexMunicipality() {
		return indexMunicipality;
	}

	/**
	 * Imposta l'indice dei comuni dove finiscono presenti all'interno della
	 * lista
	 * 
	 * @param indexMunicipality
	 *            indice dei comuni da settare
	 */
	public void setIndexMunicipality(int indexMunicipality) {
		this.indexMunicipality = indexMunicipality;
	}

	/**
	 * 
	 * Restituisce l'indice dove finiscono le scuole presenti all'interno della
	 * lista
	 * 
	 * @return indice delle scuole
	 */
	public int getIndexSchool() {
		return indexSchool;
	}

	/**
	 * Imposta l'indice delle scuole presenti all'interno della lista
	 * 
	 * @param indexSchool
	 *            indice delle scuole da settare
	 */
	public void setIndexSchool(int indexSchool) {
		this.indexSchool = indexSchool;
	}

	/**
	 * Riempie la lista di {@link UtilityMatrixPreference} utilizzando la lista
	 * dei Watch recuperati con {@link PersistenceService}
	 * 
	 * @param list
	 *            lista di documenti dai quali prelevare le informazioni
	 *            necessarie
	 * @param profile
	 *            profilo dell'utente
	 */
	public void fillPreferencesWithWatches(List<Document> list, Profile profile) {
		/*
		 * se la lista dei documenti � vuota, non viene riempita la lista di
		 * preferenze
		 */
		if (list.size() < 1) {
			LOGGER.severe("[" + UtilityMatrix.class.getName()
					+ "] You must fill matrix with not empty list");
			return;
		}

		/*
		 * Si effettua un ciclo su ogni documento presente nella lista dei
		 * Watches per prelevare i dati rilevanti
		 */
		for (Document doc : list) {

			/*
			 * Si recupera l'id dell'utente e si verifica se � uguale a quello
			 * del profilo a cui si sta consigliando
			 */
			long userId = doc.getLong(USER_ID);
			if (userId == profile.getId())
				continue;

			/* Si recupera il target e il tipo di watch */
			Document target = (Document) doc.get(TARGET);
			long typeId = target.getLong(TYPE_ID);

			int value = 0;
			long eventType = 1;
			switch ((int) typeId) {
			case 1:
				/*
				 * La preferenza � sulle provincia quindi la si recupera dal
				 * documento
				 */
				String province = target.getString(KEY);
				if (province == null)
					break;

				/*
				 * Si calcola il punteggio in base al fatto che il watch sia
				 * aggiunto o rimosso
				 */
				Document ledProvince = (Document) doc.get(LAST_EVENT_DATA);
				if (ledProvince != null) {
					eventType = ledProvince.getLong(EVENT_TYPE);
				}

				value = Utils.computeValue(eventType);

				/*
				 * Si aggiunge la preferenza sulla provincia, le si assegna il
				 * tag relativo alle province(1) e il valore calcolato in
				 * precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId, province,
						PROVINCE_ID, value));
				break;

			case 2:
				/*
				 * La preferenza � sul comune quindi lo si recupera dal
				 * documento
				 */
				String municipality = target.getString(KEY);
				if (municipality == null)
					break;

				/*
				 * Si calcola il punteggio in base al fatto che il watch sia
				 * aggiunto o rimosso
				 */
				Document ledMunicipality = (Document) doc.get(LAST_EVENT_DATA);
				if (ledMunicipality != null) {
					eventType = ledMunicipality.getLong(EVENT_TYPE);
				}

				value = Utils.computeValue(eventType);

				/*
				 * Si aggiunge la preferenza sul comune, le si assegna il tag
				 * relativo ai comuni(2) e il valore calcolato in precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId,
						municipality, MUNICIPALITY_ID, value));

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
				 * Si calcola il punteggio in base al fatto che il watch sia
				 * aggiunto o rimosso
				 */
				Document ledSchool = (Document) doc.get(LAST_EVENT_DATA);
				if (ledSchool != null) {
					eventType = ledSchool.getLong(EVENT_TYPE);
				}

				value = Utils.computeValue(eventType);

				/*
				 * Si aggiunge la preferenza sulla scuola, le si assegna il tag
				 * relativo alle scuole(3) e il valore calcolato in precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId, school,
						SCHOOL_ID, value));

				String provinceFromSchool = null;
				String municipalityFromSchool = null;

				try {
					provinceFromSchool = queryManager
							.getProvinceFromSchool(school);
					municipalityFromSchool = queryManager
							.getMunicipalityFromSchool(school);
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, "[" + UtilityMatrix.class.getName()
							+ "] Cannot execute statement.");
				}
				if (provinceFromSchool != null)
					preferences.add(new UtilityMatrixPreference(userId,
							provinceFromSchool, PROVINCE_ID, value));

				if (municipalityFromSchool != null)
					preferences.add(new UtilityMatrixPreference(userId,
							municipalityFromSchool, MUNICIPALITY_ID, value));

				break;
			}
		}
	}

	/**
	 * Riempie la lista di {@link UtilityMatrixPreference} utilizzando la lista
	 * dei documenti provenienti dal Log di navigazione recuperati con
	 * {@link PersistenceService}
	 * 
	 * @param list
	 *            lista di documenti dai quali prelevare le informazioni
	 *            necessarie
	 * @param profile
	 *            profilo dell'utente
	 */
	public void fillPreferencesWithLogs(List<Document> list, Profile profile) {
		/*
		 * se la lista dei documenti è vuota, non viene riempita la matrice di
		 * utility
		 */
		if (list.size() < 1) {
			System.out.println("You must fill matrix with not empty list");
			return;
		}

		/* Vengono iterati tutti i documenti */
		for (Document doc : list) {
			/*
			 * Si recupera l'id dell'utente e si verifica se esso � uguale a
			 * quello del profilo a cui consigliare
			 */
			Long userId = doc.getLong(USER_ID);
			if (userId == null || userId == profile.getId())
				continue;
			/*
			 * Si recupera la tipologia di azione presente nel log in maniera da
			 * effettuare uno switch su di essa
			 */
			String action = doc.getString(ACTION);
			Document attributes = (Document) doc.get(ATTRIBUTES);

			int value = 0;
			switch (action) {
			case "webapi_municipality_aggregates":
				/*
				 * La preferenza � sulle provincia quindi la si recupera dal
				 * documento
				 */
				String province = attributes.getString(CODE_PROVINCE);
				if (province == null)
					break;

				/* Calcola il valore da attribuire alla preferenza */
				value = Utils.computeValue(3);

				/*
				 * Si aggiunge la preferenza sulla provincia, le si assegna il
				 * tag relativo alle province(1) e il valore calcolato in
				 * precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId, province,
						PROVINCE_ID, value));
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
				value = Utils.computeValue(3);

				/*
				 * Si aggiunge la preferenza sul comune, le si assegna il tag
				 * relativo ai comuni(2) e il valore calcolato in precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId,
						municipality, MUNICIPALITY_ID, value));
				break;
			case "webapi_get_best_schools":
				/*
				 * La preferenza è sulla provincia quindi la si recupera dal
				 * documento
				 */
				String provinceFromBestSchool = attributes
						.getString(CODE_PROVINCE);
				if (provinceFromBestSchool == null)
					break;

				/* Calcola il valore da attribuire alla preferenza */
				value = Utils.computeValue(3);

				/*
				 * Si aggiunge la preferenza sulla provincia, le si assegna il
				 * tag relativo alle province(1) e il valore calcolato in
				 * precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId,
						provinceFromBestSchool, PROVINCE_ID, value));
				break;

			case "webapi_get_school_detail":

				String school = attributes.getString(ID);
				if (school == null)
					break;

				/* Calcola il valore da attribuire alla preferenza */
				value = Utils.computeValue(3);

				/*
				 * Si aggiunge la preferenza sulla provincia, le si assegna il
				 * tag relativo alle province(1) e il valore calcolato in
				 * precedenza
				 */
				preferences.add(new UtilityMatrixPreference(userId, school,
						SCHOOL_ID, value));

				/*
				 * Per rendere la matrice meno sparsa si riempiono anche
				 * provincia e comune dove la scuola risiede
				 */
				String provinceFromSchool = null;
				String municipalityFromSchool = null;

				try {
					provinceFromSchool = queryManager
							.getProvinceFromSchool(school);
					municipalityFromSchool = queryManager
							.getMunicipalityFromSchool(school);
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, "[" + UtilityMatrix.class.getName()
							+ "] Cannot execute statement.");
				}
				if (provinceFromSchool != null)
					preferences.add(new UtilityMatrixPreference(userId,
							provinceFromSchool, PROVINCE_ID, value));

				if (municipalityFromSchool != null)
					preferences.add(new UtilityMatrixPreference(userId,
							municipalityFromSchool, MUNICIPALITY_ID, value));

				break;
			}
		}
	}

	/**
	 * Ordina gli elementi all'interno della lista di
	 * {@link UtilityMatrixPreference} in ordine crescente per tag
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
				 * verificato se esso � gi� presente nella lista delle
				 * preferenze ordinate
				 */
				UtilityMatrixPreference preference = preferences.get(j);
				if (preference.getTag() == i) {
					int present = Utils.isInList(sortedPreferences, preference);
					if (present == -1) {
						/* Se non presente si aggiunge */
						if (preference.getScore() == 0) {
							preference.setScore(2);
						}
						sortedPreferences.add(preference);
					} else {
						/* Se presente si aggiorna il punteggio */
						if (sortedPreferences.get(present).getScore() == preference
								.getScore())
							continue;
						if (sortedPreferences.get(present).getScore() == 3)
							continue;

						int score = sortedPreferences.get(present).getScore()
								+ preference.getScore();
						sortedPreferences.get(present).setScore(score);
					}
				}

			}
			/* Vengono recuperati i contatori delle tipologie di tag */
			if (i == 1) {
				indexProvince = sortedPreferences.size();
			}
			if (i == 2) {
				indexMunicipality = sortedPreferences.size();
			}
			if (i == 3) {
				indexSchool = sortedPreferences.size();
			}
		}
		/*
		 * Si assegna al campo preferences la lista degli elementi ordinati per
		 * tag
		 */
		preferences = sortedPreferences;
	}

}
