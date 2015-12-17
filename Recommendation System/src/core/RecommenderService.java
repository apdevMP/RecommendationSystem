package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.relation.Role;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.CachingUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.ConjugateGradientOptimizer;
import org.apache.mahout.cf.taste.impl.recommender.knn.KnnItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.Optimizer;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.classifier.sgd.RankingGradient;
import org.bson.Document;

import com.mongodb.client.FindIterable;

public class RecommenderService
{

	private Profile	userProfile;

	public RecommenderService(Profile profile)
	{
		userProfile = profile;
	}

	/**
	 * Metodo per trovare i suggerimenti da fare ad un utente che ricerca una
	 * regione
	 * 
	 * @param region
	 */
	public List<CustomRecommendedItem> recommendByRegion(String region)
	{

		List<CustomRecommendedItem> finalList = new ArrayList<CustomRecommendedItem>();
		
		UtilityMatrixService creator = new UtilityMatrixService(userProfile, region);

		UtilityMatrix matrix = creator.createUtilityMatrix();
		creator.saveMatrix(matrix);

		try
		{
			DataModel model = new FileDataModel(new File("matrix_value2.csv"));
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			UserSimilarity similarity2 = new EuclideanDistanceSimilarity(model);
			UserSimilarity similarity3 = new LogLikelihoodSimilarity(model);
			//ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
		//	Optimizer optimizer =  new ConjugateGradientOptimizer();
          Recommender recommender = new SlopeOneRecommender(model);
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(1, similarity2, model);
			//Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity2);
			//	Recommender cachingRecommender = new CachingRecommender(recommender);
			
			
			List<RecommendedItem> recommendedItems = recommender.recommend(7428, 20);
			
			System.out.println("Creata lista items, grandezza: " + recommendedItems.size());

			if (!recommendedItems.isEmpty())
			{
				for (RecommendedItem item : recommendedItems)
				{
					System.out.println("id:" + item.getItemID() + ",value:" + item.getValue());
					
				}
				System.out.println("Size recommendedItems: "+recommendedItems.size());
				RankingService rankingService = new RankingService(recommendedItems, creator.getItemsMap(), creator.getCategoriesMap(), userProfile);
				
				/*
				 * ottenimento della lista dei risultati da stampare sulla finestra utente
				 */
				finalList = rankingService.getFinalList();
				
			} else
			{
				System.out.println("la lista è vuota");
				
			}
			
		} catch (IOException | TasteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finalList;
	//	System.exit(0);
		

	}

}
