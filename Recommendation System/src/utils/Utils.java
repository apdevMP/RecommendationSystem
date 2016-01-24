package utils;

import java.util.List;

import core.data.UtilityMatrixPreference;

/**
 * Classe che contiene metodi di utilità
 * 
 * @author apdev
 * 
 */
public class Utils {

	/**
	 * Calcola il valore da assegnare all'{@link UtilityMatrixPreference}
	 * 
	 * @param eventType
	 *            tipologia di evento
	 * @return valore calcolato
	 */
	public static int computeValue(long eventType) {
		int value = 0;

		// watch rimosso
		if (eventType == 2) {
			value = 0;
		}
		// watch aggiunto
		if (eventType == 1) {
			value = 2;
		}
		// vale per i log
		if (eventType == 3) {
			value = 1;
		}
		return value;
	}

	/**
	 * Metodo che restituisce il massimo tra {@code a} e {@code b}
	 * 
	 * @param a
	 *            valore intero
	 * @param b
	 *            valore intero
	 * @return max valore massimo
	 */
	public static int getMax(int a, int b) {
		if (a > b)
			return a;
		else
			return b;
	}

	/**
	 * Metodo che permette di verificare se all'interno di {@code list} è
	 * presente la {@link UtilityMatrixPreference} {@code p}
	 * 
	 * @param list
	 *            lista di UtilityMatrixPreference
	 * @param p
	 *            preferenza la cui presenza nella lista è da verificaree
	 * @return l'indice nella lista se presente, -1 altrimenti
	 */
	public static int isInList(List<UtilityMatrixPreference> list,
			UtilityMatrixPreference p) {
		/* Inizializzo la variabile che verifica se la preferenza è presente */
		int present = -1;
		/*
		 * Per ogni preferenza presente nella lista vengono recuperati id
		 * dell'utente,id del posto e tag e vengono confrontati con quelli di p
		 */
		for (int i = 0; i < list.size(); i++) {
			String place = list.get(i).getPlaceId();
			long id = list.get(i).getUserId();
			int tag = list.get(i).getTag();
			if (place.equals(p.getPlaceId()) && id == p.getUserId()
					&& tag == p.getTag()) {
				/*
				 * Se presente si restituisce l'indice della lista nella quale
				 * si trova la preferenza p
				 */
				present = i;
				return present;
			}
		}
		/*
		 * Restituisce -1 perchè la preferenza p non è presente all'interno
		 * della lista
		 */
		return present;
	}

}
