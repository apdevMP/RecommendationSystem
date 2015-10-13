/**
 * 
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

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

	private StartWindow		window;
	private ActionListener	listener;
	private static Configuration configuration;

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
				String teachingRole = window.getClassCode();
				String region = window.getRegion();
				Double score = window.getScore();
				//Integer range = window.getRange();
				long id = 0;

				Profile userProfile = retrieveProfileFromDb(id, teachingRole, score);
				RecommenderService recommender = new RecommenderService(userProfile);
				recommender.recommendByRegion(region);
				// quando viene cliccato il bottone di avvio, comincia la
				// ricerca dei suggerimenti
				//startSearch();

			}

			private Profile retrieveProfileFromDb(long id, String teachingRole, double score)
			{
				Profile profile = null;
				QueryManager qm = new QueryManager();
				profile = qm.retrieveProfile(id, teachingRole, score);
				return profile;
			}
		};
		window.getButton().addActionListener(listener);
	}

	public void startSearch()
	{
		// TODO Auto-generated method stub

		// alla fine dell'elaborazione, stampa in output i risultati
		printResults();
	}

	/**
	 * Riporta i risultati nella JTextArea dedicata sull'interfaccia
	 */
	public void printResults()
	{

		// al posto di null mi aspetto una lista.toString
		window.getTextArea().setText(null);
	}
}
