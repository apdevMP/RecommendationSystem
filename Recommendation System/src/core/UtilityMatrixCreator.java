package core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import utils.Utils;

/**
 * Classe di servizio per creare,modificare e lavorare sulle matrici di utilit�
 * 
 */
public class UtilityMatrixCreator {

	private UtilityMatrix umFromWatch;
	private UtilityMatrix umFromLog;
	private UtilityMatrix umMerge;
	
	private static final Logger LOGGER = Logger.getLogger(UtilityMatrixCreator.class.getName());


	/**
	 * Costruttore di default
	 */
	public UtilityMatrixCreator() {
		umFromWatch = new UtilityMatrix();
		umFromLog = new UtilityMatrix();
		umMerge = new UtilityMatrix();
	}

	/**
	 * Restituisce la matrice di utilit� proveniente dai Watches
	 * 
	 * @return
	 */
	public UtilityMatrix getUmFromWatch() {
		return umFromWatch;
	}

	/**
	 * Imposta la matrice di utilit� proveniente dai Watches
	 * 
	 * @param umFromWatch
	 */
	public void setUmFromWatch(UtilityMatrix umFromWatch) {
		this.umFromWatch = umFromWatch;
	}

	/**
	 * Restituisce la matrice di utilit� proveniente dai Log
	 * 
	 * @return
	 */
	public UtilityMatrix getUmFromLog() {
		return umFromLog;
	}

	/**
	 * Imposta la matrice di utilit� proveniente dai Log
	 * 
	 * @param umFromLog
	 */
	public void setUmFromLog(UtilityMatrix umFromLog) {
		this.umFromLog = umFromLog;
	}

	/**
	 * Restituisce la matrice di utilit� completa
	 * 
	 * @return
	 */
	public UtilityMatrix getUmMerge() {
		return umMerge;
	}

	/**
	 * Imposta la matrice di utilit� completa
	 * 
	 * @param umMerge
	 */
	public void setUmMerge(UtilityMatrix umMerge) {
		this.umMerge = umMerge;
	}

	/**
	 * Riempie le matrici di utilit� relative ai Watches e al log di navigazione
	 * 
	 * @param listFromWatch
	 *            lista di watch filtrata
	 * @param listFromLog
	 *            lista di log filtrata
	 */
	public void fillMatrix(List<Document> listFromWatch,
			List<Document> listFromLog) {

		// riempie e stampa la matrice di utilit� relativa ai watches
		umFromWatch.fillMatrixWithWatches(listFromWatch);
	//	umFromWatch.printUtilityMatrix();

		// riempie e stampa la matrice di utilit� relativa al log
		umFromLog.fillMatrixWithLogs(listFromLog);
	//	umFromLog.printUtilityMatrix();
	}

	/**
	 * Metodo che permette di effettuare il merge tra le liste di province
	 * 
	 * @param userId
	 *            id dell'utente
	 * @param isPresent
	 *            verifica se l'utente sia gi� presente
	 */
	public void mergeProvinceList(long userId, boolean isPresent) {

		
		// recupera la lista degli utenti
		List<Long> matrixUser = umMerge.getUserMatrix();

		// Se l'utente non � presente si aggiunge alla lista
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

		// si recupera la lista delle province dalla matrice di utilit� relativa
		// al log di navigazione
		List<String> provinceToMergeList = umFromLog.getProvinceMatrix();

		// recupera l'indice dell'utente e inizializza delle variabili di
		// appoggio che conterranno rispettivamente il valore presente nella
		// matrice dei log,il valore presente nella matrice dei watches ed
		// infine il massimo valore tra esi
		int indexUser = matrixUser.indexOf(userId);
		int valueLog, valueWatch, maxValue;

		// Si verifica per ogni provincia presente nella lista se essa sia
		// all'interno della matrice di utilit� dei watches
		for (String province : provinceToMergeList) {
			// la provincia � presente
			if (umMerge.getProvinceMatrix().contains(province)) {

				// si recuperano i valori contenuti nelle matrici di log e
				// watches
				valueLog = umFromLog
						.getValueByUserAndProvince(userId, province);
				valueWatch = umMerge
						.getValueByUserAndProvince(userId, province);
				// si prende il massimo valore tra quelli recuperati e lo si
				// assegna alla matrice di utilit�
				//TODO
				//maxValue = Utils.getMax(valueLog, valueWatch);
				maxValue = valueLog+valueWatch;
				umMerge.setValueByUserAndProvince(userId, province, maxValue);
			}
			// la provincia non � presente
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
	 *            verifica se l'utente sia gi� presente
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

		// si recupera la lista dei comuni dalla matrice di utilit� relativa
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
		// all'interno della matrice di utilit� dei watches
		for (String municipality : municipalityToMergeList) {
			// il comune � presente
			if (umMerge.getMunicipalityMatrix().contains(municipality)) {

				// si recuperano i valori contenuti nelle matrici di log e
				// watches
				valueLog = umFromLog.getValueByUserAndMunicipality(userId,
						municipality);
				valueWatch = umMerge.getValueByUserAndMunicipality(userId,
						municipality);
				// si prende il massimo valore tra quelli recuperati e lo si
				// assegna alla matrice di utilit�
				//TODO
				maxValue = valueLog+valueWatch;
				//maxValue = Utils.getMax(valueLog, valueWatch);
				umMerge.setValueByUserAndMunicipality(userId, municipality,
						maxValue);
			}
			// il comune non � presente
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
	 *            verifica se l'utente sia gi� presente
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

		// si recupera la lista delle scuole dalla matrice di utilit� relativa
		// al log di navigazione
		List<String> schoolToMergeList = umFromLog.getSchoolMatrix();

		// recupera l'indice dell'utente e inizializza delle variabili di
		// appoggio che conterranno rispettivamente il valore presente nella
		// matrice dei log,il valore presente nella matrice dei watches ed
		// infine il massimo valore tra esi
		int indexUser = matrixUser.indexOf(userId);
		int valueLog, valueWatch, maxValue;

		for (String school : schoolToMergeList) {
			// la scuola � presente
			if (umMerge.getSchoolMatrix().contains(school)) {

				// si recuperano i valori contenuti nelle matrici di log e
				// watches
				valueLog = umFromLog.getValueByUserAndSchool(userId, school);
				valueWatch = umMerge.getValueByUserAndSchool(userId, school);

				// si prende il massimo valore tra quelli recuperati e lo si
				// assegna alla matrice di utilit�
				
				//TODO
				maxValue = valueLog+valueWatch;
			//	maxValue = Utils.getMax(valueLog, valueWatch);
				umMerge.setValueByUserAndSchool(userId, school, maxValue);
			}
			// la scuola non � presente
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
	 * Metodo che esegue il merge tra due matrici di utilit�. Il merge viene
	 * eseguito unendo alla matrice dei watches quella proveniente dal log di
	 * navigazione
	 */
	public void mergeMatrix() {

		// viene assegnata la matrice dei watches a quella di merge
		umMerge = umFromWatch;

		// si recuperano le liste degli utenti delle due matrici
		List<Long> userToMerge = umFromLog.getUserMatrix();
		List<Long> matrixUser = umMerge.getUserMatrix();

		// per ogni utente della matrice di log, si verifica se � contenuto
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
		
		LOGGER.info("["+UtilityMatrixCreator.class.getName()+"] Utility matrix created");
	}

	/**
	 * Metodo che, date le liste di documenti provenienti dai watch e dal log,
	 * resituisce la matrice di utilit� generale
	 * 
	 * @param listFromWatch
	 * @param listFromLog
	 * @return Matrice di Utilit� completa
	 */
	public UtilityMatrix createUtilityMatrix(List<Document> listFromWatch,
			List<Document> listFromLog) {
		// riempie le due matrici di utilit� provenienti da watches e log di
		// navigazione
		LOGGER.info("["+UtilityMatrixCreator.class.getName()+"] Filling matrix from logs and watches..");
	//	System.out.println("Filling matrix from logs and watches..");
		fillMatrix(listFromWatch, listFromLog);

		// unisce le due matrici di utilit� in una sola che viene restituita
		LOGGER.info("["+UtilityMatrixCreator.class.getName()+"] Merging matrixes..");
	//	System.out.println("Merging matrixes..");
		mergeMatrix();
		return umMerge;
	}

}
