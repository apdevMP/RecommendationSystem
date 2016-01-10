/**
 * 
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;

import core.CustomRecommendedItem;
import core.Profile;
import core.ProfileManager;
import core.RecommenderService;
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

				// recupero dei dati inseriti dall'utente
				window.getTextArea().setText(null);
				String teachingRole = window.getClassCode();
				String region = window.getRegion();
				Double score = window.getScore();
			
				long id = 52657;
				Profile userProfile = ProfileManager.createProfile(id, teachingRole, score, region);
				System.out.println(userProfile.getUserPreferences().size());
				//Profile userProfile = new Profile(id, teachingRole, score, region);
				LOGGER.info("[" + Controller.class.getName() + "] Starting recommendation system for: " + userProfile.toString());
				recommenderService = new RecommenderService(userProfile);

				long startTime = System.nanoTime();

				List<CustomRecommendedItem> recommendedItems = recommenderService.recommendByRegion(region);
				long endTime = System.nanoTime();

				LOGGER.info("Execution time: " + (endTime - startTime));

				showResults(recommendedItems);

			}

		};
		window.getButton().addActionListener(listener);
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
	}
}
