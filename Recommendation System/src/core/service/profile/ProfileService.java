package core.service.profile;

import java.sql.SQLException;
import java.util.List;

import org.bson.Document;

import utils.Utils;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;

import core.data.UtilityMatrixPreference;
import core.service.PersistenceService;

/**
 * Classe per la gestione e la creazione della classe {@link Profile}
 * 
 * @author Andrea
 * 
 */
public class ProfileService {

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

	private static final String[] actions = { "webapi_school_aggregates",
			"webapi_municipality_aggregates", "webapi_get_best_schools",
			"webapi_get_school_detail" };

	private static final int PROVINCE_ID = 1;
	private static final int MUNICIPALITY_ID = 2;
	private static final int SCHOOL_ID = 3;

	private static final int BONUS = 1;
	/**
	 * Metodo statico per la creazione del profilo
	 * 
	 * @param id
	 * @param teachingRole
	 * @param score
	 * @param position
	 * @return
	 */
	public static Profile createProfile(long id, String teachingRole,
			double score, String position) {

		/* Istanzia il profilo passandogli gli opportuni parametri */
		Profile profile = new Profile(id, teachingRole, score, position);
		/* Riempie la lista di preferenze */
		fillListOfUserPreference(profile);

		/* Restituisce il profilo */
		return profile;
	}

	/**
	 * Riempie la lista di preferenze del profilo
	 * 
	 * @param profile
	 */
	private static void fillListOfUserPreference(Profile profile) {
		/* Si istanzia il QueryManager e si richiamano gli opportuni metodi */
		PersistenceService queryManager = new PersistenceService();

		/* Recupera la lista di log in base all'id dell'utente */
		FindIterable<Document> logByIdLists = queryManager
				.getLogsByActionAndId(profile.getId(), actions);
		/* Recupera la lista dei Watch in base all'id dell'utente */
		FindIterable<Document> watchByIdLists = queryManager
				.getWatchById(profile.getId());

		List<UtilityMatrixPreference> userPreference = profile
				.getUserPreferences();

		/* Si riempie la lista */
		fillListWithLogs(profile, logByIdLists, userPreference);
		fillListWithWatches(profile, watchByIdLists, userPreference);
		profile.setUserPreferences(userPreference);
	}

	/**
	 * Riempie la lista del profilo utente con le preferenze dei Watch
	 * 
	 * @param profile
	 * @param logByIdLists
	 * @param userPreference
	 */
	private static void fillListWithLogs(Profile profile,
			FindIterable<Document> logByIdLists,
			List<UtilityMatrixPreference> userPreference) {

		List<Document> list = Lists.newArrayList(logByIdLists);
		// se la lista dei documenti � vuota, non viene riempita la matrice di
		// utility
		if (list.size() < 1) {
			return;
		}

		/* Vengono iterati tutti i documenti */
		for (Document doc : list) {

			/* Si recupera l'id dell'utente */
			Long userId = doc.getLong(USER_ID);
			if (userId != profile.getId() || userId == null)
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
				userPreference.add(new UtilityMatrixPreference(userId,
						province, PROVINCE_ID, value,BONUS));
				break;

			case "webapi_school_aggregates":
				/*
				 * La preferenza � sul comune quindi lo si recupera dal
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
				userPreference.add(new UtilityMatrixPreference(userId,
						municipality, MUNICIPALITY_ID, value,BONUS));
				break;
			case "webapi_get_best_schools":
				/*
				 * La preferenza � sulle provincia quindi la si recupera dal
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
				userPreference.add(new UtilityMatrixPreference(userId,
						provinceFromBestSchool, PROVINCE_ID, value,BONUS));
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
				userPreference.add(new UtilityMatrixPreference(userId, school,
						SCHOOL_ID, value,BONUS));
				PersistenceService service = new PersistenceService();
				String provinceFromSchool = null;
				String municipalityFromSchool = null;

				try {
					provinceFromSchool = service.getProvinceFromSchool(school);
					municipalityFromSchool = service
							.getMunicipalityFromSchool(school);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (provinceFromSchool != null)
					userPreference.add(new UtilityMatrixPreference(userId,
							provinceFromSchool, PROVINCE_ID, value,BONUS));

				if (municipalityFromSchool != null)
					userPreference.add(new UtilityMatrixPreference(userId,
							municipalityFromSchool, MUNICIPALITY_ID, value,BONUS));

				break;
			}

		}

	}

	/**
	 * Riempie la lista del profilo utente con le preferenze dei Watch
	 * 
	 * @param profile
	 * @param watchByIdLists
	 * @param userPreference
	 */
	private static void fillListWithWatches(Profile profile,
			FindIterable<Document> watchByIdLists,
			List<UtilityMatrixPreference> userPreference) {

		List<Document> list = Lists.newArrayList(watchByIdLists);
		/*
		 * se la lista dei documenti � vuota, non viene riempita la lista di
		 * preferenze
		 */
		if (list.size() < 1) {
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
				userPreference.add(new UtilityMatrixPreference(userId,
						province, PROVINCE_ID, value,BONUS));
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
				userPreference.add(new UtilityMatrixPreference(userId,
						municipality, MUNICIPALITY_ID, value,BONUS));
				break;

			case 3:
				/*
				 * La preferenza � sulle scuola quindi la si recupera dal
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
				userPreference.add(new UtilityMatrixPreference(userId, school,
						SCHOOL_ID, value,BONUS));
				PersistenceService service = new PersistenceService();

				String provinceFromSchool = null;
				String municipalityFromSchool = null;

				try {
					provinceFromSchool = service.getProvinceFromSchool(school);
					municipalityFromSchool = service
							.getMunicipalityFromSchool(school);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (provinceFromSchool != null)
					userPreference.add(new UtilityMatrixPreference(userId,
							provinceFromSchool, PROVINCE_ID, value,BONUS));

				if (municipalityFromSchool != null)
					userPreference.add(new UtilityMatrixPreference(userId,
							municipalityFromSchool, MUNICIPALITY_ID, value,BONUS));

				break;
			}
		}
	}

}
