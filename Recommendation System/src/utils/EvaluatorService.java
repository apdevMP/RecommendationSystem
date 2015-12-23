package utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import com.google.common.util.concurrent.Service.Listener;

import view.EvaluationWindow;

/**
 * 
 * NON PIU UTILIZZATA, TRASFERITO TUTTO IN EVALUATOR CONTROLLER Classe per la
 * valutazione del Recommendation System. Score bassi identificano una buona
 * qualità del sistema di raccomandazioni. Score pari a 0 implicano il matching
 * perfetto tra le preferenze stimate e quelle reali.
 * 
 * @author
 *
 */

public class EvaluatorService
{
	private static DataModel	model;

	public EvaluatorService()
	{

	}

	public void startEvaluation() throws Exception
	{

		RandomUtils.useTestSeed();

		model = new FileDataModel(new File("matrix_value2.csv"));

		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();

		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {

			@Override
			public Recommender buildRecommender(final DataModel model) throws TasteException
			{

				UserSimilarity similarity1 = new PearsonCorrelationSimilarity(model);
				//	UserSimilarity similarity2 = new EuclideanDistanceSimilarity(model);
				//	UserSimilarity similarity3 = new LogLikelihoodSimilarity(model);
				//	ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(model);
				//	Optimizer optimizer =  new ConjugateGradientOptimizer();
				//Recommender recommender = new SlopeOneRecommender(model);

				/*
				 * FIXME per ora proviamo solo con la ThresholdUserNeighborhood
				 */
				UserNeighborhood neighborhood = new ThresholdUserNeighborhood(1, similarity1, model);

				//	Recommender cachingRecommender = new CachingRecommender(recommender);

				return new GenericUserBasedRecommender(model, neighborhood, similarity1);

			}
		};

		double score = evaluator.evaluate(recommenderBuilder, null, model, 0.5, 1.0);

		System.out.println("Evaluator score: " + score);
	}
}
