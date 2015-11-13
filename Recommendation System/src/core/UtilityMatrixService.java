package core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.google.common.collect.Maps;
import com.mongodb.Function;
import com.mongodb.client.FindIterable;

public class UtilityMatrixService
{

	private Profile			userProfile;
	private String			region;
	private QueryManager	queryManager;

	public UtilityMatrixService()
	{

	}

	public UtilityMatrixService(Profile profile, String region)
	{
		this.userProfile = profile;
		this.region = region;
	}

	public UtilityMatrix createUtilityMatrix()
	{
		// istanzio un queryManager per recuperare dati dalle collezioni
		queryManager = new QueryManager();
		//queryManager.getLogAction();
		//		for(String itemString : arrayList){
		//			System.out.println(itemString);
		//		}
		// si recupera la materia di insegnamento dell'utente
		String teachingRole = userProfile.getTeachingRole();
		String[] actions = {"webapi_school_aggregates","webapi_municipality_aggregates","webapi_get_best_schools"};

		// si recupera la lista di watch e di log in base alla materia di
		// insegnamento
	//	FindIterable<Document> itWatches = queryManager.findWatchesByTeachingRole(teachingRole);
		FindIterable<Document> itWatches = queryManager.findWatches();
	//	FindIterable<Document> itLogs = queryManager.findLogsByTeachingRole(teachingRole);
		FindIterable<Document> itLogs = queryManager.getLogsByAction(actions);

		// le liste di watch e log vengono filtrate per regione
		//ArrayList<Document> listWatches = Filter.filterWatchesByRegion(itWatches, region);
		//ArrayList<Document> listLogs = Filter.filterLogsByRegion(itLogs, region);
		ArrayList<Document> listWatches = new ArrayList<Document>();
		ArrayList<Document> listLogs = new ArrayList<Document>();

		for (Document document : itWatches)
		{
			listWatches.add(document);
			//System.out.println(document.toJson());
		}
		for (Document document : itLogs)
		{
			listLogs.add(document);
			//System.out.println(document.toJson());
		}

		// dalle liste viene creata la matrice di utilit�
		UtilityMatrixCreator ums = new UtilityMatrixCreator();
		UtilityMatrix uMatrix = ums.createUtilityMatrix(listWatches, listLogs);
		System.out.println("+++++++++++++++++ MERGE ++++++++++++++++++++++");
		//uMatrix.printUtilityMatrix();
		return uMatrix;
	}

	public void saveMatrix(UtilityMatrix matrix)
	{
		try
		{
			FileWriter writer = new FileWriter("new_versionMap.csv");
			List<Long> userList = matrix.getUserMatrix();
			List<String> provinceList = matrix.getProvinceMatrix();
			List<String> municipalityList = matrix.getMunicipalityMatrix();
			List<String> schoolList = matrix.getSchoolMatrix();

			//prova mapping
			Map<String, Long> map;

			long counter = 0;
			map = new HashMap<String, Long>();
			for(String provinceCodeString : provinceList){
				Long i = map.get(provinceCodeString);
				if (i == null)
				{
					map.put(provinceCodeString, counter);
					//	System.out.println("MAP: "+schoolCodeString+","+counter);
					i = (long) counter;
					++counter;
				}
			}
			for(String municipalityCodeString : municipalityList){
				Long i = map.get(municipalityCodeString);
				if (i == null)
				{
					map.put(municipalityCodeString, counter);
					//	System.out.println("MAP: "+schoolCodeString+","+counter);
					i = (long) counter;
					++counter;
				}
			}
			for (String schoolCodeString : schoolList)
			{

				Long i = map.get(schoolCodeString);
				if (i == null)
				{
					map.put(schoolCodeString, counter);
					//	System.out.println("MAP: "+schoolCodeString+","+counter);
					i = (long) counter;
					++counter;
				}
			}
			
			System.out.println("counter:"+counter);

			for (long user : userList)
			{
				//List<String> provinceList = matrix.getProvinceMatrix();
				for (String province : provinceList)
				{
					double value = (double) matrix.getValueByUserAndProvince(user, province); 
					/*
					 * Dato che il Recommender di Mahout richiede che i dati
					 * presenti nel csv siano di tipo Long, al posto della sigla
					 * della procincia si recupera e scrive il codice
					 * identificativo, utilizzando il file municipality.csv
					 */

				//	long provinceId = queryManager.retrieveProvinceId(province);

					//System.out.println("provincia:" + province + ",id:" + provinceId);

					if(value > 0)
					writer.append(String.valueOf(user) + "," + map.get(province) + "," + String.valueOf(value) + "\n");
				}
			//	List<String> municipalityList = matrix.getMunicipalityMatrix();
				for (String municipality : municipalityList)
				{
					double value = (double) matrix.getValueByUserAndMunicipality(user, municipality);

				//	long municipalityId = queryManager.retrieveMunicipalityId(municipality);
			//		System.out.println("comune:" + municipality + ",id:" + municipalityId);
					if (value != 0)
						writer.append(String.valueOf(user) + "," + map.get(municipality) + "," + String.valueOf(value) + "\n");
				}

				//System.out.println(schoolList);
				for (String school : schoolList)
				{

					double value = (double) matrix.getValueByUserAndSchool(user, school);
					
					if(value > 0)
						writer.append(String.valueOf(user) + "," + map.get(school) + "," + String.valueOf(value) + "\n");
					//	System.out.println("trovata scuola:" + String.valueOf(user) + "," + map.get(school) + "," + school + ","
					//			+ String.valueOf(value));
					

				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e)
		{
			System.out.println("File not Found!!!");
			e.printStackTrace();
		}

	}

}
