package utils;

import java.util.List;

import core.UtilityMatrixPreference;

public class Utils {

	/**
	 * Metodo che restituisce il massimo tra {@code a} e {@code b}
	 * 
	 * @param a
	 * @param b
	 * @return max
	 */
	public static int getMax(int a, int b) {
		if (a > b)
			return a;
		else
			return b;
	}

	/**
	 * Metodo che permette di verificare se all'interno di {@code list} �
	 * presente la {@link UtilityMatrixPreference} {@code p}
	 * 
	 * @param list
	 *            lista di UtilityMatrixPreference
	 * @param p
	 *            preferenza la cui presenza nella lista � da verificaree
	 * @return l'indice nella lista se presente, -1 altrimenti
	 */
	public static int isInList(List<UtilityMatrixPreference> list,
			UtilityMatrixPreference p) {
		/* Inizializzo la variabile che verifica se la preferenza � presente */
		int present = -1;
		/*
		 * Per ogni preferenza presente nella lista vengono recuperati id
		 * dell'utente,id del posto e tag e vengono confronati con quelli di p
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
		 * Restituisce -1 perch� la preferenza p non � presente all'interno
		 * della lista
		 */
		return present;
	}

}
