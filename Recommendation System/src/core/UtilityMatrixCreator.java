package core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;

public class UtilityMatrixCreator {

	private Profile userProfile;
	private String region;

	public UtilityMatrixCreator() {

	}

	public UtilityMatrixCreator(Profile profile, String region) {
		this.userProfile = profile;
		this.region = region;
	}

	public UtilityMatrix createUtilityMatrix() {
		// istanzio un queryManager per recuperare dati dalle collezioni
		QueryManager queryManager = new QueryManager();

		// si recupera la materia di insegnamento dell'utente
		String teachingRole = userProfile.getTeachingRole();

		// si recupera la lista di watch e di log in base alla materia di
		// insegnamento
		FindIterable<Document> itWatches = queryManager
				.findWatchesByTeachingRole(teachingRole);
		FindIterable<Document> itLogs = queryManager
				.findLogsByTeachingRole(teachingRole);

		// le liste di watch e log vengono filtrate per regione
		ArrayList<Document> listWatches = Filter.filterWatchesByRegion(
				itWatches, region);
		ArrayList<Document> listLogs = Filter
				.filterLogsByRegion(itLogs, region);

		for (Document document : listWatches) {
			System.out.println(document.toJson());
		}
		for (Document document : listLogs) {
			System.out.println(document.toJson());
		}

		// dalle liste viene creata la matrice di utilit�
		UtilityMatrixService ums = new UtilityMatrixService();
		UtilityMatrix uMatrix = ums.createUtilityMatrix(listWatches, listLogs);
		System.out.println("+++++++++++++++++ MERGE ++++++++++++++++++++++");
		uMatrix.printUtilityMatrix();
		return uMatrix;
	}

	public void saveMatrix(UtilityMatrix matrix) {
		try {
			FileWriter writer = new FileWriter("matrix.csv");
			List<Long> userList = matrix.getUserMatrix();
			for (long user : userList) {
				List<String> provinceList = matrix.getProvinceMatrix();
				for (String province : provinceList) {
					double value = (double) matrix.getValueByUserAndProvince(
							user, province);
					writer.append(String.valueOf(user) + "," + province + ","
							+ String.valueOf(value)+"\n");
				}
				List<String> municipalityList = matrix.getMunicipalityMatrix();
				for (String municipality : municipalityList) {
					double value = (double) matrix.getValueByUserAndMunicipality(user, municipality);
					writer.append(String.valueOf(user) + "," + municipality + ","
							+ String.valueOf(value)+"\n");
				}
				List<String> schoolList = matrix.getSchoolMatrix();
				for (String school : schoolList) {
					double value = (double) matrix.getValueByUserAndSchool(user, school);
					writer.append(String.valueOf(user) + "," + school + ","
							+ String.valueOf(value)+"\n");
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("File not Found!!!");
			e.printStackTrace();
		}

	}
}
