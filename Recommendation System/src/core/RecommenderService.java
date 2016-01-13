package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;

import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * Classe di servizio per effettuare le raccomandazioni
 * 
 */
public class RecommenderService {

	private Profile userProfile;
	private static final Logger LOGGER = Logger
			.getLogger(RecommenderService.class.getName());

	public RecommenderService(Profile profile) {
		userProfile = profile;
	}

	/**
	 * Metodo per trovare i suggerimenti da fare ad un utente che ricerca una
	 * regione
	 * 
	 * @param region
	 */
	public List<CustomRecommendedItem> recommendByRegion(String region) {

		List<CustomRecommendedItem> finalList = new ArrayList<CustomRecommendedItem>();

		UtilityMatrixService utilityMatrixService = new UtilityMatrixService(
				userProfile);

		UtilityMatrix matrix = utilityMatrixService
				.createPreferencesWithPagination();
		utilityMatrixService.savePreferences(matrix);

		try {
			DataModel model = new FileDataModel(new File("matrix_value.csv"));
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			UserSimilarity similarity2 = new EuclideanDistanceSimilarity(model);
			UserSimilarity similarity3 = new LogLikelihoodSimilarity(model);
			// ItemSimilarity similarity = new
			// PearsonCorrelationSimilarity(model);
			// Optimizer optimizer = new ConjugateGradientOptimizer();
			Recommender recommender = new SlopeOneRecommender(model);
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(1,
					similarity2, model);
			// Recommender recommender = new GenericUserBasedRecommender(model,
			// neighborhood, similarity2);
			// Recommender cachingRecommender = new
			// CachingRecommender(recommender);

			LOGGER.info("\n\n[" + RecommenderService.class.getName()
					+ "] Starting recommender service..");

			List<RecommendedItem> recommendedItems = recommender.recommend(
					userProfile.getId(), 70);

			LOGGER.info("[" + RecommenderService.class.getName()
					+ "] List of recommended items created");

			if (!recommendedItems.isEmpty()) {
				for (RecommendedItem item : recommendedItems) {
					LOGGER.info("id:" + item.getItemID() + ", value:"
							+ item.getValue());

				}

				RankingService rankingService = new RankingService(
						recommendedItems, utilityMatrixService.getItemsMap(),
						utilityMatrixService.getCategoriesMap(), userProfile);

				/*
				 * ottenimento della lista dei risultati da stampare sulla
				 * finestra utente
				 */
				finalList = rankingService.getFinalList();

			} else {
				LOGGER.warning("[" + RecommenderService.class.getName()
						+ "] Empty recommendation list");

			}

		} catch (IOException | TasteException e) {
			LOGGER.log(Level.SEVERE, "[" + RecommenderService.class.getName()
					+ "] Exception: ", e);
		}

		return finalList;
		// System.exit(0);

	}

}
