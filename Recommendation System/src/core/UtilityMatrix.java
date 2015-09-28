package core;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

/**
 * Questa classe rappresenta la matrice di utilit� ricavata dai dati
 * 
 */
public class UtilityMatrix {

	private static final String TARGET = "target";
	private static final String TYPE_ID = "typeId";
	private static final String KEY = "key";
	private static final String LAST_EVENT_DATA = "lastEventData";
	private static final String USER_ID = "userId";
	private static final String CONTEXT = "context";
	private static final String USER_SCORE = "userScore";
	private static final String EVENT_TYPE = "eventType";
	private static final String SCORE = "score";
	private static final String CODE_MUNICIPALITY = "codeMunicipality";
	private static final String CODE_PROVINCE = "codeProvince";
	private static final String ACTION = "action";
	private static final String ATTRIBUTES = "attributes";

	private List<Long> matrixUser;
	private List<String> matrixProvince;
	private List<String> matrixMunicipality;
	private List<String> matrixSchool;
	private ArrayList<ArrayList<Integer>> values;
	private int placeSize;

	/**
	 * Costruttore di default per la matrice di utilit�
	 */
	public UtilityMatrix() {
		matrixUser = new ArrayList<Long>();
		matrixProvince = new ArrayList<String>();
		matrixMunicipality = new ArrayList<String>();
		matrixSchool = new ArrayList<String>();
		values = new ArrayList<ArrayList<Integer>>();
	}

	/**
	 * Aggiunge un utente, se non presente, alla matrice degli utenti
	 * 
	 * @param doc
	 * 
	 */
	private void addUser(Document doc) {
		long userId = doc.getLong(USER_ID);
		if (!matrixUser.contains(userId)) {
			matrixUser.add(userId);
		}
	}

	/**
	 * Aggiunge una provincia proveniente dai log alla relativa matrice
	 * 
	 * @param doc
	 */
	private void addProvinceFromLog(Document doc) {
		String province = doc.getString(CODE_PROVINCE);
		if (!matrixProvince.contains(province)) {
			matrixProvince.add(province);
		}
	}

	/**
	 * Aggiunge una provincia proveniente dai Watch alla relativa matrice
	 * 
	 * @param doc
	 */
	private void addProvinceFromWatch(Document doc) {
		String province = doc.getString(KEY);
		if (!matrixProvince.contains(province)) {
			matrixProvince.add(province);
		}
	}

	/**
	 * Aggiunge un comune proveniente dai log alla relativa matrice
	 * 
	 * @param doc
	 */
	private void addMunicipalityFromLog(Document doc) {
		String municipality = doc.getString(CODE_MUNICIPALITY);
		if (!matrixMunicipality.contains(municipality)) {
			matrixMunicipality.add(municipality);
		}
	}

	/**
	 * Aggiunge un comune proveniente dai Watch alla relativa matrice
	 * 
	 * @param doc
	 */
	private void addMunicipalityFromWatch(Document doc) {
		String municipality = doc.getString(KEY);
		if (!matrixMunicipality.contains(municipality)) {
			matrixMunicipality.add(municipality);
		}
	}

	/**
	 * Aggiunge una scuola alla relativa matrice
	 * 
	 * @param doc
	 */
	private void addSchool(Document doc) {
		String school = doc.getString(KEY);
		if (!matrixSchool.contains(school)) {
			matrixSchool.add(school);
		}
	}

	/**
	 * Inizializza la matrice dei valori a 0
	 */
	private void initializeValues() {
		// Si ricava la grandezza della matrice di utilit�, data dalla somma
		// delle grandezze delle singole matrici
		placeSize = matrixMunicipality.size() + matrixProvince.size()
				+ matrixSchool.size();

		for (ArrayList<Integer> it : values) {
			for (int i = 0; i < placeSize; i++) {
				it.add(0);
			}
		}
	}

	/**
	 * Calcola il valore da assegnare all'interno della matrice
	 * 
	 * @param score
	 * @param userScore
	 * @param eventType
	 * @return valore calcolato
	 */
	private int computeValue(int score, int userScore, long eventType) {
		int value = 0;

		// se il punteggio � maggiore di quello dell'utente, allora viene
		// impostato il valore ad 1
		if (userScore > score + 5) {
			value = 1;
			// si aggiunge una unit� se il watch viene rimosso
			if (eventType == 2) {
				value++;
			}
		}

		// se il punteggio si trova all'interno di un determinato intervallo,
		// allora viene impostato 2
		if (userScore <= score + 5 && userScore >= score - 5) {
			value = 2;
		}

		// se il punteggio � inferiore a quello dell'utente, allora viene
		// impostato 3
		if (userScore <= score - 5)
			value = 3;
		return value;
	}

	/**
	 * Riempie la matrice di utilit� con i dati recuperati dai Watches
	 * 
	 * @param list
	 *            lista dei documenti filtrati per regione e per tipologia di
	 *            materia insegnata
	 * @param score
	 *            punteggio dell'utente al quale suggerire luoghi
	 */
	public void fillMatrixWithWatches(ArrayList<Document> list, int score) {
		// se la lista dei documenti � vuota, non viene riempita la matrice di
		// utilit�
		if (list.size() < 1) {
			System.out.println("You must fill matrix with not empty list");
		}

		for (Document doc : list) {
			// Vengono aggiunti gli utenti, se non presenti, e gli array di
			// interi relativi ai valori da assegnare
			this.addUser(doc);
			values.add(new ArrayList<Integer>());

			// Si recupera il tipo di watch eseguito:
			// 1 : Provincia
			// 2 : Comune
			// 3 : Scuola
			// Viene poi aggiunto il luogo del watch alla relativa matrice
			Document target = (Document) doc.get(TARGET);
			long typeId = target.getLong(TYPE_ID);
			System.out.println(typeId);
			switch ((int) typeId) {
			case 1:
				this.addProvinceFromWatch(target);
				break;

			case 2:
				this.addMunicipalityFromWatch(target);
				break;

			case 3:
				this.addSchool(target);
				break;
			}

		}

		// vengono inizializzati i valori della matrice
		this.initializeValues();

		for (Document doc : list) {
			// Si recupera l'indice dell'array relativo all'utente fornito dal
			// documento e la tipologia del watch
			long userId = doc.getLong(USER_ID);
			int indexUser = matrixUser.indexOf(userId);
			Document target = (Document) doc.get(TARGET);
			long typeId = target.getLong(TYPE_ID);

			int value = 0;
			switch ((int) typeId) {
			case 1:
				// La preferenza � sulla provincia, quindi si recupera l'indice
				// dalla matrice delle province assieme al valore del punteggio
				// dell'utente ed infine si calcola il valore da assegnare
				String province = target.getString(KEY);
				int indexProvince = matrixProvince.indexOf(province);
				Document ledProvince = (Document) doc.get(LAST_EVENT_DATA);
				Document ctxProvince = (Document) ledProvince.get(CONTEXT);
				int usProvince = ctxProvince.getInteger(USER_SCORE);
				value = computeValue(score, usProvince,
						ledProvince.getLong(EVENT_TYPE));

				values.get(indexUser).set(indexProvince, value);
				break;

			case 2:
				// La preferenza � sul comune, quindi si recupera l'indice
				// dalla matrice del comune assieme al valore del punteggio
				// dell'utente ed infine si calcola il valore da assegnare
				String municipality = target.getString(KEY);
				int indexMunicipality = matrixMunicipality
						.indexOf(municipality);
				Document ledMunicipality = (Document) doc.get(LAST_EVENT_DATA);
				Document ctxMunicipality = (Document) ledMunicipality
						.get(CONTEXT);
				int usMunicipality = ctxMunicipality.getInteger(USER_SCORE);
				value = computeValue(score, usMunicipality,
						ledMunicipality.getLong(EVENT_TYPE));

				values.get(indexUser).set(
						indexMunicipality + matrixProvince.size(), value);
				break;

			case 3:
				// La preferenza � sulla scuola, quindi si recupera l'indice
				// dalla matrice delle scuole assieme al valore del punteggio
				// dell'utente ed infine si calcola il valore da assegnare
				String school = target.getString(KEY);
				int indexSchool = matrixSchool.indexOf(school);
				Document ledSchool = (Document) doc.get(LAST_EVENT_DATA);
				Document ctxSchool = (Document) ledSchool.get(CONTEXT);
				int usSchool = ctxSchool.getInteger(USER_SCORE);
				value = computeValue(score, usSchool,
						ledSchool.getLong(EVENT_TYPE));

				values.get(indexUser).set(
						indexSchool + matrixProvince.size()
								+ matrixMunicipality.size(), value);
				break;
			}
		}
	}

	/**
	 * Riempie la matrice di utilit� con i dati recuperati dal log
	 * 
	 * @param list
	 *            lista dei documenti filtrati per regione e per tipologia di
	 *            materia insegnata
	 * @param score
	 *            punteggio dell'utente al quale suggerire luoghi
	 */
	public void fillMatrixWithLogs(ArrayList<Document> list, int score) {
		// se la lista dei documenti � vuota, non viene riempita la matrice di
		// utilit�
		if (list.size() < 1) {
			System.out.println("You must fill matrix with not empty list");
		}

		for (Document doc : list) {
			// Vengono aggiunti gli utenti, se non presenti, e gli array di
			// interi relativi ai valori da assegnare
			this.addUser(doc);
			values.add(new ArrayList<Integer>());

			// Si recupera il tipo di log eseguito dal campo "action":
			// webapi_municipality_aggregates : Provincia
			// webapi_school_aggregates : Comune
			// Viene poi aggiunto il luogo del log alla relativa matrice
			Document attributes = (Document) doc.get(ATTRIBUTES);
			String action = doc.getString(ACTION);
			switch (action) {
			case "webapi_municipality_aggregates":
				this.addProvinceFromLog(attributes);
				break;

			case "webapi_school_aggregates":
				this.addMunicipalityFromLog(attributes);
				break;
			}

		}

		// vengono inizializzati i valori della matrice
		this.initializeValues();

		for (Document doc : list) {
			// Si recupera l'indice dell'array relativo all'utente fornito dal
			// documento e la tipologia di azione del log
			long userId = doc.getLong(USER_ID);
			int indexUser = matrixUser.indexOf(userId);
			Document attributes = (Document) doc.get(ATTRIBUTES);
			String action = doc.getString(ACTION);

			int value = 0;
			switch (action) {
			case "webapi_municipality_aggregates":
				// La preferenza � sulla provincia, quindi si recupera l'indice
				// dalla matrice delle province assieme al valore del punteggio
				// dell'utente ed infine si calcola il valore da assegnare
				String province = attributes.getString(CODE_PROVINCE);
				int indexProvince = matrixProvince.indexOf(province);
				long usProvince = attributes.getLong(SCORE);

				value = computeValue(score, (int) usProvince, 1);

				values.get(indexUser).set(indexProvince, value);
				break;

			case "webapi_school_aggregates":
				// La preferenza � sul comune, quindi si recupera l'indice
				// dalla matrice del comune assieme al valore del punteggio
				// dell'utente ed infine si calcola il valore da assegnare
				String municipality = attributes.getString(CODE_MUNICIPALITY);
				int indexMunicipality = matrixMunicipality
						.indexOf(municipality);
				int usMunicipality = attributes.getInteger(SCORE);
				value = computeValue(score, usMunicipality, 1);

				values.get(indexUser).set(
						indexMunicipality + matrixProvince.size(), value);
				break;
			}
		}
	}

	/**
	 * Stampa la matrice di utilit�
	 */
	public void printUtilityMatrix() {
		int i = 0;
		for (Long userId : matrixUser) {
			System.out.println("[User:" + userId + "]");
			int j = 0;
			for (String province : matrixProvince) {
				System.out.println("Province:" + province + " Value: ["
						+ values.get(i).get(j) + "]");
				j++;
			}
			for (String municipality : matrixMunicipality) {
				System.out.println("Municipality:" + municipality + " Value: ["
						+ values.get(i).get(j) + "]");
				j++;
			}
			for (String school : matrixSchool) {
				System.out.println("School:" + school + " Value: ["
						+ values.get(i).get(j) + "]");
				j++;
			}
			i++;
		}
	}
}
