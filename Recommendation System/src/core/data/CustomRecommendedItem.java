package core.data;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

/*
 * Customizzazione di RecommendedItem, per permettere l'assegnazione di una priorità agli oggetti raccomandati,
 * in modo da poter stilare una classifica degli stessi al momento del filtraggio
 */

public class CustomRecommendedItem implements RecommendedItem, Comparable<CustomRecommendedItem>
{

	private long	id;
	private float	value;			
	private int		ranking	= 0;	
	private String	realID;		

	public CustomRecommendedItem()
	{
	};

	/**
	 * Costruttore
	 * 
	 * @param id		id numerico assegnato per le raccomandazioni
	 * @param value		valore della raccomandazione
	 * @param ranking	valore del punteggio
	 * @param realID	id reale dell'item
	 */
	public CustomRecommendedItem(long id, float value, int ranking, String realID)
	{
		super();
		this.id = id;
		this.value = value;
		this.ranking = ranking;
		this.realID = realID;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(float value)
	{
		this.value = value;
	}

	@Override
	public long getItemID()
	{
		return this.id;
	}

	@Override
	public float getValue()
	{
		return this.value;
	}

	public int getRanking()
	{
		return ranking;
	}

	public void setRanking(int ranking)
	{
		this.ranking = ranking;
	}

	public String getRealID()
	{
		return realID;
	}

	public void setRealID(String realID)
	{
		this.realID = realID;
	}

	/**
	 * Ritorna un numero maggiore di 0 nel caso in cui l'item corrente abbia
	 * punteggio maggiore rispetto a quello passato come argomento.
	 * 
	 * @param customRecommendedItem2
	 * @return
	 */
	@Override
	public int compareTo(CustomRecommendedItem customRecommendedItem2)
	{
		return customRecommendedItem2.getRanking() - this.getRanking();

	}

}
