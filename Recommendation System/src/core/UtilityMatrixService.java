package core;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

/**
 * Classe di servizio per creare,modificare e lavorare sulle matrici di utilità
 * 
 */
public class UtilityMatrixService {

	private UtilityMatrix umFromWatch;
	private UtilityMatrix umFromLog;
	private UtilityMatrix umMerge;

	/**
	 * Costruttore di default
	 */
	public UtilityMatrixService() {
		umFromWatch = new UtilityMatrix();
		umFromLog = new UtilityMatrix();
		umMerge = new UtilityMatrix();
	}

	/**
	 * Restituisce la matrice di utilità proveniente dai Watches
	 * 
	 * @return
	 */
	public UtilityMatrix getUmFromWatch() {
		return umFromWatch;
	}

	/**
	 * Imposta la matrice di utilità proveniente dai Watches
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
	 * Imposta la matrice di utilità proveniente dai Log
	 * 
	 * @param umFromLog
	 */
	public void setUmFromLog(UtilityMatrix umFromLog) {
		this.umFromLog = umFromLog;
	}

	/**
	 * Restituisce la matrice di utilità completa
	 * 
	 * @return
	 */
	public UtilityMatrix getUmMerge() {
		return umMerge;
	}

	/**
	 * Imposta la matrice di utilità completa
	 * 
	 * @param umMerge
	 */
	public void setUmMerge(UtilityMatrix umMerge) {
		this.umMerge = umMerge;
	}

	/**
	 * Riempie le matrici di utilità relative ai Watches e al log di navigazione
	 * 
	 * @param listFromWatch
	 * @param listFromLog
	 */
	public void fillMatrix(List<Document> listFromWatch,
			List<Document> listFromLog) {

		umFromWatch.fillMatrixWithWatches(listFromWatch);
		umFromWatch.printUtilityMatrix();
		umFromLog.fillMatrixWithLogs(listFromLog);
		umFromLog.printUtilityMatrix();
	}

	public void mergeProvinceList(long userId, boolean isPresent) {

		List<Long> matrixUser = umMerge.getUserMatrix();
		if (isPresent == false) {
			matrixUser.add(userId);
			umMerge.getProvinceValues().add(new ArrayList<Integer>());
			int indexUser = matrixUser.indexOf(userId);
			for (int i = 0; i < umMerge.getProvinceMatrix().size(); i++) {
				umMerge.getProvinceValues().get(indexUser).add(0);
			}
		}

		List<String> provinceToMergeList = umFromLog.getProvinceMatrix();
		int indexUser = matrixUser.indexOf(userId);
		for (String province : provinceToMergeList) {
			if (umMerge.getProvinceMatrix().contains(province)) {

				int valueLog = umFromLog.getValueByUserAndProvince(userId,
						province);
				int valueWatch = umMerge.getValueByUserAndProvince(userId,
						province);
				int maxValue = Utils.getMax(valueLog, valueWatch);
				umMerge.setValueByUserAndProvince(userId, province, maxValue);
			} else {
				int valueLog = umFromLog.getValueByUserAndProvince(userId,
						province);
				umMerge.getProvinceMatrix().add(province);
				umMerge.getProvinceValues().get(indexUser).add(valueLog);
				int cont = 0;
				for (long id : matrixUser) {
					if (id != userId) {
						umMerge.getProvinceValues().get(cont).add(0);
					}
					cont++;
				}

			}
		}
	}

	public void mergeMunicipalityList(long userId, boolean isPresent) {

		List<Long> matrixUser = umMerge.getUserMatrix();
		if (isPresent == false) {

			umMerge.getMunicipalityValues().add(new ArrayList<Integer>());
			int indexUser = matrixUser.indexOf(userId);
			for (int i = 0; i < umMerge.getMunicipalityMatrix().size(); i++) {
				umMerge.getMunicipalityValues().get(indexUser).add(0);
			}
		}

		List<String> municipalityToMergeList = umFromLog
				.getMunicipalityMatrix();
		int indexUser = matrixUser.indexOf(userId);
		for (String municipality : municipalityToMergeList) {
			if (umMerge.getMunicipalityMatrix().contains(municipality)) {

				int valueLog = umFromLog.getValueByUserAndMunicipality(userId,
						municipality);
				int valueWatch = umMerge.getValueByUserAndMunicipality(userId,
						municipality);
				int maxValue = Utils.getMax(valueLog, valueWatch);
				umMerge.setValueByUserAndMunicipality(userId, municipality,
						maxValue);
			} else {
				int valueLog = umFromLog.getValueByUserAndMunicipality(userId,
						municipality);
				umMerge.getMunicipalityMatrix().add(municipality);
				umMerge.getMunicipalityValues().get(indexUser).add(valueLog);
				int cont = 0;
				for (long id : matrixUser) {
					if (id != userId) {
						umMerge.getMunicipalityValues().get(cont).add(0);
					}
					cont++;
				}

			}
		}
	}

	public void mergeSchoolList(long userId, boolean isPresent) {

		List<Long> matrixUser = umMerge.getUserMatrix();
		if (isPresent == false) {

			umMerge.getSchoolValues().add(new ArrayList<Integer>());
			int indexUser = matrixUser.indexOf(userId);
			for (int i = 0; i < umMerge.getSchoolMatrix().size(); i++) {
				umMerge.getSchoolValues().get(indexUser).add(0);
			}
		}

		List<String> schoolToMergeList = umFromLog.getSchoolMatrix();
		int indexUser = matrixUser.indexOf(userId);
		for (String school : schoolToMergeList) {
			if (umMerge.getSchoolMatrix().contains(school)) {

				int valueLog = umFromLog
						.getValueByUserAndSchool(userId, school);
				int valueWatch = umMerge
						.getValueByUserAndSchool(userId, school);
				int maxValue = Utils.getMax(valueLog, valueWatch);
				umMerge.setValueByUserAndSchool(userId, school, maxValue);
			} else {
				int valueLog = umFromLog
						.getValueByUserAndSchool(userId, school);
				umMerge.getSchoolMatrix().add(school);
				umMerge.getSchoolValues().get(indexUser).add(valueLog);
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
	 * Metodo che esegue il merge tra due matrici di utilità
	 */
	public void mergeMatrix() {

		umMerge = umFromWatch;
		List<Long> userToMerge = umFromLog.getUserMatrix();
		List<Long> matrixUser = umMerge.getUserMatrix();
		for (long userId : userToMerge) {

			boolean containsUser = matrixUser.contains(userId);
			mergeProvinceList(userId, containsUser);
			mergeMunicipalityList(userId, containsUser);
			mergeSchoolList(userId, containsUser);
		}
	}

	/**
	 * Metodo che, date le liste di documenti provenienti dai watch e dal log,
	 * resituisce la matrice di utilità generale
	 * 
	 * @param listFromWatch
	 * @param listFromLog
	 * @return Matrice di Utilità completa
	 */
	public UtilityMatrix createUtilityMatrix(List<Document> listFromWatch,
			List<Document> listFromLog) {

		fillMatrix(listFromWatch, listFromLog);
		mergeMatrix();
		return umMerge;
	}

}
