package core;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.FindIterable;

public class Filter
{

	private static final String	TARGET	= "target";
	private static final String	TYPE_ID	= "typeId";
	private static final String	KEY		= "key";

	public static ArrayList<Document> filterWatchesByRegion(FindIterable<Document> iterable, String region)
	{

		ArrayList<Document> list = new ArrayList<Document>();
		QueryManager queryManager = new QueryManager();
		for (Document document : iterable)
		{
			int result;
			Document target = (Document) document.get(TARGET);
			long typeId = target.getLong(TYPE_ID);
			// System.out.println(typeId);
			switch ((int) typeId)
			{
				case 1:
					String province = target.getString(KEY);
					// System.out.println(province);
					result = queryManager.isProvinceInRegion(region, province);
					if (result == 0)
					{
						list.add(document);
					}
					break;

				case 2:
					String municipality = target.getString(KEY);
					// System.out.println(municipality);
					result = queryManager.isMunicipalityInRegion(region, municipality);
					if (result == 0)
					{
						list.add(document);
					}

					break;

				case 3:
					String school = target.getString(KEY);
					// System.out.println(school);
					result = queryManager.isSchoolInRegion(region, school);
					if (result == 0)
					{
						list.add(document);
					}
					break;

			}
		}
		return list;
	}

	public static ArrayList<Document> filterLogsByRegion(FindIterable<Document> iterable, String region)
	{
		ArrayList<Document> list = new ArrayList<Document>();
		QueryManager queryManager = new QueryManager();
		for (Document document : iterable)
		{
			int result = 0;
			String action = document.getString("action");
			Document attributes = (Document) document.get("attributes");
			switch (action)
			{
				case "webapi_province_aggregates":

					break;

				case "webapi_municipality_aggregates":
					String municipality = attributes.getString("codeProvince");
					System.out.println(municipality);
					result = queryManager.isProvinceInRegion(region, municipality);
					if (result == 0)
					{
						list.add(document);
					}
					break;

				case "webapi_school_aggregates":
					String school = attributes.getString("codeMunicipality");
					System.out.println(school);
					result = queryManager.isMunicipalityInRegion(region, school);
					if (result == 0)
					{
						list.add(document);
					}
					break;
			}
		}

		return list;
	}

}