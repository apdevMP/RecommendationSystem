package view;

import java.awt.EventQueue;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;

import controller.EvaluationController;
import java.awt.Font;

public class EvaluationWindow
{

	private JFrame					frame;
	private JComboBox<String>		similarityComboBox;
	private JComboBox				neighborhoodComboBox;
	public static final String[]	similarityArray		= { "Pearson Correlation Similarity", "Euclidean Distance Similarity",
			"Log Likelihood Similarity", "Item Pearson Correlation Similarity" };
	public static final String[]	neighborhoodArray	= { "ThresholdUserNeighborhood" };
	private JTextField				trainingTextField;
	private JTextField				testTextField;
	private JButton					btnStart;
	private JLabel					scoreLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				try
				{
					EvaluationWindow window = new EvaluationWindow();
					window.frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EvaluationWindow()
	{
		initialize();
		EvaluationController controller = new EvaluationController(this);
		try
		{
			controller.evaluate();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("unchecked")
	private void initialize()
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 495, 326);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(6, 6, 277, 292);
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Evaluation parameters", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		frame.getContentPane().add(panel);

		JLabel lblSimilarity = new JLabel("Similarity");
		lblSimilarity.setBounds(16, 35, 80, 16);
		panel.add(lblSimilarity);

		similarityComboBox = new JComboBox(similarityArray);
		similarityComboBox.setBounds(6, 51, 253, 27);
		panel.add(similarityComboBox);

		JLabel lblNeighborhood = new JLabel("Neighborhood");
		lblNeighborhood.setBounds(16, 90, 107, 16);
		panel.add(lblNeighborhood);

		neighborhoodComboBox = new JComboBox(neighborhoodArray);
		neighborhoodComboBox.setBounds(6, 104, 253, 27);
		panel.add(neighborhoodComboBox);

		JLabel lblTrainingSet = new JLabel("Training Set");
		lblTrainingSet.setBounds(16, 143, 107, 16);
		panel.add(lblTrainingSet);

		trainingTextField = new JTextField();
		trainingTextField.setBounds(16, 162, 134, 28);
		panel.add(trainingTextField);
		trainingTextField.setColumns(10);
		trainingTextField.setText("0.5");

		JLabel lblTestSet = new JLabel("Test Set");
		lblTestSet.setBounds(16, 202, 107, 16);
		panel.add(lblTestSet);

		testTextField = new JTextField();
		testTextField.setBounds(16, 220, 134, 28);
		panel.add(testTextField);
		testTextField.setColumns(10);
		testTextField.setText("1.0");

		btnStart = new JButton("Avvia");
		btnStart.setBounds(70, 260, 117, 29);
		panel.add(btnStart);

		JPanel panelScore = new JPanel();
		panelScore.setBounds(295, 16, 194, 271);
		panelScore.setLayout(null);
		panelScore.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Score", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		frame.getContentPane().add(panelScore);

		scoreLabel = new JLabel();
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreLabel.setFont(new Font("Arial", Font.BOLD, 13));
		scoreLabel.setBounds(18, 122, 158, 16);
		scoreLabel.setText(null);
		panelScore.add(scoreLabel);

	}

	public int getSimilarity()
	{

		int choice;
		switch (similarityComboBox.getSelectedItem().toString())
		{
			case "Pearson Correlation Similarity":

				choice = 0;
				break;
			case "Euclidean Distance Similarity":
				choice = 1;
				break;
			case "Log Likelihood Similarity":
				choice = 2;
				break;
			case "Item Pearson Correlation Similarity":
				choice = 3;
				break;
			default:
				choice = 1;
				break;
		}
		return choice;

	}

	public Double getTrainingSet()
	{
		return Double.parseDouble(trainingTextField.getText());
	}

	public Double getTestSet()
	{
		return Double.parseDouble(testTextField.getText());
	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame()
	{
		return frame;
	}

	/**
	 * @return the btnStart
	 */
	public JButton getBtnStart()
	{
		return btnStart;
	}

	/**
	 * @param btnStart the btnStart to set
	 */
	public void setBtnStart(JButton btnStart)
	{
		this.btnStart = btnStart;
	}

	/**
	 * @return the scoreLabel
	 */
	public JLabel getScoreLabel()
	{
		return scoreLabel;
	}

	/**
	 * @param scoreLabel the scoreLabel to set
	 */
	public void setScoreLabel(JLabel scoreLabel)
	{
		this.scoreLabel = scoreLabel;
	}
}
