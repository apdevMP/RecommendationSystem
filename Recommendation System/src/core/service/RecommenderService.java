package core.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import core.data.CustomRecommendedItem;
import core.data.UtilityMatrix;
import core.service.profile.Profile;

import utils.Configuration;

/**
 * Classe di servizio per effettuare le raccomandazioni
 * 
 */
public class RecommenderService
{

	private Profile				userProfile;
	private Configuration configuration = null;
	private static final Logger	LOGGER	= Logger.getLogger(RecommenderService.class.getName());

	public RecommenderService(Profile profile)
	{
		userProfile = profile;
		if (configuration == null)
		{
			configuration = Configuration.getIstance();

		}
	}

	/**
	 * Metodo per trovare i suggerimenti da fare ad un utente che ricerca una
	 * regione
	 * 
	 * @param region
	 */
	public List<CustomRecommendedItem> recommendItems(String region)
	{

		List<CustomRecommendedItem> finalList = new ArrayList<CustomRecommendedItem>();

		UtilityMatrixService utilityMatrixService = new UtilityMatrixService(userProfile);

		UtilityMatrix matrix = utilityMatrixService.createPreferencesWithPagination();
		utilityMatrixService.savePreferences(matrix);

		try
		{
			DataModel model = new FileDataModel(new File(userProfile.getId()+".csv"));
			@SuppressWarnings("unused")
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			UserSimilarity similarity2 = new EuclideanDistanceSimilarity(model);
			@SuppressWarnings("unused")
			UserSimilarity similarity3 = new LogLikelihoodSimilarity(model);
			// ItemSimilarity similarity = new
			// PearsonCorrelationSimilarity(model);
			// Optimizer optimizer = new ConjugateGradientOptimizer();
		//	Recommender recommender = new SlopeOneRecommender(model);
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(1, similarity2, model);
			 Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity2);
			// Recommender cachingRecommender = new
			// CachingRecommender(recommender);

			LOGGER.info("\n\n[" + RecommenderService.class.getName() + "] Starting recommender service..");

			System.out.println(configuration.getRecommended_items());
			List<RecommendedItem> recommendedItems = recommender.recommend(userProfile.getId(),configuration.getRecommended_items() );

			LOGGER.info("[" + RecommenderService.class.getName() + "] List of recommended items created");

			if (!recommendedItems.isEmpty())
			{
				for (RecommendedItem item : recommendedItems)
				{
					LOGGER.info("id:" + item.getItemID() + ", value:" + item.getValue());

				}

				RankingService rankingService = new RankingService(recommendedItems, utilityMatrixService.getItemsMap(),
						utilityMatrixService.getCategoriesMap(), userProfile);

				/*
				 * ottenimento della lista dei risultati da stampare sulla
				 * finestra utente
				 */
				finalList = rankingService.getFinalList();

			} else
			{
				LOGGER.warning("[" + RecommenderService.class.getName() + "] Empty recommendation list");

			}

		} catch (IOException | TasteException e)
		{
			LOGGER.log(Level.SEVERE, "[" + RecommenderService.class.getName() + "] Exception: ", e);
		}

		return finalList;
		// System.exit(0);

	}

}
