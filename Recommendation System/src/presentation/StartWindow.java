package presentation;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JTextPane;
import java.awt.ScrollPane;
import java.awt.TextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;

public class StartWindow
{

	private JFrame		frame;
	private JTextField	textFieldScore;

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
					StartWindow window = new StartWindow();
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
	public StartWindow()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
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

		JSpinner spinnerClass = new JSpinner();
		spinnerClass.setBounds(6, 62, 154, 34);
		panelData.add(spinnerClass);

		JLabel lblScore = new JLabel("Punteggio");
		lblScore.setBounds(6, 101, 70, 16);
		panelData.add(lblScore);

		textFieldScore = new JTextField();
		textFieldScore.setBounds(6, 119, 139, 34);
		panelData.add(textFieldScore);
		textFieldScore.setColumns(10);

		JLabel lblFascia = new JLabel("Fascia");
		lblFascia.setBounds(6, 158, 61, 16);
		panelData.add(lblFascia);

		JSpinner spinnerFascia = new JSpinner();
		spinnerFascia.setBounds(6, 175, 154, 34);
		panelData.add(spinnerFascia);

		JButton btnGo = new JButton("Avvia");
		btnGo.setBounds(28, 280, 117, 29);
		panelData.add(btnGo);

		JLabel lblRegion = new JLabel("Regione Provenienza");
		lblRegion.setBounds(6, 221, 151, 16);
		panelData.add(lblRegion);

		JSpinner spinnerRegion = new JSpinner();
		spinnerRegion.setBounds(6, 240, 154, 34);
		panelData.add(spinnerRegion);

		JPanel panelResult = new JPanel();
		panelResult.setBackground(Color.WHITE);
		panelResult.setBounds(216, 30, 333, 315);
		panelTitle.add(panelResult);
		panelResult.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 200, 0), new Color(255, 0, 0)), "",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelResult.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(6, 6, 321, 303);
		panelResult.add(scrollPane);

		JTextArea textArea = new JTextArea();
		textArea.setBounds(6, 6, 304, 303);
		scrollPane.add(textArea);

	}
}
