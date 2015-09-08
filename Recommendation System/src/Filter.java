import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.FindIterable;


public class Filter {
	
	public static ArrayList<Document> filterByRegion(FindIterable<Document> iterable,String region){
	
		ArrayList<Document> list = new ArrayList<Document>();
		QueryManager queryManager = new QueryManager();
		for(Document document : iterable)
		{
			int result;
			Document target = (Document) document.get("target");
			long typeId = target.getLong("typeId");
			System.out.println(typeId);
			switch ((int) typeId) {
			case 1:
				String province = target.getString("key");
				System.out.println(province);
				result = queryManager.isProvinceInRegion(region,province);
				if(result==0){
					list.add(document);
				}	
				break;

			case 2:
				String municipality = target.getString("key");
				System.out.println(municipality);
				result = queryManager.isMunicipalityInRegion(region,municipality);
				if(result==0){
					list.add(document);
				}
					
				break;
				
			case 3:
				String school = target.getString("key");
				System.out.println(school);
				result = queryManager.isSchoolInRegion(region,school);
				if(result==0){
					list.add(document);
				}
				break;
			
			}
		}
		return list;
	}

}