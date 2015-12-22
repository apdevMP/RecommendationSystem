package core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.neo4j.cypherdsl.grammar.ForEach;

/**
 * Questa classe rappresenta la matrice di utilit� ricavata dai dati
 * 
 */
public class UtilityMatrix
{

	private static final String			TARGET				= "target";
	private static final String			TYPE_ID				= "typeId";
	private static final String			KEY					= "key";
	private static final String			LAST_EVENT_DATA		= "lastEventData";
	private static final String			USER_ID				= "userId";
	private static final String			CONTEXT				= "context";
	private static final String			USER_SCORE			= "userScore";
	private static final String			EVENT_TYPE			= "eventType";
	private static final String			SCORE				= "score";
	private static final String			CODE_MUNICIPALITY	= "codeMunicipality";
	private static final String			CODE_PROVINCE		= "codeProvince";
	private static final String			PROVINCE_CODE		= "provinceCode";
	private static final String			ACTION				= "action";
	private static final String			ATTRIBUTES			= "attributes";

	private List<Long>					matrixUser;
	private List<String>				matrixProvince;
	private List<String>				matrixMunicipality;
	private List<String>				matrixSchool;
	private List<ArrayList<Integer>>	provinceValues;
	private List<ArrayList<Integer>>	municipalityValues;
	private List<ArrayList<Integer>>	schoolValues;
	
	private static final Logger LOGGER = Logger.getLogger(UtilityMatrix.class.getName());

	/**
	 * Costruttore di default per la matrice di utilit�
	 */
	public UtilityMatrix()
	{
		matrixUser = new ArrayList<Long>();
		matrixProvince = new ArrayList<String>();
		matrixMunicipality = new ArrayList<String>();
		matrixSchool = new ArrayList<String>();
		provinceValues = new ArrayList<ArrayList<Integer>>();
		municipalityValues = new ArrayList<ArrayList<Integer>>();
		schoolValues = new ArrayList<ArrayList<Integer>>();

	}

	public long getUser(int index)
	{
		return matrixUser.get(index);
	}

	public String getProvince(int index)
	{
		return matrixProvince.get(index);
	}

	public String getSchool(int index)
	{
		return matrixSchool.get(index);
	}

	public String getMunicipality(int index)
	{
		return matrixMunicipality.get(index);
	}

	public List<Long> getUserMatrix()
	{
		return matrixUser;
	}

	public List<String> getProvinceMatrix()
	{
		return matrixProvince;
	}

	public List<String> getMunicipalityMatrix()
	{
		return matrixMunicipality;
	}

	public List<String> getSchoolMatrix()
	{
		return matrixSchool;
	}

	public List<ArrayList<Integer>> getProvinceValues()
	{
		return provinceValues;
	}

	public void setProvinceValues(List<ArrayList<Integer>> provinceValues)
	{
		this.provinceValues = provinceValues;
	}

	public List<ArrayList<Integer>> getMunicipalityValues()
	{
		return municipalityValues;
	}

	public void setMunicipalityValues(List<ArrayList<Integer>> municipalityValues)
	{
		this.municipalityValues = municipalityValues;
	}

	public List<ArrayList<Integer>> getSchoolValues()
	{
		return schoolValues;
	}

	public void setSchoolValues(List<ArrayList<Integer>> schoolValues)
	{
		this.schoolValues = schoolValues;
	}

	/**
	 * Aggiunge un utente, se non presente, alla matrice degli utenti
	 * 
	 * @param doc
	 * 
	 */
	public void addUser(long userId)
	{
		
		if (!matrixUser.contains(userId))
		{
			matrixUser.add(userId);
		}
	}

	/**
	 * Aggiunge una provincia proveniente dai log alla relativa matrice
	 * 
	 * @param doc
	 */
	public void addProvinceFromLog(Document doc)
	{

		//	System.out.println(doc.toJson());
		String province = doc.getString(CODE_PROVINCE);
		if (province == null)
		{
			province = doc.getString(PROVINCE_CODE);
		}
		//	System.out.println("prov:"+province);
		if (!matrixProvince.contains(province))
		{
			matrixProvince.add(province);
		}
	}

	/**
	 * Aggiunge una provincia proveniente dai Watch alla relativa matrice
	 * 
	 * @param doc
	 */
	public void addProvinceFromWatch(Document doc)
	{
		String province = doc.getString(KEY);
		if (!matrixProvince.contains(province))
		{
			matrixProvince.add(province);
		}
	}

	/**
	 * Aggiunge un comune proveniente dai log alla relativa matrice
	 * 
	 * @param doc
	 */
	public void addMunicipalityFromLog(Document doc)
	{
		String municipality = doc.getString(CODE_MUNICIPALITY);
		if (!matrixMunicipality.contains(municipality))
		{
			matrixMunicipality.add(municipality);
		}
	}

	/**
	 * Aggiunge un comune proveniente dai Watch alla relativa matrice
	 * 
	 * @param doc
	 */
	public void addMunicipalityFromWatch(Document doc)
	{
		String municipality = doc.getString(KEY);
		if (!matrixMunicipality.contains(municipality))
		{
			matrixMunicipality.add(municipality);
		}
	}

	/**
	 * Aggiunge una scuola alla relativa matrice
	 * 
	 * @param doc
	 */
	public void addSchool(Document doc)
	{
		String school = doc.getString(KEY);
		if (!matrixSchool.contains(school))
		{
			matrixSchool.add(school);
		}
	}

	/**
	 * Inizializza la matrice dei valori a 0
	 */
	private void initializeValues()
	{
		int i = 0;

		for (ArrayList<Integer> it : provinceValues)
		{
			for (i = 0; i < matrixProvince.size(); i++)
			{
				it.add(0);
			}
		}

		for (ArrayList<Integer> it : municipalityValues)
		{
			for (i = 0; i < matrixMunicipality.size(); i++)
			{
				it.add(0);
			}
		}

		for (ArrayList<Integer> it : schoolValues)
		{
			for (i = 0; i < matrixSchool.size(); i++)
			{
				it.add(0);
			}
		}
	}

	/**
	 * Calcola il valore da assegnare all'interno della matrice
	 * 
	 * @param score
	 * @param userScore
	 * @param eventType
	 * @return valore calcolato
	 */
	private int computeValue(long eventType)
	{
		int value = 0;

		//FIXME riportare a 1 e 2
		if (eventType == 2)
		{
			value = 2;
		} else
		{
			value = 1;
		}

		return value;
	}

	/**
	 * Riempie la matrice di utilit� con i dati recuperati dai Watches
	 * 
	 * @param list lista dei documenti filtrati per regione e per tipologia di
	 * materia insegnata
	 * @param score punteggio dell'utente al quale suggerire luoghi
	 */
	public void fillMatrixWithWatches(List<Document> list)
	{
		// se la lista dei documenti � vuota, non viene riempita la matrice di
		// utilit�
		if (list.size() < 1)
		{
			LOGGER.severe("["+UtilityMatrix.class.getName()+"] WATCHES: You must fill matrix with not empty list");
		}

		for (Document doc : list)
		{
			// Vengono aggiunti gli utenti, se non presenti, e gli array di
			// interi relativi ai valori da assegnare
		//	System.out.println(doc.getLong(USER_ID));
			long userId = doc.getLong(USER_ID);
			this.addUser(userId);
			provinceValues.add(new ArrayList<Integer>());
			municipalityValues.add(new ArrayList<Integer>());
			schoolValues.add(new ArrayList<Integer>());

			// Si recupera il tipo di watch eseguito:
			// 1 : Provincia
			// 2 : Comune
			// 3 : Scuola
			// Viene poi aggiunto il luogo del watch alla relativa matrice
			Document target = (Document) doc.get(TARGET);
			long typeId = target.getLong(TYPE_ID);
			//	System.out.println(typeId);
			switch ((int) typeId)
			{
				case 1:
					this.addProvinceFromWatch(target);
					break;

				case 2:
					this.addMunicipalityFromWatch(target);
					break;

				case 3:
					this.addSchool(target);
					break;
			}

		}

		// vengono inizializzati i valori della matrice
		this.initializeValues();

		for (Document doc : list)
		{
			// Si recupera l'indice dell'array relativo all'utente fornito dal
			// documento e la tipologia del watch
			long userId = doc.getLong(USER_ID);
			int indexUser = matrixUser.indexOf(userId);
			Document target = (Document) doc.get(TARGET);
			long typeId = target.getLong(TYPE_ID);

			int value = 0;
			long eventType = 1;
			switch ((int) typeId)
			{
				case 1:
					// La preferenza � sulla provincia, quindi si recupera
					// l'indice
					// dalla matrice delle province assieme al valore del punteggio
					// dell'utente ed infine si calcola il valore da assegnare
					String province = target.getString(KEY);
					int indexProvince = matrixProvince.indexOf(province);
					Document ledProvince = (Document) doc.get(LAST_EVENT_DATA);

					
					if(ledProvince != null){
						eventType = ledProvince.getLong(EVENT_TYPE);
					}
					value = computeValue(eventType);

					provinceValues.get(indexUser).set(indexProvince, value);
					break;

				case 2:
					// La preferenza � sul comune, quindi si recupera l'indice
					// dalla matrice del comune assieme al valore del punteggio
					// dell'utente ed infine si calcola il valore da assegnare
					String municipality = target.getString(KEY);
					int indexMunicipality = matrixMunicipality.indexOf(municipality);
					Document ledMunicipality = (Document) doc.get(LAST_EVENT_DATA);

				
					if(ledMunicipality != null){
						eventType = ledMunicipality.getLong(EVENT_TYPE);
					}
					value = computeValue(eventType);

					municipalityValues.get(indexUser).set(indexMunicipality, value);
					break;

				case 3:
					// La preferenza � sulla scuola, quindi si recupera l'indice
					// dalla matrice delle scuole assieme al valore del punteggio
					// dell'utente ed infine si calcola il valore da assegnare
					String school = target.getString(KEY);
					int indexSchool = matrixSchool.indexOf(school);
				//	System.out.println("xxxxxxxxxxxxxxxxxxxx");
					//System.out.println(doc.toJson());
					Document ledSchool = (Document) doc.get(LAST_EVENT_DATA);
					
			
					if(ledSchool != null){
						eventType = ledSchool.getLong(EVENT_TYPE);
					}
					value = computeValue(eventType);

					schoolValues.get(indexUser).set(indexSchool, value);
					break;
			}
		}
	}

	/**
	 * Riempie la matrice di utilit� con i dati recuperati dal log
	 * 
	 * @param list lista dei documenti filtrati per regione e per tipologia di
	 * materia insegnata
	 * @param score punteggio dell'utente al quale suggerire luoghi
	 */
	public void fillMatrixWithLogs(List<Document> list)
	{
		// se la lista dei documenti � vuota, non viene riempita la matrice di
		// utilit�
		if (list.size() < 1)
		{
			LOGGER.severe("["+UtilityMatrix.class.getName()+"] LOGS: You must fill matrix with not empty list");
		}

		for (Document doc : list)
		{
			// Vengono aggiunti gli utenti, se non presenti, e gli array di
			// interi relativi ai valori da assegnare
		//	System.out.println("+++++++++++");
		//	System.out.println(doc.toJson());
			Long userId = doc.getLong(USER_ID);
			if(userId == null)
				continue;
			this.addUser(userId);
			provinceValues.add(new ArrayList<Integer>());
			municipalityValues.add(new ArrayList<Integer>());

			// Si recupera il tipo di log eseguito dal campo "action":
			// webapi_municipality_aggregates : Provincia
			// webapi_school_aggregates : Comune
			// Viene poi aggiunto il luogo del log alla relativa matrice
			Document attributes = (Document) doc.get(ATTRIBUTES);
			String action = doc.getString(ACTION);
			switch (action)
			{
				case "webapi_municipality_aggregates":
					this.addProvinceFromLog(attributes);
					break;

				case "webapi_school_aggregates":
					this.addMunicipalityFromLog(attributes);
					break;

				case "webapi_get_best_schools":
					this.addProvinceFromLog(attributes);
					break;
			}

		}

		// vengono inizializzati i valori della matrice
		this.initializeValues();

		for (Document doc : list)
		{
			// Si recupera l'indice dell'array relativo all'utente fornito dal
			// documento e la tipologia di azione del log
			Long userId = doc.getLong(USER_ID);
			if(userId == null)
				continue;
			int indexUser = matrixUser.indexOf(userId);
			Document attributes = (Document) doc.get(ATTRIBUTES);
			String action = doc.getString(ACTION);

			int value = 0;
			switch (action)
			{
				case "webapi_municipality_aggregates":
					// La preferenza � sulla provincia, quindi si recupera l'indice
					// dalla matrice delle province assieme al valore del punteggio
					// dell'utente ed infine si calcola il valore da assegnare
					String province = attributes.getString(CODE_PROVINCE);
					int indexProvince = matrixProvince.indexOf(province);

					value = computeValue(1);

					provinceValues.get(indexUser).set(indexProvince, value);
					break;

				case "webapi_school_aggregates":
					// La preferenza � sul comune, quindi si recupera l'indice
					// dalla matrice del comune assieme al valore del punteggio
					// dell'utente ed infine si calcola il valore da assegnare
					String municipality = attributes.getString(CODE_MUNICIPALITY);
					int indexMunicipality = matrixMunicipality.indexOf(municipality);

					value = computeValue(1);

					municipalityValues.get(indexUser).set(indexMunicipality, value);
					break;
			}
		}

	}

	/**
	 * Stampa la matrice di utilit�
	 */
	public void printUtilityMatrix()
	{
		int i = 0;
		for (Long userId : matrixUser)
		{
			System.out.println("[User:" + userId + "]");
			int j = 0;
			for (String province : matrixProvince)
			{
				System.out.println("Province:" + province + " Value: [" + provinceValues.get(i).get(j) + "]");
				j++;
			}
			j = 0;
			for (String municipality : matrixMunicipality)
			{
				System.out.println("Municipality:" + municipality + " Value: [" + municipalityValues.get(i).get(j) + "]");
				j++;
			}
			j = 0;
			for (String school : matrixSchool)
			{

				System.out.println("School:" + school + " Value: [" + schoolValues.get(i).get(j) + "]");
				j++;
			}
			i++;
		}
	}

	public void setValueByUserAndProvince(long user, String province, int value)
	{

		int indexUser = matrixUser.indexOf(user);
		int indexProvince = matrixProvince.indexOf(province);

		provinceValues.get(indexUser).set(indexProvince, value);

	}

	public int getValueByUserAndProvince(long user, String province)
	{
		int value = 0;
		int indexUser = matrixUser.indexOf(user);
		int indexProvince = matrixProvince.indexOf(province);
		value = provinceValues.get(indexUser).get(indexProvince);
		return value;

	}

	public void setValueByUserAndMunicipality(long userId, String municipality, int value)
	{
		int indexUser = matrixUser.indexOf(userId);
		int indexMunicipality = matrixMunicipality.indexOf(municipality);

		municipalityValues.get(indexUser).set(indexMunicipality, value);
	}

	public int getValueByUserAndMunicipality(long user, String municipality)
	{
		int value = 0;
		int indexUser = matrixUser.indexOf(user);
		int indexMunicipality = matrixMunicipality.indexOf(municipality);
		value = municipalityValues.get(indexUser).get(indexMunicipality);
		return value;

	}

	public void setValueByUserAndSchool(long user, String school, int value)
	{
		int indexUser = matrixUser.indexOf(user);
		int indexSchool = matrixSchool.indexOf(school);

		schoolValues.get(indexUser).set(indexSchool, value);

	}

	public int getValueByUserAndSchool(long user, String school)
	{
		int value = 0;
		int indexUser = matrixUser.indexOf(user);
		int indexSchool = matrixSchool.indexOf(school);
		value = schoolValues.get(indexUser).get(indexSchool);
		return value;
	}

}
