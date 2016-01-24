package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import core.data.CustomRecommendedItem;
import core.service.RecommenderService;
import core.service.profile.Profile;
import core.service.profile.ProfileService;
import utils.Configuration;
import view.StartWindow;

/**
 * Controller riferito al pattern MVC per la gestione e la comunicazione tra
 * {@link StartWindow} e {@link RecommenderService}.
 * 
 * @author apdev
 */
public class Controller {

	private StartWindow window;
	private ActionListener listener;
	private static Configuration configuration;
	private RecommenderService recommenderService;
	private static final Logger LOGGER = Logger.getLogger(Controller.class
			.getName());

	/**
	 * Costruttore di default
	 * 
	 * @param window
	 *            dialog di avvio per il testing
	 */
	public Controller(StartWindow window) {
		/* Crea l'istanza di Configuration */
		if (configuration == null) {
			configuration = Configuration.getIstance();

		}
		this.window = window;
	};

	/**
	 * Avvia l'elaborazione dei dati per la ricerca dei risultati da suggerire
	 * all'utente alla pressione del bottone di avvio di {@link StartWindow}
	 */
	public void recommend() {

		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				/* disabilito il bottone di avvio */
				window.getButtonStart().setEnabled(false);
				window.getButtonStop().setEnabled(false);

				/* recupero dei dati inseriti dall'utente */
				window.getTextArea().setSelectionStart(0);
				window.getTextArea().append(null);

				String teachingRole = window.getClassCode();
				String region = window.getRegion();
				Double score = 0.0;
				try {
					score = window.getScore();
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null,
							"Score value not correct. Restart the system");
					System.exit(1);
				}

				/*
				 * Crea il profilo dell'utente sul quale si effettua la
				 * raccomandazione
				 */
				Profile userProfile = ProfileService.createProfile(
						configuration.getUserId(), teachingRole, score, region);

				LOGGER.info("[" + Controller.class.getName()
						+ "] Starting recommendation system for: "
						+ userProfile.toString());
				recommenderService = new RecommenderService(userProfile);

				long startTime = System.currentTimeMillis();

				/* Avvia la raccomandazione */
				List<CustomRecommendedItem> recommendedItems = recommenderService
						.recommendItems(region);
				long endTime = System.currentTimeMillis();

				Long executionTime = new Long((endTime - startTime) / 1000);
				double executionTimeInSeconds = executionTime.doubleValue();
				LOGGER.info("Execution time: " + executionTimeInSeconds
						+ " seconds.");

				/* Mostra i risultati sulla GUI */
				showResults(recommendedItems);

			}

		};
		window.getButtonStart().addActionListener(listener);
	}

	/**
	 * Riporta i risultati nell'apposita JTextArea mostrata in
	 * {@link StartWindow}
	 * 
	 * @param list
	 *            lista delle raccomandazioni da mostrare
	 */
	public void showResults(List<CustomRecommendedItem> list) {
		DecimalFormat numberFormat = new DecimalFormat("#.00");

		for (CustomRecommendedItem item : list)
			window.getTextArea().append(
					item.getRealID() + "\t value:"
							+ numberFormat.format(item.getValue())
							+ "\tranking:" + item.getRanking() + "\n");
		window.getTextArea().append("\n-----\n\n");
		/* una volta stampata la lista, riabilito il bottone di avvio */
		window.getButtonStart().setEnabled(true);

	}
}
