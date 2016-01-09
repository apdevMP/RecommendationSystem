package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import core.GraphManager;

public class StartWindow
{

	private JFrame				frame;
	private JTextField			textFieldScore;
	private JComboBox<String>	comboBoxRegion;
	private JComboBox<String>	comboBoxClass;
	private JButton				btnGo;
	private JTextArea			textArea;
	private static GraphManager	gManager;
	public static Configuration	configuration;

	private String[]			regionStrings	= { "Abruzzo", "Basilicata", "Calabria", "Campania", "Emilia-Romagna", "Friuli-Venezia Giulia",
			"Lazio", "Liguria", "Lombardia", "Molise", "Piemonte", "Puglia", "Sardegna", "Sicilia", "Toscana", "Trentino-Alto Adige", "Umbria",
			"Valle d'Aosta", "Veneto"			};

	/**
	 * Di prova per vedere se l'interfaccia veniva creata bene
	 */
	public static void main(String[] args)
	{

		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				try
				{
					StartWindow window = new StartWindow();

					window.frame.setVisible(true);
					Controller controller = new Controller(window);
					controller.recommend();

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
	public StartWindow()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize()
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 586, 402);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panelTitle = new JPanel();
		panelTitle.setBorder(new TitledBorder(null, "Recommendation System Tester", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panelTitle.setBounds(6, 6, 574, 368);
		frame.getContentPane().add(panelTitle);
		panelTitle.setLayout(null);

		JPanel panelData = new JPanel();
		panelData.setBounds(17, 30, 172, 315);
		panelTitle.add(panelData);
		panelData.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Inserire i propri dati", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panelData.setLayout(null);

		JLabel lblClass = new JLabel("Classe di Concorso");
		lblClass.setBounds(6, 44, 126, 16);
		panelData.add(lblClass);

		//retrieveValues("config.json");

		Configuration.getIstance();

		gManager = GraphManager.getIstance();
		ArrayList<String> classCodesArrayList = new ArrayList<>();
		try
		{
			classCodesArrayList = gManager.retrieveClassCodes();
		} catch (SQLException e1)
		{
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
		btnGo.setBounds(28, 280, 117, 29);
		panelData.add(btnGo);

		JPanel panelResult = new JPanel();
		panelResult.setBackground(Color.WHITE);
		panelResult.setBounds(216, 30, 333, 315);
		panelTitle.add(panelResult);
		panelResult.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 200, 0), new Color(255, 0, 0)), "",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelResult.setLayout(null);

		textArea = new JTextArea();
		textArea.setBounds(6, 6, 304, 303);
		//	scrollPane.add(textArea);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(6, 6, 321, 303);
		panelResult.add(scrollPane);

	}

	public JButton getButton()
	{
		return btnGo;
	}

	public JTextArea getTextArea()
	{
		return textArea;
	}

	public String getRegion()
	{

		return comboBoxRegion.getSelectedItem().toString();
	}

	public Double getScore()
	{
		if (!textFieldScore.getText().isEmpty())
			return Double.parseDouble(textFieldScore.getText());
		else
			return 0.0;
	}

	public String getClassCode()
	{
		return comboBoxClass.getSelectedItem().toString();
	}

}
