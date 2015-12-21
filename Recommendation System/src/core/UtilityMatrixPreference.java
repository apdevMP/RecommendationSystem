package core;

/**
 * Classe di supporto per l'utilizzo di Mahout in maniera semplificata
 * 
 * @author Andrea
 * 
 */
public class UtilityMatrixPreference {

	private long userId;
	private String placeId;
	private int tag;    // 1: province
						// 2: municipality
						// 3: school
	private int score;

	public UtilityMatrixPreference(long userId, String placeId, int tag,
			int score) {
		this.userId = userId;
		this.placeId = placeId;
		this.tag = tag;
		this.score = score;
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
}
