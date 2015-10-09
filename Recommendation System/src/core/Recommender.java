package core;

import java.util.ArrayList;

import javax.management.relation.Role;

import org.bson.Document;

import com.mongodb.client.FindIterable;

public class Recommender {

	private Profile userProfile;

	public Recommender(Profile profile) {
		userProfile = profile;
	}

	/**
	 * Metodo per trovare i suggerimenti da fare ad un utente che ricerca una regione
	 * @param region
	 */
	public void recommedByRegion(String region) {

		//istanzio un queryManager per recuperare dati dalle collezioni
		QueryManager queryManager = new QueryManager();
		
		//si recupera la materia di insegnamento dell'utente
		String teachingRole = userProfile.getTeachingRole();

		//si recupera la lista di watch e di log in base alla materia di insegnamento
		FindIterable<Document> itWatches = queryManager
				.findWatchesByTeachingRole(teachingRole);
		FindIterable<Document> itLogs = queryManager
				.findLogsByTeachingRole(teachingRole);
		
		//le liste di watch e log vengono filtrate per regione
		ArrayList<Document> listWatches = Filter.filterWatchesByRegion(
				itWatches, region);
		ArrayList<Document> listLogs = Filter
				.filterLogsByRegion(itLogs, region);

		
		for(Document document : listWatches){
			System.out.println(document.toJson());
		}
		for(Document document : listLogs){
			System.out.println(document.toJson());
		}
		
		//dalle liste viene creata la matrice di utilit�
		UtilityMatrixService ums = new UtilityMatrixService();
		UtilityMatrix uMatrix = ums.createUtilityMatrix(listWatches, listLogs);
		System.out.println("+++++++++++++++++ MERGE ++++++++++++++++++++++");
		uMatrix.printUtilityMatrix();
	}

	public void recommedByProvince(String province) {

	}

	public void recommendByMunicipality(String municipality) {

	}

	public void recommedBySchool(String school) {

	}
}
