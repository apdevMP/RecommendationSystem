package core.data;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

/**
 * Customizzazione di {@link RecommendedItem}, per permettere l'assegnazione di
 * una priorità agli oggetti raccomandati, in modo da poter stilare una
 * classifica degli stessi al momento del filtraggio.
 * 
 * @author apdev
 * 
 */
public class CustomRecommendedItem implements RecommendedItem,
		Comparable<CustomRecommendedItem> {

	private long id; /* Id della località */
	private float value; /* Valore ottenuto dalla raccomandazione */
	private int ranking = 0; /* Valore della classificazione */
	private String realID; /* Id reale della località */

	/**
	 * Costruttore di default
	 */
	public CustomRecommendedItem() {
	};

	/**
	 * Costruttore
	 * 
	 * @param id
	 *            id numerico assegnato per le raccomandazioni
	 * @param value
	 *            valore della raccomandazione
	 * @param ranking
	 *            valore del punteggio
	 * @param realID
	 *            id reale dell'item
	 */
	public CustomRecommendedItem(long id, float value, int ranking,
			String realID) {
		super();
		this.id = id;
		this.value = value;
		this.ranking = ranking;
		this.realID = realID;
	}

	/**
	 * Imposta l'id
	 * 
	 * @param id
	 *            id da settare
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Imposta il valore
	 * 
	 * @param value
	 *            valore da settare
	 */
	public void setValue(float value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mahout.cf.taste.recommender.RecommendedItem#getItemID()
	 */
	@Override
	public long getItemID() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mahout.cf.taste.recommender.RecommendedItem#getValue()
	 */
	@Override
	public float getValue() {
		return this.value;
	}

	/**
	 * Restituisce il valore della classifica
	 * 
	 * @return valore della classifica
	 */
	public int getRanking() {
		return ranking;
	}

	/**
	 * Imposta il valore della classificazione
	 * 
	 * @param ranking
	 *            valore della classifica da impostare
	 */
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	/**
	 * Restituisce il reale id della località
	 * 
	 * @return id reale della località
	 */
	public String getRealID() {
		return realID;
	}

	/**
	 * Imposta il valore reale dell'id della località
	 * 
	 * @param realID
	 *            valore reale della località
	 * 
	 */
	public void setRealID(String realID) {
		this.realID = realID;
	}

	/**
	 * Ritorna un numero maggiore di 0 nel caso in cui l'item corrente abbia
	 * punteggio maggiore rispetto a quello passato come argomento.
	 * 
	 * @param customRecommendedToCompare
	 *            item da confrontare
	 * 
	 * @return valore > 0 se item corrente ha un punteggio maggiore di
	 *         {@code customRecommendedToCompare}
	 */
	@Override
	public int compareTo(CustomRecommendedItem customRecommendedToCompare) {
		return customRecommendedToCompare.getRanking() - this.getRanking();

	}

}
