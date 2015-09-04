/**
 * 
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

	/**
	 * Costruttore
	 * 
	 * @param window dialog di avvio per il testing
	 */
	public Controller(StartWindow window)
	{
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
				
				//recupero dei dati inseriti dall'utente
				String classString = window.getClassCode();
				String regionString = window.getRegion();
				Double scoreString = window.getScore();
				Integer range = window.getRange();
				
				
				//quando viene cliccato il bottone di avvio, comincia la ricerca dei suggerimenti
				startSearch();

			}
		};
		window.getButton().addActionListener(listener);
	}

	public void startSearch()
	{
		// TODO Auto-generated method stub

		
		
		//alla fine dell'elaborazione, stampa in output i risultati
		printResults();
	}

	/**
	 * Riporta i risultati nella JTextArea dedicata sull'interfaccia
	 */
	public void printResults()
	{

		//al posto di null mi aspetto una lista.toString
		window.getTextArea().setText(null);
	}
}
