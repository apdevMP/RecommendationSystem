package core.data;

/**
 * Classe di supporto per l'utilizzo di Mahout in maniera semplificata.
 * Rappresenta le preferenze degli utenti rispetto a province, comuni, scuole
 * 
 * 
 */
public class UtilityMatrixPreference {

	private long userId; // Id dell'utente
	private String placeId; // Id del luogo
	private int tag; // Pu� essere di tre tipi:
						// 1: province
						// 2: municipality
						// 3: school
	private int score; // punteggio relativo al luogo
	private int bonus;
	/**
	 * Costruttore
	 * 
	 * @param userId
	 * @param placeId
	 * @param tag
	 * @param score
	 */
	public UtilityMatrixPreference(long userId, String placeId, int tag,
			int score,int bonus) {
		this.userId = userId;
		this.placeId = placeId;
		this.tag = tag;
		this.score = score;
		this.setBonus(bonus);
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
	
	public void addBonus(){
		score = score + bonus;
	}

}
