package core.data;

/**
 * Classe di supporto per l'utilizzo di Mahout in maniera semplificata.
 * Rappresenta le preferenze degli utenti rispetto a province, comuni, scuole
 * 
 */
public class UtilityMatrixPreference {

	private long userId; 		/* Id dell'utente */
	private String placeId; 	/* Id del luogo */
	private int tag; 			/* Può essere di tre tipi: 
	 							 * 1 = province, 
	 							 * 2 = municipality, 
	 							 * 3 = school
	 							 */
	private int score; /* punteggio relativo al luogo */

	/**
	 * Costruttore
	 * 
	 * @param userId
	 * @param placeId
	 * @param tag
	 * @param score
	 */
	public UtilityMatrixPreference(long userId, String placeId, int tag,
			int score) {
		this.userId = userId;
		this.placeId = placeId;
		this.tag = tag;
		this.score = score;

	}

	/**
	 * Restituisce l'id dell'utente
	 * 
	 * @return id dell'utente
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * Imposta l'id dell'utente
	 * 
	 * @param userId
	 *            id utente da impostare
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * Restituisce l'id della località sulla quale si espressa la preferenza
	 * 
	 * @return id della località
	 */
	public String getPlaceId() {
		return placeId;
	}

	/**
	 * Imposta l'id della località sulla quale si espressa la preferenza
	 * 
	 * @param placeId
	 *            id da impostare
	 */
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	/**
	 * Restituisce il tag
	 * 
	 * @return tag della preferenza
	 * 
	 */
	public int getTag() {
		return tag;
	}

	/**
	 * Imposta il tag
	 * 
	 * @param tag
	 *            da impostare
	 */
	public void setTag(int tag) {
		this.tag = tag;
	}

	/**
	 * Restituisce il valore del punteggio della preferenza
	 * 
	 * @return punteggio della preferenza
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Imposta il punteggio della preferenze
	 * 
	 * @param score
	 *            punteggio da settare
	 */
	public void setScore(int score) {
		this.score = score;
	}

}
