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
	 * 
	 * @param region
	 */
	public void recommedByRegion(String region) {
		QueryManager queryManager = new QueryManager();
		String teachingRole = userProfile.getTeachingRole();

		FindIterable<Document> itWatches = queryManager
				.findWatchesByTeachingRole(teachingRole);

		FindIterable<Document> itLogs = queryManager
				.findLogsByTeachingRole(teachingRole);
		
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
		
		UtilityMatrix um = new UtilityMatrix();
		um.fillMatrixWithWatches(listWatches);
		um.printUtilityMatrix();

		UtilityMatrix um2 = new UtilityMatrix();
		um2.fillMatrixWithLogs(listLogs);
		um2.printUtilityMatrix();
		
		um.mergeUtilityMatrix(um2);
		System.out.println("+++++++++++++++++ MERGE ++++++++++++++++++++++");
		um.printUtilityMatrix();
	}

	public void recommedByProvince(String province) {

	}

	public void recommendByMunicipality(String municipality) {

	}

	public void recommedBySchool(String school) {

	}
}
