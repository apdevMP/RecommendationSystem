/**
 * 
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import core.CustomRecommendedItem;
import core.Profile;
import core.QueryManager;
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
	private RecommenderService 		recommenderService;

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
			//	Double score = window.getScore();
				Double score = 2.0;
				long id = 0;

			//	Profile userProfile = retrieveProfileFromDb(id, teachingRole, score);
				Profile userProfile = new Profile(id, teachingRole, score, region);
				System.out.println("Profile:"+userProfile.toString());
				recommenderService = new RecommenderService(userProfile);
				//System.out.println("Region:"+region);
				List<CustomRecommendedItem> recommendedItems = recommenderService.recommendByRegion(region);

				
				showResults(recommendedItems);

			}

			private Profile retrieveProfileFromDb(long id, String teachingRole, double score, String position)
			{
				Profile profile = null;
				QueryManager qm = new QueryManager();
				profile = qm.retrieveProfile(id, teachingRole, score, position);
				return profile;
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
		
		for(CustomRecommendedItem item : list)
		window.getTextArea().append(item.getRealID() + "\t value:" + numberFormat.format(item.getValue()) + "\tranking:" + item.getRanking()+"\n");
	}
}
