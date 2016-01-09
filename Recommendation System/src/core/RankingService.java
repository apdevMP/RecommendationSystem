package core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

/**
 * Servizio per la classificazione finale degli item raccomandati in base a: -
 * distanza dalla regione dell'utente - posizioni libere - area di insegnamento
 * - punteggio dell'utente
 * 
 * @author Vanessa
 *
 */

public class RankingService
{

	private List<CustomRecommendedItem>	provinceIdList;
	private List<CustomRecommendedItem>	municipalityIdList;
	private List<CustomRecommendedItem>	schoolsIdList;
	private List<CustomRecommendedItem>	finalList;
	private String						userPosition;
	private String						teachingRole;
	private Double						score;
	private QueryManager				queryManager;
	private static int					ELEMENTS_PER_LIST	= 5;

	private static final Logger			LOGGER				= Logger.getLogger(RankingService.class.getName());

	public RankingService(List<RecommendedItem> recommendedItems, Map<String, Long> itemsMap, Map<String, Long> categoriesMap, Profile userProfile)
	{

		LOGGER.info("[" + RankingService.class.getName() + "] Starting ranking service..");

		/*
		 * istanzia le liste che dovranno essere popolate con gli item creati
		 * dal recommender
		 */
		provinceIdList = new ArrayList<CustomRecommendedItem>();
		municipalityIdList = new ArrayList<CustomRecommendedItem>();
		schoolsIdList = new ArrayList<CustomRecommendedItem>();

		/*
		 * crea un'istanza di QueryManager per effettuare le query sui db
		 */
		queryManager = new QueryManager();

		/*
		 * recupera la posizione dell'utente dal profilo per il primo filtraggio
		 * degli item raccomandati
		 */
		userPosition = userProfile.getPosition();
		teachingRole = userProfile.getTeachingRole();
		score = userProfile.getScore();

		LOGGER.info("[" + RankingService.class.getName() + "] ++++++++++++++++++++++++++++score = "+score);
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

			if (itemId <= categoriesMap.get("province"))
			{
				//trovato l'id di una provincia
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

						CustomRecommendedItem customProvince = new CustomRecommendedItem(itemId, item.getValue(), 0, realId);

						provinceIdList.add(customProvince);
						break;
					}

				}

			}

			else if (categoriesMap.get("province") < itemId && itemId <= categoriesMap.get("comuni"))
			{
				//trovato l'id di un comune
				for (Entry<String, Long> entry : itemsMap.entrySet())
				{
					if (entry.getValue().compareTo(itemId) == 0)
					{
						String realId = entry.getKey();
						CustomRecommendedItem customMunicipality = new CustomRecommendedItem(itemId, item.getValue(), 0, realId);
						municipalityIdList.add(customMunicipality);
						break;
					}

				}
			}

			else if (categoriesMap.get("comuni") < itemId && itemId <= categoriesMap.get("scuole"))
			{
				//trovato l'id di una scuola
				for (Entry<String, Long> entry : itemsMap.entrySet())
				{
					if (entry.getValue().compareTo(itemId) == 0)
					{
						String realId = entry.getKey();
						CustomRecommendedItem customSchool = new CustomRecommendedItem(itemId, item.getValue(), 0, realId);
						schoolsIdList.add(customSchool);
						break;
					}

				}

			}
		}

		/*
		 * a questo punto le tre liste sono state riempite, quindi si passa alla
		 * classificazione
		 */
		updateRankings();
		LOGGER.info("[" + RankingService.class.getName() + "] Updating process for rankings terminated. Classification in progress.. ");

		sortLists();

		LOGGER.info("[" + RankingService.class.getName() + "] Classification terminated");

		printSortedLists();

		LOGGER.info("\n\n----- FINAL LIST ---");
		finalList = new ArrayList<CustomRecommendedItem>();
		getRecommendedItemsList(ELEMENTS_PER_LIST);
		for (CustomRecommendedItem item : finalList)
		{
			LOGGER.info(item.getRealID() + " , recommendation value:" + item.getValue() + " , ranking:" + item.getRanking());
		}

		LOGGER.info("\n[" + RankingService.class.getName() + "] Done.");
	}

	/**
	 * @return la lista finale da mostrare all'utente
	 */
	public List<CustomRecommendedItem> getFinalList()
	{
		return finalList;
	}

	public void setFinalList(List<CustomRecommendedItem> finalList)
	{
		this.finalList = finalList;
	}

	/**
	 * Aggiunge alla lista finale da mostrare all'utente un numero {@param
	 * numberOfResults} di item per ognuna delle liste create
	 * 
	 * @param numberOfResults il numero di item per lista da mostrare
	 * sull'interfaccia
	 */
	public void getRecommendedItemsList(int numberOfResults)
	{

		/*
		 * Per ogni lista, se essa contiene più item di quelli richiesti, ne
		 * aggiungo solo un numero pari a quelli voluti, altrimenti se sono
		 * meno, li aggiungo tutti alla lista finale
		 */
		addToFinalList(provinceIdList, numberOfResults);
		addToFinalList(municipalityIdList, numberOfResults);
		addToFinalList(schoolsIdList, numberOfResults);

	}

	/**
	 * Preleva dalla lista list passata come parametro i primi numberOfResults
	 * item da inserire nella lista finale
	 * 
	 * @param list
	 * @param numberOfResults
	 */
	private void addToFinalList(List<CustomRecommendedItem> list, int numberOfResults)
	{

		if (list.size() > numberOfResults)
		{
			for (int index = 0; index < numberOfResults; index++)
			{

				finalList.add(list.get(index));

			}
		} else
		{
			/*
			 * se il numero di elementi nella lista è minore di quelli da
			 * inserire, allora si aggiungono tutti senza iterare
			 */
			getFinalList().addAll(list);
		}

	}

	/**
	 * Aggiorna il punteggio degli item delle liste parziali
	 */
	private void updateRankings()
	{
		if (!provinceIdList.isEmpty())
			rankProvinces();
		if (!municipalityIdList.isEmpty())
			rankMunicipalities();
		if (!schoolsIdList.isEmpty())
			rankSchools();

	}

	/**
	 * Ordina le liste parziali
	 */
	private void sortLists()
	{
		if (!provinceIdList.isEmpty())
			Collections.sort(provinceIdList);
		if (!municipalityIdList.isEmpty())
			Collections.sort(municipalityIdList);
		if (!schoolsIdList.isEmpty())
			Collections.sort(schoolsIdList);

	}

	private void printSortedLists()
	{
		LOGGER.info("\n----- PROVINCE -----");
		printList(provinceIdList);

		LOGGER.info("\n----- COMUNI -----");
		printList(municipalityIdList);

		LOGGER.info("\n----- SCUOLE -----");
		printList(schoolsIdList);

	}

	private void printList(List<CustomRecommendedItem> list)
	{

		for (CustomRecommendedItem item : list)
		{
			LOGGER.info(item.getRealID() + " , recommendation value:" + item.getValue() + " , ranking:" + item.getRanking());
		}
	}

	/**
	 * Assegna punteggi alle province
	 */
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
				province.setRanking(province.getRanking() + 1);
			}

			//CLASSIFICAZIONE IN BASE AL TEACHING ROLE
			try
			{
				if (queryManager.freePositionAvailableInMunicipality(province.getRealID(), teachingRole) == true)
				{
					province.setRanking(province.getRanking() + 1);
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}

		}
	}

	/**
	 * Assegna punteggi ai comuni
	 */
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
				municipality.setRanking(municipality.getRanking() + 1);
			}

			//CLASSIFICAZIONE IN BASE AL TEACHING ROLE
			try
			{
				if (queryManager.freePositionAvailableInMunicipality(municipality.getRealID(), teachingRole) == true)
				{
					municipality.setRanking(municipality.getRanking() + 1);
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}

		}
	}

	/**
	 * Assegna punteggi alle scuole
	 */
	public void rankSchools()
	{

		for (CustomRecommendedItem school : schoolsIdList)
		{

			String realID = school.getRealID();

			/*
			 * CLASSIFICAZIONE IN BASE ALLA POSIZIONE se la scuola trovata si
			 * trova all'interno della regione scelta dall'utente allora aumenta
			 * il suo ranking
			 */
			if (queryManager.isSchoolInRegion(userPosition, realID) == 0)
			{

				school.setRanking(school.getRanking() + 1);
			}

			/*
			 * CLASSIFICAZIONE IN BASE AL TEACHING ROLE Se la scuola prevede tra
			 * le materie di insegnamento quella dell'utente allora aumenta il
			 * suo ranking
			 */
			try
			{
				if (queryManager.isSchoolQuotedForTeachingRole(realID, teachingRole))
				{
					school.setRanking(school.getRanking() + 1);
				}

			} catch (SQLException e)
			{
				e.printStackTrace();
			}

			/*
			 * CLASSIFICAZIONE IN BASE ALLO SCORE Se la scuola prevede tra i
			 * trasferimenti in uscita utenti con score relazionabile a quello
			 * dell'utente allora aumenta il suo ranking
			 */
			try
			{
				
				if (queryManager.isScoreMatchingTransfers(realID, teachingRole, score))
				{
					LOGGER.info("[" + RankingService.class.getName() + "] +++++++++ Scuola "+realID+" trovato score compatibile");
					school.setRanking(school.getRanking() + 1);
				}

			} catch (SQLException e)
			{
				e.printStackTrace();
			}

			/*
			 * CLASSIFICAZIONE IN BASE AL NUMERO DI POSTI LIBERI PER QUEL
			 * TEACHING ROLE
			 */

			try
			{
				if (queryManager.freePositionAvailableAtSchool(realID, teachingRole) == true)
				{
					//		System.out.println("Posti disponibili nella scuola");
					school.setRanking(school.getRanking() + 1);
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}

		}

	}
}
