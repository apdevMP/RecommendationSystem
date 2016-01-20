/**
 * 
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import core.data.CustomRecommendedItem;
import core.data.UtilityMatrixPreference;
import core.service.RecommenderService;
import core.service.profile.Profile;
import core.service.profile.ProfileService;
import utils.Configuration;
import view.StartWindow;

/**
 * @author apdev
 * 
 * Controller per la gestione dell'interfaccia e dell'elaborazione dei dati
 * inseriti.
 * 
 */
public class Controller
{

	private StartWindow				window;
	private ActionListener			listener;
	private static Configuration	configuration;
	private RecommenderService		recommenderService;
	private static final Logger		LOGGER	= Logger.getLogger(Controller.class.getName());

	/**
	 * Costruttore
	 * 
	 * @param window dialog di avvio per il testing
	 */
	public Controller(StartWindow window)
	{
		if (configuration == null)
		{
			configuration = Configuration.getIstance();

		}
		this.window = window;
	};

	/**
	 * Avvia l'elaborazione dei dati per la ricerca dei risultati da suggerire
	 * all'utente, alla pressione del bottone di avvio dell'interfaccia
	 */
	public void recommend()
	{

		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{

				//disabilito il bottone di avvio
				window.getButtonStart().setEnabled(false);
				window.getButtonStop().setEnabled(false);

				// recupero dei dati inseriti dall'utente
				window.getTextArea().setSelectionStart(0);
				window.getTextArea().append(null);

				String teachingRole = window.getClassCode();
				String region = window.getRegion();
				Double score = 0.0;
				try
				{
					score = window.getScore();
				} catch (NumberFormatException ex)
				{
					JOptionPane.showMessageDialog(null, "Score value not correct. Restart the system");
					System.exit(1);
				}

				Profile userProfile = ProfileService.createProfile(configuration.getUserId(), teachingRole, score, region);
				System.out.println(userProfile.getUserPreferences().size());
				System.out.println(configuration.getYear());
				for (UtilityMatrixPreference u : userProfile.getUserPreferences())
				{
					System.out.println("Preference: " + u.getPlaceId() + " - " + u.getScore());
				}
				LOGGER.info("[" + Controller.class.getName() + "] Starting recommendation system for: " + userProfile.toString());
				recommenderService = new RecommenderService(userProfile);

				long startTime = System.currentTimeMillis();

				List<CustomRecommendedItem> recommendedItems = recommenderService.recommendItems(region);
				long endTime = System.currentTimeMillis();

				Long executionTime = new Long((endTime - startTime) / 1000);
				double executionTimeInSeconds = executionTime.doubleValue();
				LOGGER.info("Execution time: " + executionTimeInSeconds + " seconds.");

				showResults(recommendedItems);

			}

		};
		window.getButtonStart().addActionListener(listener);
	}

	/**
	 * Riporta i risultati nella JTextArea dedicata sull'interfaccia
	 */
	public void showResults(List<CustomRecommendedItem> list)
	{
		DecimalFormat numberFormat = new DecimalFormat("#.00");

		for (CustomRecommendedItem item : list)
			window.getTextArea().append(item.getRealID() + "\t value:" + numberFormat.format(item.getValue()) + "\tranking:" + item.getRanking()
					+ "\n");
		window.getTextArea().append("\n-----\n\n");
		//una volta stampata la lista, riabilito il bottone di avvio
		window.getButtonStart().setEnabled(true);

	}
}
