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
	private Map<String, Long> itemsMap;
	private Map<String, Long> categoriesMap;

	public UtilityMatrixService()
	{

	}

	public UtilityMatrixService(Profile profile, String region)
	{
		this.userProfile = profile;
		this.region = region;
		
		this.categoriesMap = new HashMap<String, Long>();
	
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
		System.out.println("Finding watches..");
		FindIterable<Document> itWatches = queryManager.findWatches();
	//	FindIterable<Document> itLogs = queryManager.findLogsByTeachingRole(teachingRole);
		System.out.println("Findind logs..");;
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

		System.out.println("Creating utility matrix..");
		// dalle liste viene creata la matrice di utilit�
		UtilityMatrixCreator ums = new UtilityMatrixCreator();
		UtilityMatrix uMatrix = ums.createUtilityMatrix(listWatches, listLogs);
		//uMatrix.printUtilityMatrix();
		return uMatrix;
	}

	public void saveMatrix(UtilityMatrix matrix)
	{
		System.out.println("Saving data...");
		try
		{
			FileWriter writer = new FileWriter("matrix_value.csv");
			List<Long> userList = matrix.getUserMatrix();
			List<String> provinceList = matrix.getProvinceMatrix();
			List<String> municipalityList = matrix.getMunicipalityMatrix();
			List<String> schoolList = matrix.getSchoolMatrix();


			long counter = 0;
			this.itemsMap = new HashMap<String, Long>();
			System.out.println("counter="+counter+"...inizio province");
			for(String provinceCodeString : provinceList){
			//	System.out.println(provinceCodeString);
				
				Long i = itemsMap.get(provinceCodeString);
				
				if (i == null)
				{
					itemsMap.put(provinceCodeString, counter);
					//	System.out.println("MAP: "+schoolCodeString+","+counter);
					i = (long) counter;
					++counter;
				}
			}
			
			this.categoriesMap.put("province", counter);
			System.out.println("categoriesMap, province:"+categoriesMap.get("province"));
			System.out.println("counter="+counter+"...inizio comuni");
			for(String municipalityCodeString : municipalityList){
				//System.out.println(municipalityCodeString);
				Long i = itemsMap.get(municipalityCodeString);
				if (i == null)
				{
					itemsMap.put(municipalityCodeString, counter);
					//	System.out.println("MAP: "+schoolCodeString+","+counter);
					i = (long) counter;
					++counter;
				}
			}
			
			this.categoriesMap.put("comuni", counter);
			System.out.println("categoriesMap, comuni:"+categoriesMap.get("comuni"));
			System.out.println("counter="+counter+"...inizio scuole");
			for (String schoolCodeString : schoolList)
			{
				//System.out.println(schoolCodeString);
				Long i = itemsMap.get(schoolCodeString);
				if (i == null)
				{
					itemsMap.put(schoolCodeString, counter);
					//	System.out.println("MAP: "+schoolCodeString+","+counter);
					i = (long) counter;
					++counter;
				}
			}
			
			
			this.categoriesMap.put("scuole", counter);
			System.out.println("categoriesMap, scuole:"+categoriesMap.get("scuole"));
			System.out.println("counter totale:"+counter);

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
					writer.append(String.valueOf(user) + "," + itemsMap.get(province) + "," + String.valueOf(value) + "\n");
				}
			//	List<String> municipalityList = matrix.getMunicipalityMatrix();
				for (String municipality : municipalityList)
				{
					double value = (double) matrix.getValueByUserAndMunicipality(user, municipality);

				//	long municipalityId = queryManager.retrieveMunicipalityId(municipality);
			//		System.out.println("comune:" + municipality + ",id:" + municipalityId);
					if (value != 0)
						writer.append(String.valueOf(user) + "," + itemsMap.get(municipality) + "," + String.valueOf(value) + "\n");
				}

				//System.out.println(schoolList);
				for (String school : schoolList)
				{

					double value = (double) matrix.getValueByUserAndSchool(user, school);
					
					if(value > 0)
						writer.append(String.valueOf(user) + "," + itemsMap.get(school) + "," + String.valueOf(value) + "\n");
					//	System.out.println("trovata scuola:" + String.valueOf(user) + "," + map.get(school) + "," + school + ","
					//			+ String.valueOf(value));
					

				}
			}
			writer.flush();
			writer.close();
			
			System.out.println("Data saved!");
		} catch (IOException e)
		{
			System.out.println("File not Found!!!");
			e.printStackTrace();
		}
		

	}
	
	public Map<String, Long>getItemsMap(){
	 return	this.itemsMap;
	}
	
	public Map<String, Long> getCategoriesMap(){
		return this.categoriesMap;
	}

}
