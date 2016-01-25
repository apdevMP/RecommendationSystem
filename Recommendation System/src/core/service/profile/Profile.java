package core.service.profile;

import java.util.ArrayList;
import java.util.List;

import core.data.UtilityMatrixPreference;

/**
 * Classe per rappresentare il profilo dell'utente
 * 
 * @author apdev
 * 
 */
public class Profile {
	private long id;
	private String teachingRole;
	private double score;
	private String position;
	private List<UtilityMatrixPreference> userPreferences;

	/**
	 * Costruttore di default istanziabile con un factory da
	 * {@link ProfileService}
	 * 
	 * @param id
	 * @param teachingRole
	 * @param score
	 * @param position
	 */
	protected Profile(long id, String teachingRole, double score,
			String position) {
		this.id = id;
		this.teachingRole = teachingRole;
		this.score = score;
		this.position = position;
		this.userPreferences = new ArrayList<UtilityMatrixPreference>();
	}

	/**
	 * Restituisce l'id dell'utente
	 * 
	 * @return id dell'utente
	 */
	public long getId() {
		return id;
	}

	/**
	 * Imposta {@code id} come id del profilo
	 * 
	 * @param id
	 *            id utente
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Recupera la materia insegnata dall'utente
	 * 
	 * @return materia insegnata
	 */
	public String getTeachingRole() {
		return teachingRole;
	}

	/**
	 * Recupera la regione di provenienza dell'utente
	 * 
	 * @return regione di provenienza
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * Imposta {@code position} come regione di provenienza dell'utente
	 * 
	 * @param position
	 *            regione da impostare
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * Recupera il punteggio dell'utente nella graduatoria nazionale
	 * 
	 * @return punteggio dell'utente
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * Recupera la lista di {@link UtilityMatrixPreference} dell'utente
	 * 
	 * @return la lista di preferenze dell'utente
	 */
	public List<UtilityMatrixPreference> getUserPreferences() {
		return userPreferences;
	}

	/**
	 * Imposta {code userPreferences} come lista di {link
	 * UtilityMatrixPreference} preferite dall'utente
	 * 
	 * @param userPreferences
	 *            lista da impostare
	 */
	public void setUserPreferences(List<UtilityMatrixPreference> userPreferences) {
		this.userPreferences = userPreferences;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Profile [id=" + id + ", teachingRole=" + teachingRole
				+ ", score=" + score + ", position=" + position + "]";
	}

}
