package core;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import utils.Utils;

/**
 * Classe di servizio per creare,modificare e lavorare sulle matrici di utilità
 * 
 */
public class UtilityMatrixCreator {

	private UtilityMatrix umFromWatch;
	private UtilityMatrix umFromLog;
	private UtilityMatrix umMerge;

	/**
	 * Costruttore di default
	 */
	public UtilityMatrixCreator() {
		umFromWatch = new UtilityMatrix();
		umFromLog = new UtilityMatrix();
		umMerge = new UtilityMatrix();
	}

	/**
	 * Restituisce la matrice di utilitï¿½ proveniente dai Watches
	 * 
	 * @return
	 */
	public UtilityMatrix getUmFromWatch() {
		return umFromWatch;
	}

	/**
	 * Imposta la matrice di utilitï¿½ proveniente dai Watches
	 * 
	 * @param umFromWatch
	 */
	public void setUmFromWatch(UtilityMatrix umFromWatch) {
		this.umFromWatch = umFromWatch;
	}

	/**
	 * Restituisce la matrice di utilità proveniente dai Log
	 * 
	 * @return
	 */
	public UtilityMatrix getUmFromLog() {
		return umFromLog;
	}

	/**
	 * Imposta la matrice di utilitï¿½ proveniente dai Log
	 * 
	 * @param umFromLog
	 */
	public void setUmFromLog(UtilityMatrix umFromLog) {
		this.umFromLog = umFromLog;
	}

	/**
	 * Restituisce la matrice di utilitï¿½ completa
	 * 
	 * @return
	 */
	public UtilityMatrix getUmMerge() {
		return umMerge;
	}

	/**
	 * Imposta la matrice di utilitï¿½ completa
	 * 
	 * @param umMerge
	 */
	public void setUmMerge(UtilityMatrix umMerge) {
		this.umMerge = umMerge;
	}

	/**
	 * Riempie le matrici di utilitï¿½ relative ai Watches e al log di navigazione
	 * 
	 * @param listFromWatch
	 *            lista di watch filtrata
	 * @param listFromLog
	 *            lista di log filtrata
	 */
	public void fillMatrix(List<Document> listFromWatch,
			List<Document> listFromLog) {

		// riempie e stampa la matrice di utilitï¿½ relativa ai watches
		umFromWatch.fillMatrixWithWatches(listFromWatch);
		umFromWatch.printUtilityMatrix();

		// riempie e stampa la matrice di utilitï¿½ relativa al log
		umFromLog.fillMatrixWithLogs(listFromLog);
		umFromLog.printUtilityMatrix();
	}

	/**
	 * Metodo che permette di effettuare il merge tra le liste di province
	 * 
	 * @param userId
	 *            id dell'utente
	 * @param isPresent
	 *            verifica se l'utente sia giï¿½ presente
	 */
	public void mergeProvinceList(long userId, boolean isPresent) {

		// recupera la lista degli utenti
		List<Long> matrixUser = umMerge.getUserMatrix();

		// Se l'utente non ï¿½ presente si aggiunge alla lista
		if (isPresent == false) {
			matrixUser.add(userId);

			// si aggiunge la lista relativa all'utente aggiunto
			umMerge.getProvinceValues().add(new ArrayList<Integer>());

			// si recupera l'indice dell'utente all'interno della lista e si
			// inzializzano i valori relativi alle province della matrice di
			// merge a 0
			int indexUser = matrixUser.indexOf(userId);
			for (int i = 0; i < umMerge.getProvinceMatrix().size(); i++) {
				umMerge.getProvinceValues().get(indexUser).add(0);
			}
		}

		// si recupera la lista delle province dalla matrice di utilitï¿½ relativa
		// al log di navigazione
		List<String> provinceToMergeList = umFromLog.getProvinceMatrix();

		// recupera l'indice dell'utente e inizializza delle variabili di
		// appoggio che conterranno rispettivamente il valore presente nella
		// matrice dei log,il valore presente nella matrice dei watches ed
		// infine il massimo valore tra esi
		int indexUser = matrixUser.indexOf(userId);
		int valueLog, valueWatch, maxValue;

		// Si verifica per ogni provincia presente nella lista se essa sia
		// all'interno della matrice di utilitï¿½ dei watches
		for (String province : provinceToMergeList) {
			// la provincia ï¿½ presente
			if (umMerge.getProvinceMatrix().contains(province)) {

				// si recuperano i valori contenuti nelle matrici di log e
				// watches
				valueLog = umFromLog
						.getValueByUserAndProvince(userId, province);
				valueWatch = umMerge
						.getValueByUserAndProvince(userId, province);
				// si prende il massimo valore tra quelli recuperati e lo si
				// assegna alla matrice di utilitï¿½
				maxValue = Utils.getMax(valueLog, valueWatch);
				umMerge.setValueByUserAndProvince(userId, province, maxValue);
			}
			// la provincia non ï¿½ presente
			else {
				// si recupera il valore dalla matrice di log
				valueLog = umFromLog
						.getValueByUserAndProvince(userId, province);
				// si aggiunge la provincia alla matrice e le si assegna il
				// valore recuperato
				umMerge.getProvinceMatrix().add(province);
				umMerge.getProvinceValues().get(indexUser).add(valueLog);

				int cont = 0;
				// per ogni utente presente nela lista (se diverso da quello che
				// si sta verificando) si assegna 0 alla colonna relativa alla
				// provincia
				for (long id : matrixUser) {
					if (id != userId) {
						umMerge.getProvinceValues().get(cont).add(0);
					}
					cont++;
				}

			}
		}
	}

	/**
	 * Metodo che permette di effettuare il merge tra le liste dei comuni
	 * 
	 * @param userId
	 *            id dell'utente
	 * @param isPresent
	 *            verifica se l'utente sia giï¿½ presente
	 */
	public void mergeMunicipalityList(long userId, boolean isPresent) {

		// recupera la lista degli utenti
		List<Long> matrixUser = umMerge.getUserMatrix();

		if (isPresent == false) {
			// si aggiunge la lista relativa all'utente aggiunto e non presente
			umMerge.getMunicipalityValues().add(new ArrayList<Integer>());

			// si recupera l'indice dell'utente all'interno della lista e si
			// inzializzano i valori relativi ai comuni della matrice di
			// merge a 0
			int indexUser = matrixUser.indexOf(userId);
			for (int i = 0; i < umMerge.getMunicipalityMatrix().size(); i++) {
				umMerge.getMunicipalityValues().get(indexUser).add(0);
			}
		}

		// si recupera la lista dei comuni dalla matrice di utilitï¿½ relativa
		// al log di navigazione
		List<String> municipalityToMergeList = umFromLog
				.getMunicipalityMatrix();

		// recupera l'indice dell'utente e inizializza delle variabili di
		// appoggio che conterranno rispettivamente il valore presente nella
		// matrice dei log,il valore presente neella matrice dei watches ed
		// infine il massimo valore tra esi
		int indexUser = matrixUser.indexOf(userId);
		int valueLog, valueWatch, maxValue;

		// Si verifica per ogni comune presente nella lista, se esso sia
		// all'interno della matrice di utilitï¿½ dei watches
		for (String municipality : municipalityToMergeList) {
			// il comune ï¿½ presente
			if (umMerge.getMunicipalityMatrix().contains(municipality)) {

				// si recuperano i valori contenuti nelle matrici di log e
				// watches
				valueLog = umFromLog.getValueByUserAndMunicipality(userId,
						municipality);
				valueWatch = umMerge.getValueByUserAndMunicipality(userId,
						municipality);
				// si prende il massimo valore tra quelli recuperati e lo si
				// assegna alla matrice di utilitï¿½
				maxValue = Utils.getMax(valueLog, valueWatch);
				umMerge.setValueByUserAndMunicipality(userId, municipality,
						maxValue);
			}
			// il comune non ï¿½ presente
			else {
				// si recupera il valore dalla matrice di log
				valueLog = umFromLog.getValueByUserAndMunicipality(userId,
						municipality);
				// si aggiunge il comune alla matrice e gli si assegna il
				// valore recuperato
				umMerge.getMunicipalityMatrix().add(municipality);
				umMerge.getMunicipalityValues().get(indexUser).add(valueLog);

				int cont = 0;
				// per ogni utente presente nela lista (se diverso da quello che
				// si sta verificando) si assegna 0 alla colonna relativa al
				// comune
				for (long id : matrixUser) {
					if (id != userId) {
						umMerge.getMunicipalityValues().get(cont).add(0);
					}
					cont++;
				}

			}
		}
	}

	/**
	 * Metodo che permette di effettuare il merge tra le liste delle scuole
	 * 
	 * @param userId
	 *            id dell'utente
	 * @param isPresent
	 *            verifica se l'utente sia giï¿½ presente
	 */
	public void mergeSchoolList(long userId, boolean isPresent) {

		// recupera la lista degli utenti
		List<Long> matrixUser = umMerge.getUserMatrix();

		if (isPresent == false) {
			// si aggiunge la lista relativa all'utente aggiunto e non presente
			umMerge.getSchoolValues().add(new ArrayList<Integer>());

			// si recupera l'indice dell'utente all'interno della lista e si
			// inzializzano i valori relativi alle scuole della matrice di
			// merge a 0
			int indexUser = matrixUser.indexOf(userId);
			for (int i = 0; i < umMerge.getSchoolMatrix().size(); i++) {
				umMerge.getSchoolValues().get(indexUser).add(0);
			}
		}

		// si recupera la lista delle scuole dalla matrice di utilitï¿½ relativa
		// al log di navigazione
		List<String> schoolToMergeList = umFromLog.getSchoolMatrix();

		// recupera l'indice dell'utente e inizializza delle variabili di
		// appoggio che conterranno rispettivamente il valore presente nella
		// matrice dei log,il valore presente nella matrice dei watches ed
		// infine il massimo valore tra esi
		int indexUser = matrixUser.indexOf(userId);
		int valueLog, valueWatch, maxValue;

		for (String school : schoolToMergeList) {
			// la scuola ï¿½ presente
			if (umMerge.getSchoolMatrix().contains(school)) {

				// si recuperano i valori contenuti nelle matrici di log e
				// watches
				valueLog = umFromLog.getValueByUserAndSchool(userId, school);
				valueWatch = umMerge.getValueByUserAndSchool(userId, school);

				// si prende il massimo valore tra quelli recuperati e lo si
				// assegna alla matrice di utilitï¿½
				maxValue = Utils.getMax(valueLog, valueWatch);
				umMerge.setValueByUserAndSchool(userId, school, maxValue);
			}
			// la scuola non è presente
			else {
				// si recupera il valore dalla matrice di log
				valueLog = umFromLog.getValueByUserAndSchool(userId, school);
				// si aggiunge la scuola alla matrice e gli si assegna il
				// valore recuperato
				umMerge.getSchoolMatrix().add(school);
				umMerge.getSchoolValues().get(indexUser).add(valueLog);

				// per ogni utente presente nela lista (se diverso da quello che
				// si sta verificando) si assegna 0 alla colonna relativa alla
				// scuola
				int cont = 0;
				for (long id : matrixUser) {
					if (id != userId) {
						umMerge.getSchoolValues().get(cont).add(0);
					}
					cont++;
				}

			}
		}
	}

	/**
	 * Metodo che esegue il merge tra due matrici di utilitï¿½. Il merge viene
	 * eseguito unendo alla matrice dei watches quella proveniente dal log di
	 * navigazione
	 */
	public void mergeMatrix() {

		// viene assegnata la matrice dei watches a quella di merge
		umMerge = umFromWatch;

		// si recuperano le liste degli utenti dellee due matrici
		List<Long> userToMerge = umFromLog.getUserMatrix();
		List<Long> matrixUser = umMerge.getUserMatrix();

		// per ogni utente della matrice di log, si verifica se ï¿½ contenuto
		// all'interno di quella dei watches in maniera da aggiungere la sua
		// riga o meno all'interno della matrice finale
		for (long userId : userToMerge) {

			// verifica se l'utente sia presente
			boolean containsUser = matrixUser.contains(userId);

			// unisco le liste delle province,dei comuni e delle scuole
			mergeProvinceList(userId, containsUser);
			mergeMunicipalityList(userId, containsUser);
			mergeSchoolList(userId, containsUser);
		}
	}

	/**
	 * Metodo che, date le liste di documenti provenienti dai watch e dal log,
	 * resituisce la matrice di utilitï¿½ generale
	 * 
	 * @param listFromWatch
	 * @param listFromLog
	 * @return Matrice di Utilitï¿½ completa
	 */
	public UtilityMatrix createUtilityMatrix(List<Document> listFromWatch,
			List<Document> listFromLog) {
		// riempie le due matrici di utilitï¿½ provenienti da watches e log di
		// navigazione
		fillMatrix(listFromWatch, listFromLog);

		// unisce le due matrici di utilitï¿½ in una sola che viene restituita
		mergeMatrix();
		return umMerge;
	}

}
