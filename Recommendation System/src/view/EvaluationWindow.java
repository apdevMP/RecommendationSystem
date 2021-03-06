package view;

import java.awt.EventQueue;

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

/**
 * Classe per la gestione della GUI per la valutazione del Recommender
 * 
 * @author apdev
 * 
 */
public class EvaluationWindow {

	private JFrame frame;
	private JComboBox<String> similarityComboBox;
	private JComboBox<String> neighborhoodComboBox;
	public static final String[] similarityArray = {
			"Pearson Correlation Similarity", "Euclidean Distance Similarity",
			"Tanimoto Coefficient Similarity", "Log Likelihood Similarity" };
	public static final String[] neighborhoodArray = {
			"ThresholdUserNeighborhood", "NearestNUserNeighborhood" };
	private JTextField trainingTextField;
	private JTextField testTextField;
	private JButton btnStart;
	private JLabel scoreLabel, precisionLabel, recallLabel;

	/**
	 * Lancia l'applicazione
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EvaluationWindow window = new EvaluationWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Costruttore di default
	 */
	public EvaluationWindow() {
		initialize();
		EvaluationController controller = new EvaluationController(this);
		try {
			controller.evaluate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inizializza il contenuto dei frame
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 495, 341);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(6, 6, 277, 307);
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED,
				null, null), "Evaluation parameters", TitledBorder.LEADING,
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
		btnStart.setBounds(70, 272, 117, 29);
		panel.add(btnStart);

		JPanel panelScore = new JPanel();
		panelScore.setBounds(295, 28, 194, 271);
		panelScore.setLayout(null);
		panelScore.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED, null, null), "Evaluation",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		frame.getContentPane().add(panelScore);

		scoreLabel = new JLabel();
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreLabel.setFont(new Font("Arial", Font.PLAIN, 13));
		scoreLabel.setBounds(18, 59, 158, 16);
		scoreLabel.setText("...");
		panelScore.add(scoreLabel);

		precisionLabel = new JLabel();
		precisionLabel.setText("...");
		precisionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		precisionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
		precisionLabel.setBounds(18, 134, 158, 16);
		panelScore.add(precisionLabel);

		recallLabel = new JLabel();
		recallLabel.setText("...");
		recallLabel.setHorizontalAlignment(SwingConstants.CENTER);
		recallLabel.setFont(new Font("Arial", Font.PLAIN, 13));
		recallLabel.setBounds(18, 222, 158, 16);
		panelScore.add(recallLabel);

		JLabel lblScore = new JLabel("Score");
		lblScore.setBounds(78, 31, 39, 16);
		panelScore.add(lblScore);

		JLabel lblPrecision = new JLabel("Precision");
		lblPrecision.setBounds(67, 106, 57, 16);
		panelScore.add(lblPrecision);

		JLabel lblRecall = new JLabel("Recall");
		lblRecall.setBounds(78, 194, 39, 16);
		panelScore.add(lblRecall);

	}

	/**
	 * Recupera la tipologia similarità
	 * 
	 * @return tipologia di similarità
	 */
	public int getSimilarity() {

		int choice;
		switch (similarityComboBox.getSelectedItem().toString()) {
		case "Pearson Correlation Similarity":
			choice = 0;
			break;

		case "Euclidean Distance Similarity":
			choice = 1;
			break;

		case "Tanimoto Coefficient Similarity":
			choice = 2;
			break;

		case "Log Likelihood Similarity":
			choice = 3;
			break;

		default:
			choice = 1;
			break;
		}
		return choice;

	}

	/**
	 * Recupera la tipologia di neighborhood
	 * 
	 * @return tipologia di neighborhood
	 */
	public int getNeighborhood() {
		int choice;
		switch (neighborhoodComboBox.getSelectedItem().toString()) {
		case "ThresholdUserNeighborhood":
			choice = 0;
			break;

		case "NearestNUserNeighborhood":
			choice = 1;
			break;

		default:
			choice = 0;
			break;
		}
		return choice;
	}

	/**
	 * Recupera il trainingSet
	 * 
	 * @return trainingSet
	 */
	public Double getTrainingSet() {
		return Double.parseDouble(trainingTextField.getText());
	}

	/**
	 * Recupera il testSet
	 * 
	 * @return testSet
	 */
	public Double getTestSet() {
		return Double.parseDouble(testTextField.getText());
	}

	/**
	 * Recupera il frame
	 * 
	 * @return frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Recupera il bottone di avvio
	 * 
	 * @return bottone di avvio
	 */
	public JButton getBtnStart() {
		return btnStart;
	}

	/**
	 * Imposta {@code btnStart} come bottone di avvio
	 * 
	 * @param btnStart
	 *            bottone da settare
	 */
	public void setBtnStart(JButton btnStart) {
		this.btnStart = btnStart;
	}

	/**
	 * Recupera la label dedicata al puntegio
	 * 
	 * @return label del punteggio
	 */
	public JLabel getScoreLabel() {
		return scoreLabel;
	}

	/**
	 * Imposta {@code scoreLabel} come label per il punteggio
	 * 
	 * @param scoreLabel
	 *            label da impostare
	 */
	public void setScoreLabel(JLabel scoreLabel) {
		this.scoreLabel = scoreLabel;
	}

	/**
	 * Recupera la label dedicata alla precisione
	 * 
	 * @return label per la precisione
	 */
	public JLabel getPrecisionLabel() {
		return precisionLabel;
	}

	/**
	 * Imposta {@code precisionLabel} come label per la precisione
	 * 
	 * @param precisionLabel
	 *            label da impostare
	 */
	public void setPrecisionLabel(JLabel precisionLabel) {
		this.precisionLabel = precisionLabel;
	}

	/**
	 * Recupera la label dedicata al punteggio della recall
	 * 
	 * @return label per la recall
	 */
	public JLabel getRecallLabel() {
		return recallLabel;
	}

	/**
	 * Imposta {@code recallLabel} come label dedicata all puntteggio della
	 * recall
	 * 
	 * @param recallLabel
	 *            label da impostare
	 */
	public void setRecallLabel(JLabel recallLabel) {
		this.recallLabel = recallLabel;
	}
}
