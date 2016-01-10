package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import view.EvaluationWindow;

public class EvaluationController
{

	private EvaluationWindow		evaluationWindow;
	private ActionListener			listener;
	private static UserSimilarity	userSimilarity;
	private int						similarityCode;
	private double					trainingSet;
	private double					testSet;

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
				similarityCode = evaluationWindow.getSimilarity();
				trainingSet = evaluationWindow.getTrainingSet();
				testSet = evaluationWindow.getTestSet();

				double score;
				try
				{
					score = startEvaluation();
					publishScore(score);
				} catch (FileNotFoundException e1)
				{
					JOptionPane.showMessageDialog(null, "Cannot create file data model. File .csv is missing");
					System.exit(0);
					
				} catch (Exception e1)
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

		
		DataModel model = new FileDataModel(new File("matrix_value.csv"));
		

		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();

		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {

			@Override
			public Recommender buildRecommender(final DataModel model) throws TasteException
			{

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
				switch (similarityCode)
				{
					case 0:
						userSimilarity = new PearsonCorrelationSimilarity(model);
						break;
					case 1:
						userSimilarity = new EuclideanDistanceSimilarity(model);
						break;
				}
			}
		};

		return evaluator.evaluate(recommenderBuilder, null, model, trainingSet, testSet);

	}
}
