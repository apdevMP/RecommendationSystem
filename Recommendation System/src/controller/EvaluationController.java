package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import utils.Configuration;
import view.EvaluationWindow;

public class EvaluationController
{

	private EvaluationWindow		evaluationWindow;
	private ActionListener			listener;
	private int						similarityCode;
	private int						neighborhoodCode;
	private double					trainingSet;
	private double					testSet;
	private static Configuration	configuration;

	public EvaluationController(EvaluationWindow window)
	{

		if (configuration == null)
		{
			configuration = Configuration.getIstance();

		}
		this.evaluationWindow = window;
	}

	public void evaluate()
	{
		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				similarityCode = evaluationWindow.getSimilarity();
				neighborhoodCode = evaluationWindow.getNeighborhood();
				trainingSet = evaluationWindow.getTrainingSet();
				testSet = evaluationWindow.getTestSet();

				double score;
				try
				{
					score = startEvaluation();
					publishScore(score);
					computePrecisionRecall();
				} catch (FileNotFoundException e1)
				{
					JOptionPane.showMessageDialog(null, "Cannot create file data model. File .csv is missing");
					System.exit(0);

				}catch (IllegalArgumentException e2) {
					System.out.println("Cannot compute precision recall: "+e2.getMessage());
				}
				
				catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			private void publishScore(double score)
			{
				evaluationWindow.getScoreLabel().setText("" + score);
			}
		};
		evaluationWindow.getBtnStart().addActionListener(listener);
	}

	public double startEvaluation() throws Exception
	{

		RandomUtils.useTestSeed();

		DataModel model = new FileDataModel(new File(configuration.getUserId() + ".csv"));

		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();

		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {

			@Override
			public Recommender buildRecommender(final DataModel model) throws TasteException
			{

				UserNeighborhood neighborhood = setUserNeighborhood(10, getSimilarity(model), model);

				return new GenericUserBasedRecommender(model, neighborhood, getSimilarity(model));

			}

		};

		return evaluator.evaluate(recommenderBuilder, null, model, trainingSet, testSet);

	}

	public void computePrecisionRecall() throws TasteException, IOException
	{
		RandomUtils.useTestSeed();
		DataModel model = new FileDataModel(new File(configuration.getUserId() + ".csv"));
		RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException
			{
				UserNeighborhood neighborhood = setUserNeighborhood(3, getSimilarity(model), model);
				return new GenericUserBasedRecommender(model, neighborhood, getSimilarity(model));
			}
		};
		IRStatistics stats = evaluator.evaluate(recommenderBuilder, null, model, null, 5, 3, 1.0);
		System.out.println("Recall: "+stats.getRecall());
		System.out.println("Precision: "+stats.getPrecision());
		
	}

	/**
	 * Metodo di appoggio per recuperare il tipo di similarità da istanziare a
	 * seconda della scelta fatta nell'interfaccia
	 * 
	 * @param model data model costruito dal desiderato file .csv
	 * @return un oggetto UserSimilarity
	 */
	private UserSimilarity getSimilarity(DataModel model) throws TasteException
	{

		UserSimilarity userSimilarity = null;
		switch (similarityCode)
		{
			case 0:
				userSimilarity = new PearsonCorrelationSimilarity(model);
				break;
			case 1:
				userSimilarity = new EuclideanDistanceSimilarity(model);
				break;

			case 2:
				userSimilarity = new TanimotoCoefficientSimilarity(model);
				break;
			case 3:
				userSimilarity = new LogLikelihoodSimilarity(model);
				break;

		}
		return userSimilarity;
	}

	/**
	 * Metodo di appoggio per recuperare il tipo di Neighborhood da utilizzare
	 * 
	 * @param threshold
	 * @param userSimilarity
	 * @param model
	 * @return
	 * @throws TasteException
	 */
	private UserNeighborhood setUserNeighborhood(double threshold, UserSimilarity userSimilarity, DataModel model) throws TasteException
	{
		UserNeighborhood neighborhood = null;
		if (neighborhoodCode == 0)
			neighborhood = new ThresholdUserNeighborhood(threshold, userSimilarity, model);
		else
		{
			neighborhood = new NearestNUserNeighborhood((int) threshold, userSimilarity, model);
		}
		return neighborhood;
	}
}
