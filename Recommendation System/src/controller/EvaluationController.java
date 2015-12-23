package controller;

import java.awt.Window;
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
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import utils.EvaluatorService;
import view.EvaluationWindow;

public class EvaluationController
{

	private EvaluationWindow	evaluationWindow;
	private ActionListener		listener;
	private EvaluatorService	evaluatorService;
	private static UserSimilarity userSimilarity;
	private static ItemSimilarity itemSimilarity;
	private int similarityCode;
	private double trainingSet;
	private double testSet;
	
	public EvaluationController(EvaluationWindow window)
	{
		this.evaluationWindow = window;
	}

	public void evaluate()
	{
		listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				similarityCode = evaluationWindow.getSimilarity();
				trainingSet = evaluationWindow.getTrainingSet();
				testSet = evaluationWindow.getTestSet();
	
				double score;
				try
				{
					score = startEvaluation();
					publishScore(score);
				} catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}

			private void publishScore(double score)
			{
				// TODO Auto-generated method stub
				evaluationWindow.getScoreLabel().setText(""+score);
			}
		};
		evaluationWindow.getBtnStart().addActionListener(listener);
	}
	
	
	public double startEvaluation() throws Exception
	{

		RandomUtils.useTestSeed();

		DataModel model = new FileDataModel(new File("matrix_value2.csv"));

		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();

		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {

			@Override
			public Recommender buildRecommender(final DataModel model) throws TasteException
			{

				//	UserSimilarity similarity1 = new PearsonCorrelationSimilarity(model);
				//	UserSimilarity similarity2 = new EuclideanDistanceSimilarity(model);
				//	UserSimilarity similarity3 = new LogLikelihoodSimilarity(model);
				//	ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(model);
				//	Optimizer optimizer =  new ConjugateGradientOptimizer();
				//Recommender recommender = new SlopeOneRecommender(model);
				
				setSimilarity(model);

				/*
				 * FIXME per ora proviamo solo con la ThresholdUserNeighborhood
				 */
				UserNeighborhood neighborhood = new ThresholdUserNeighborhood(1, userSimilarity, model);

				//	Recommender cachingRecommender = new CachingRecommender(recommender);

				return new GenericUserBasedRecommender(model, neighborhood, userSimilarity);

			}

			private void setSimilarity(DataModel model) throws TasteException
			{
				// TODO Auto-generated method stub
				switch (similarityCode)
				{
					case 0:
						userSimilarity = new PearsonCorrelationSimilarity(model);
						break;
					case 1:
						userSimilarity = new EuclideanDistanceSimilarity(model);
						break;
					case 2:
						userSimilarity = new LogLikelihoodSimilarity(model);
						break;
					case 3:
						itemSimilarity = new PearsonCorrelationSimilarity(model);
						break;

				}
			}
		};

		return evaluator.evaluate(recommenderBuilder, null, model, trainingSet, testSet);
		
		
	}
}
