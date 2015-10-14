package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.relation.Role;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
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
	public void recommendByRegion(String region)
	{

		UtilityMatrixCreator creator = new UtilityMatrixCreator(userProfile, region);
		UtilityMatrix matrix = creator.createUtilityMatrix();
		creator.saveMatrix(matrix);

		try
		{
			DataModel model = new FileDataModel(new File("matrix.csv"));
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
			Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
			Recommender cachingRecommender = new CachingRecommender(recommender);
			List<RecommendedItem> recommendedItems = cachingRecommender.recommend(71027, 3);
			
			
			System.out.println("Creata lista items");
			
			if (!recommendedItems.isEmpty())
			{
				for (RecommendedItem item : recommendedItems)
				{
					System.out.println("id:" + item.getItemID() + ",value:" + item.getValue());
				}
			} else
			{
				System.out.println("la lista è vuota");
			}
		} catch (IOException | TasteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void recommendByProvince(String province)
	{

	}

	public void recommendByMunicipality(String municipality)
	{

	}

	public void recommendBySchool(String school)
	{

	}
}
