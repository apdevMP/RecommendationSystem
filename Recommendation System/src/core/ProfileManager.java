package core;

import java.util.List;

import org.bson.Document;

import utils.Utils;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;

public class ProfileManager {

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
	private static final String[] actions = { "webapi_school_aggregates",
			"webapi_municipality_aggregates", "webapi_get_best_schools" };

	public static Profile createProfile(long id, String teachingRole,
			double score, String position) {
		Profile profile = new Profile(id, teachingRole, score, position);
		fillListOfUserPreference(profile);

		return profile;
	}

	private static void fillListOfUserPreference(Profile profile) {
		QueryManager queryManager = new QueryManager();

		FindIterable<Document> logByIdLists = queryManager
				.getLogsByActionAndId(profile.getId(), actions);

		FindIterable<Document> watchByIdLists = queryManager
				.getWatchById(profile.getId());

		List<UtilityMatrixPreference> userPreference = profile
				.getUserPreferences();

		fillListWithLogs(profile, logByIdLists, userPreference);
		fillListWithWatches(profile, watchByIdLists, userPreference);
		profile.setUserPreferences(userPreference);
	}

	private static void fillListWithLogs(Profile profile,
			FindIterable<Document> logByIdLists,
			List<UtilityMatrixPreference> userPreference) {

		List<Document> list = Lists.newArrayList(logByIdLists);
		// se la lista dei documenti è vuota, non viene riempita la matrice di
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
				 * La preferenza è sulle provincia quindi la si recupera dal
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
						province, 1, value));
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
				userPreference.add(new UtilityMatrixPreference(userId,
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
				value = Utils.computeValue(3);

				/*
				 * Si aggiunge la preferenza sulla provincia, le si assegna il
				 * tag relativo alle province(1) e il valore calcolato in
				 * precedenza
				 */
				userPreference.add(new UtilityMatrixPreference(userId,
						provinceFromBestSchool, 1, value));
				break;
			}
		}

	}

	private static void fillListWithWatches(Profile profile,
			FindIterable<Document> watchByIdLists,
			List<UtilityMatrixPreference> userPreference) {

		List<Document> list = Lists.newArrayList(watchByIdLists);
		/*
		 * se la lista dei documenti è vuota, non viene riempita la lista di
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
				value = Utils.computeValue(eventType);

				/*
				 * Si aggiunge la preferenza sulla provincia, le si assegna il
				 * tag relativo alle province(1) e il valore calcolato in
				 * precedenza
				 */
				userPreference.add(new UtilityMatrixPreference(userId,
						province, 1, value));
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
				value = Utils.computeValue(eventType);

				/*
				 * Si aggiunge la preferenza sul comune, le si assegna il tag
				 * relativo ai comuni(2) e il valore calcolato in precedenza
				 */
				userPreference.add(new UtilityMatrixPreference(userId,
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
				value = Utils.computeValue(eventType);

				/*
				 * Si aggiunge la preferenza sulla scuola, le si assegna il tag
				 * relativo alle scuole(3) e il valore calcolato in precedenza
				 */
				userPreference.add(new UtilityMatrixPreference(userId, school,
						3, value));

				break;
			}
		}
	}

}
