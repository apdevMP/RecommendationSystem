package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

public class RankingService
{

	private List<String>	provinceIdList;
	private List<String>	municipalityIdList;
	private List<String>	schoolsIdList;

	public RankingService(List<RecommendedItem> recommendedItemsList, Map<String, Long> itemsMap, Map<String, Long> categoriesMap)
	{

		provinceIdList = new ArrayList<String>();
		municipalityIdList = new ArrayList<String>();
		schoolsIdList = new ArrayList<String>();

		/*
		 * crea tre liste separate per distinguere gli item in - province -
		 * comuni -scuole
		 */
		for (RecommendedItem item : recommendedItemsList)
		{
			if (categoriesMap.isEmpty())
			{
				System.out.println("categorie vuote");
			}

			long itemId = item.getItemID();
			System.out.println("itemId= " + itemId);
			if (itemId <= categoriesMap.get("province"))
			{
				//trovato l'id di una provincia

				for (Entry<String, Long> entry : itemsMap.entrySet())
				{
					entry.getValue().compareTo(itemId);
					String realId = entry.getKey();
					provinceIdList.add(realId);

				}

				if (categoriesMap.get("province") < itemId && itemId <= categoriesMap.get("comuni"))
				{
					//trovato l'id di un comune

					for (Entry<String, Long> entry : itemsMap.entrySet())
					{
						entry.getValue().compareTo(itemId);
						String realId = entry.getKey();
						municipalityIdList.add(realId);

					}
				}

				if (categoriesMap.get("comuni") < itemId && itemId <= categoriesMap.get("scuole"))
				{
					//trovato l'id di una scuola

					for (Entry<String, Long> entry : itemsMap.entrySet())
					{
						entry.getValue().compareTo(itemId);
						String realId = entry.getKey();
						schoolsIdList.add(realId);

					}
				}

			}

		}

	}

}
