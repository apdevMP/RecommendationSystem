package core;

import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;

/**
 * Classe di servizio per creare,modificare e lavorare sulle matrici di utilità
 * 
 */
public class UtilityMatrixCreator {

	private UtilityMatrix utilityMatrix;
	private Profile userProfile;

	private static final Logger LOGGER = Logger
			.getLogger(UtilityMatrixCreator.class.getName());

	/**
	 * Costruttore di default
	 */
	public UtilityMatrixCreator(Profile profile) {
		utilityMatrix = new UtilityMatrix();
		userProfile = profile;
	}

	/**
	 * Restituisce la matrice di utilitï¿½ completa
	 * 
	 * @return
	 */
	public UtilityMatrix getUtilityMatrix() {
		return utilityMatrix;
	}

	/**
	 * Imposta la matrice di utilitï¿½ completa
	 * 
	 * @param umMerge
	 */
	public void setUtilityMatrix(UtilityMatrix utilityMatrix) {
		this.utilityMatrix = utilityMatrix;
	}

	/**
	 * Riempie la lista di preferenze contenuta all'interno della matrice di
	 * utilità e restituisce quest'ultima
	 * 
	 * @param listFromWatch
	 * @param listFromLog
	 * @return
	 */
	public UtilityMatrix fillMatrixPreferences(List<Document> listFromWatch,
			List<Document> listFromLog) {

		LOGGER.info("[" + UtilityMatrixCreator.class.getName()
				+ "] Filling matrix from logs and watches..");

		/*
		 * Riempie la lista di preferenze utilizzando i dati provenienti da log
		 * e watches
		 */
		utilityMatrix.fillPreferencesWithWatches(listFromWatch,
				userProfile.getId());
		utilityMatrix.fillPreferencesWithLogs(listFromLog, userProfile.getId());

		/*
		 * Ordina la lista di preferenze in base alla tipologie del luogo,ovvero
		 * nell'ordine province,comuni,scuole
		 */
		utilityMatrix.sortForPlacePreferences();

		/* Restituisce la matrice di utilità */
		return utilityMatrix;
	}

	public void fillMatrixPreferencesWithPagination(
			List<Document> listFromWatch, List<Document> listFromLog) {

		LOGGER.info("[" + UtilityMatrixCreator.class.getName()
				+ "] Filling matrix from logs and watches..");

		/*
		 * Riempie la lista di preferenze utilizzando i dati provenienti da log
		 * e watches
		 */
		utilityMatrix.fillPreferencesWithWatches(listFromWatch,
				userProfile.getId());
		utilityMatrix.fillPreferencesWithLogs(listFromLog, userProfile.getId());

		
		/*
		 * Ordina la lista di preferenze in base alla tipologie del luogo,ovvero
		 * nell'ordine province,comuni,scuole
		 */
		utilityMatrix.sortForPlacePreferences();
	}

}
