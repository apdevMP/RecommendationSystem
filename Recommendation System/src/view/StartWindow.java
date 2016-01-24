package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JComboBox;

import utils.Configuration;
import controller.Controller;
import core.persistence.GraphManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Classe che gestisce la GUI principale per l'avvio del Recommender
 * 
 * @author apdev
 * 
 */
public class StartWindow {

	private JFrame frame;
	private JTextField textFieldScore;
	private JComboBox<String> comboBoxRegion;
	private JComboBox<String> comboBoxClass;
	private JButton btnGo, btnStop;
	private JTextArea textArea;
	private static GraphManager gManager;
	public static Configuration configuration;

	private String[] regionStrings = { "Abruzzo", "Basilicata", "Calabria",
			"Campania", "Emilia-Romagna", "Friuli-Venezia Giulia", "Lazio",
			"Liguria", "Lombardia", "Molise", "Piemonte", "Puglia", "Sardegna",
			"Sicilia", "Toscana", "Trentino-Alto Adige", "Umbria",
			"Valle d'Aosta", "Veneto" };

	/**
	 * Avvia l'applicazione
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartWindow window = new StartWindow();

					window.frame.setVisible(true);
					Controller controller = new Controller(window);
					controller.recommend();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Costruttore di default
	 */
	public StartWindow() {
		initialize();
	}

	/**
	 * Inizializza i contenuti del frame
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 586, 402);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panelTitle = new JPanel();
		panelTitle.setBorder(new TitledBorder(null,
				"Recommendation System Tester", TitledBorder.CENTER,
				TitledBorder.TOP, null, null));
		panelTitle.setBounds(6, 6, 574, 368);
		frame.getContentPane().add(panelTitle);
		panelTitle.setLayout(null);

		JPanel panelData = new JPanel();
		panelData.setBounds(17, 30, 172, 315);
		panelTitle.add(panelData);
		panelData.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED, null, null), "Inserire i propri dati",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelData.setLayout(null);

		JLabel lblClass = new JLabel("Classe di Concorso");
		lblClass.setBounds(6, 44, 126, 16);
		panelData.add(lblClass);

		// retrieveValues("config.json");

		Configuration.getIstance();

		gManager = GraphManager.getIstance();
		ArrayList<String> classCodesArrayList = new ArrayList<>();
		try {
			classCodesArrayList = gManager.retrieveClassCodes();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String[] classCodesArray = new String[classCodesArrayList.size()];
		classCodesArray = classCodesArrayList.toArray(classCodesArray);

		comboBoxClass = new JComboBox(classCodesArray);
		comboBoxClass.setBounds(6, 62, 154, 27);
		panelData.add(comboBoxClass);

		JLabel lblScore = new JLabel("Punteggio");
		lblScore.setBounds(6, 101, 70, 16);
		panelData.add(lblScore);

		textFieldScore = new JTextField();
		textFieldScore.setBounds(6, 119, 139, 34);
		panelData.add(textFieldScore);
		textFieldScore.setColumns(10);
		textFieldScore.setText("0.0");

		JLabel lblRegion = new JLabel("Regione Provenienza");
		lblRegion.setBounds(6, 176, 151, 16);
		panelData.add(lblRegion);

		comboBoxRegion = new JComboBox(regionStrings);
		comboBoxRegion.setBounds(6, 195, 154, 27);
		panelData.add(comboBoxRegion);

		btnGo = new JButton("Avvia");
		btnGo.setBounds(6, 280, 78, 29);
		panelData.add(btnGo);

		btnStop = new JButton("Chiudi");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});
		btnStop.setBounds(82, 280, 78, 29);
		panelData.add(btnStop);
		btnStop.setEnabled(true);

		JPanel panelResult = new JPanel();
		panelResult.setBackground(Color.WHITE);
		panelResult.setBounds(216, 30, 333, 315);
		panelTitle.add(panelResult);
		panelResult.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED, new Color(255, 200, 0), new Color(255, 0,
						0)), "", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		panelResult.setLayout(null);

		textArea = new JTextArea();
		textArea.setBounds(6, 6, 304, 303);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(null);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(6, 6, 321, 303);
		panelResult.add(scrollPane);

	}

	/**
	 * Recupera il bottone di avvio
	 * 
	 * @return bottone di avvio
	 */
	public JButton getButtonStart() {
		return btnGo;

	}

	/**
	 * Recupera il bottone di stop
	 * 
	 * @return bottone di stop
	 */
	public JButton getButtonStop() {
		return btnStop;
	}

	/**
	 * Recupera l'area di testo della GUI
	 * @return area di testo
	 */
	public JTextArea getTextArea() {
		return textArea;
	}

	/**
	 * Recupera la regione di provenienza dell'utente selezionata sulla GUI
	 * 
	 * @return regione di provenienza
	 */
	public String getRegion() {

		return comboBoxRegion.getSelectedItem().toString();
	}

	/**
	 * Recupera il punteggio della graduatoria inserito
	 * 
	 * @return punteggio inserito
	 * @throws NumberFormatException
	 */
	public Double getScore() throws NumberFormatException {
		if (!textFieldScore.getText().isEmpty())
			return Double.parseDouble(textFieldScore.getText());
		else
			return 0.0;
	}

	/**
	 * Recupera la materia insegnata che è stata selezionata
	 * 
	 * @return materia insegnata
	 */
	public String getClassCode() {
		return comboBoxClass.getSelectedItem().toString();
	}
}
