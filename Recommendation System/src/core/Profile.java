package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe per rappresentare il profilo
 * 
 */
public class Profile {
	private long id;
	private String teachingRole;
	private double score;
	private String position;
	private List<UtilityMatrixPreference> userPreferences;

	public Profile(long id, String teachingRole, double score, String position) {
		this.id = id;
		this.teachingRole = teachingRole;
		this.score = score;
		this.position = position;
		this.userPreferences = new ArrayList<UtilityMatrixPreference>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTeachingRole() {
		return teachingRole;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Double getScore() {
		return score;
	}

	public List<UtilityMatrixPreference> getUserPreferences() {
		return userPreferences;
	}

	public void setUserPreferences(List<UtilityMatrixPreference> userPreferences) {
		this.userPreferences = userPreferences;
	}

	public static Profile createProfile(long id, String teachingRole,
			double score, String position) {
		Profile profile = new Profile(id, teachingRole, score, position);

		return profile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Profile [id=" + id + ", teachingRole=" + teachingRole
				+ ", score=" + score + "]";
	}

}
