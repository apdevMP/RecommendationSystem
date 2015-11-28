package core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

public class RankingService
{

	private List<CustomRecommendedItem>	provinceIdList;
	private List<CustomRecommendedItem>	municipalityIdList;
	private List<CustomRecommendedItem>	schoolsIdList;
	private String						userPosition;
	private String						teachingRole;
	private QueryManager				queryManager;

	public RankingService(List<RecommendedItem> recommendedItems, Map<String, Long> itemsMap, Map<String, Long> categoriesMap, Profile userProfile)
	{

		System.out.println("Starting ranking service..");
		//istanzia le liste che dovranno essere popolate con gli item creati dal recommender
		provinceIdList = new ArrayList<CustomRecommendedItem>();
		municipalityIdList = new ArrayList<CustomRecommendedItem>();
		schoolsIdList = new ArrayList<CustomRecommendedItem>();

		//crea un'istanza di QueryManager per effettuare le query sui db
		queryManager = new QueryManager();

		/*
		 * recupera la posizione dell'utente dal profilo per il primo filtraggio
		 * degli item raccomandati
		 */
		userPosition = userProfile.getPosition();
		teachingRole = userProfile.getTeachingRole();

		/*
		 * crea tre liste separate per distinguere gli item in - province -
		 * comuni -scuole
		 */
		for (RecommendedItem item : recommendedItems)
		{

			/*
			 * recupera l'id numerico associato all'item e in base al suo valore
			 * inserisce l'item all'interno della lista corretta
			 */
			long itemId = item.getItemID();
			//System.out.println("Item id: " + itemId);
			//	System.out.println("itemId= " + itemId);
			if (itemId <= categoriesMap.get("province"))
			{
				//trovato l'id di una provincia
				//System.out.println("Item = provincia");
				for (Entry<String, Long> entry : itemsMap.entrySet())
				{
					/*
					 * Recupera il vero id in formato stringa dell'item dalla
					 * map, lo setta all'interno dell'oggetto e lo salva nella
					 * lista delle province
					 */
					if (entry.getValue().compareTo(itemId) == 0)
					{
						String realId = entry.getKey();
						//		System.out.println("Real ID = " + realId);
						CustomRecommendedItem customProvince = new CustomRecommendedItem(itemId, item.getValue(), 0, realId);
						provinceIdList.add(customProvince);
						break;
					}

				}

			}

			else if (categoriesMap.get("province") < itemId && itemId <= categoriesMap.get("comuni"))
			{
				//trovato l'id di un comune
				//System.out.println("Item = comune");
				for (Entry<String, Long> entry : itemsMap.entrySet())
				{
					if (entry.getValue().compareTo(itemId) == 0)
					{
						String realId = entry.getKey();
						//	System.out.println("Real ID = " + realId);
						CustomRecommendedItem customMunicipality = new CustomRecommendedItem(itemId, item.getValue(), 0, realId);
						municipalityIdList.add(customMunicipality);
						break;
					}

				}
			}

			else if (categoriesMap.get("comuni") < itemId && itemId <= categoriesMap.get("scuole"))
			{
				//trovato l'id di una scuola
				//System.out.println("Item = scuola");
				for (Entry<String, Long> entry : itemsMap.entrySet())
				{
					if (entry.getValue().compareTo(itemId) == 0)
					{
						String realId = entry.getKey();
						//	System.out.println("Real ID = " + realId);
						CustomRecommendedItem customSchool = new CustomRecommendedItem(itemId, item.getValue(), 0, realId);
						schoolsIdList.add(customSchool);
						break;
					}

				}

			}

		}

		System.out.println("Liste separate create");
		System.out.println("Size lista province:" + provinceIdList.size());
		System.out.println("Size lista comuni:" + municipalityIdList.size());
		System.out.println("Size lista scuole:" + schoolsIdList.size());

		/*
		 * a questo punto le tre liste sono state riempite, quindi si passa alla
		 * classificazione
		 */
		updateRankings();

		System.out.println("Classificazione terminata.");

	}

	public void updateRankings()
	{
		if (!provinceIdList.isEmpty())
			rankProvinces();
		if (!municipalityIdList.isEmpty())
			rankMunicipalities();
		if (!schoolsIdList.isEmpty())
			rankSchools();

	}

	public void rankProvinces()
	{
		for (CustomRecommendedItem province : provinceIdList)
		{
			/*
			 * CLASSIFICAZIONE IN BASE ALLA POSIZIONE se la provincia trovata si
			 * trova all'interno della regione scelta dall'utente allora aumenta
			 * il suo ranking
			 */
			if (queryManager.isProvinceInRegion(userPosition, province.getRealID()) == 0)
			{
				System.out.println("Provincia:" + province.getRealID() + " in " + userPosition);
				province.setRanking(province.getRanking() + 1);
			}

			//TODO CLASSIFICAZIONE IN BASE AL TEACHING ROLE

		}
	}

	public void rankMunicipalities()
	{
		for (CustomRecommendedItem municipality : municipalityIdList)
		{
			/*
			 * CLASSIFICAZIONE IN BASE ALLA POSIZIONE se la provincia trovata si
			 * trova all'interno della regione scelta dall'utente allora aumenta
			 * il suo ranking
			 */
			if (queryManager.isMunicipalityInRegion(userPosition, municipality.getRealID()) == 0)
			{
				System.out.println("Comune:" + municipality.getRealID() + " in " + userPosition);
				municipality.setRanking(municipality.getRanking() + 1);
			}

			//TODO CLASSIFICAZIONE IN BASE AL TEACHING ROLE

		}
	}

	public void rankSchools()
	{

		for (CustomRecommendedItem school : schoolsIdList)
		{

			String realID = school.getRealID();

			/*
			 * CLASSIFICAZIONE IN BASE ALLA POSIZIONE 
			 * se la scuola trovata si trova all'interno della regione scelta dall'utente allora aumenta
			 * il suo ranking
			 */
			if (queryManager.isSchoolInRegion(userPosition, realID) == 0)
			{
				System.out.println("Scuola:" + realID + " in " + userPosition);
				school.setRanking(school.getRanking() + 1);
			}

			/*
			 * CLASSIFICAZIONE IN BASE AL TEACHING ROLE 
			 * Se la scuola prevede tra le materie di insegnamento quella dell'utente, allora aumenta il
			 * suo ranking
			 */
			try
			{
				if (queryManager.isSchoolQuotedForTeachingRole(realID, teachingRole));
				school.setRanking(school.getRanking() + 1);
				
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

	}
}
