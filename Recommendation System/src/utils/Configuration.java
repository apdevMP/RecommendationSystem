package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;

/**
 * Classe di utility per la gestione dei parametri di configurazione ricavati
 * dal file config.json
 * 
 * @author apdev
 * 
 */
public class Configuration {

	private String mongo_server_address;
	private int mongo_port;
	private String mongodb_name;
	private String log_collection;
	private String municipalities_collection;
	private String school_collection;
	private String watches_collection;
	private String neo_username;
	private String neo_password;
	private String neo_server_address;
	private int neo_port;
	private int doc_per_page;
	private long user_id;
	private int recommended_items;
	private int items_per_list;
	private int year;
	private static Configuration configuration = null;

	/**
	 * Costruttore di default
	 */
	private Configuration() {

	}

	/**
	 * Restituisce l'istanza di {@link Configuration}
	 * 
	 * @return configurazione del sistema
	 */
	public static Configuration getIstance() {

		if (configuration == null)
			retrieveValues("config.json");
		return configuration;

	}

	/**
	 * Recupera l'indirizzo del server di MongoDB
	 * 
	 * @return the mongo_server_address indirizzo di MongoDB
	 */
	public String getMongo_server_address() {
		return mongo_server_address;
	}

	/**
	 * Recupera la porta del server di MongoDB
	 * 
	 * @return the mongo_port porta di MongoDB
	 */
	public int getMongo_port() {
		return mongo_port;
	}

	/**
	 * Recupera il nome del DB
	 * 
	 * @return the mongodb_name nome DB
	 */
	public String getMongodb_name() {
		return mongodb_name;
	}

	/**
	 * Recupera il nome della collezione del Log di navigazione
	 * 
	 * @return the log_collection nome collezione log di navigazione
	 */
	public String getLog_collection() {
		return log_collection;
	}

	/**
	 * Recupera il nome della collezione dei comuni
	 * 
	 * @return the municipalities_collection nome collezione dei comuni
	 */
	public String getMunicipalities_collection() {
		return municipalities_collection;
	}

	/**
	 * Recupera il nome della collezione delle scuole
	 * 
	 * @return the school_collection nome collezione delle scuole
	 */
	public String getSchool_collection() {
		return school_collection;
	}

	/**
	 * Recupera il nome della collezione dei Watches
	 * 
	 * @return the watches_collection nome collezione dei Wacthes
	 */
	public String getWatches_collection() {
		return watches_collection;
	}

	/**
	 * Recupera l'username per accedere al server di Neo4j
	 * 
	 * @return the neo_username username di accesso a Neo4j
	 */
	public String getNeo_username() {
		return neo_username;
	}

	/**
	 * Recuepera la password di accesso per Neo4j
	 * 
	 * @return the neo_password password di accesso a Neo4j
	 */
	public String getNeo_password() {
		return neo_password;
	}

	/**
	 * Recupera l'indirizzo del server di Neo4j
	 * 
	 * @return the neo_server_address indirizzo di Neo4j
	 */
	public String getNeo_server_address() {
		return neo_server_address;
	}

	/**
	 * Recupera la porta del server di Neo4j
	 * 
	 * @return the neo_port porta di Neo4j
	 */
	public int getNeo_port() {
		return neo_port;
	}

	/**
	 * Recupera il numero di documenti per pagina
	 * 
	 * @return numero di documenti per pagina
	 */
	public int getDoc_per_page() {
		return doc_per_page;
	}

	/**
	 * Recupera l'id dell'utente al quale fare raccomandazioni
	 * 
	 * @return id utente
	 */
	public long getUserId() {
		return user_id;
	}

	/**
	 * Recupera il numero di raccomandazioni max da prelevare
	 * 
	 * @return numero max di raccomandazioni
	 */
	public int getRecommended_items() {
		return recommended_items;
	}

	/**
	 * Recupera il numero di elementi per lista
	 * 
	 * @return numero elementi per lista
	 */
	public int getItem_per_list() {
		return items_per_list;
	}

	/**
	 * Recupera l'anno per filtrare i dati
	 * 
	 * @return anno
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Rimepie tramite la libreria Java GSON l'istanza di {@link Configuration}
	 * 
	 * @param path
	 *            percorso del file
	 */
	private static void retrieveValues(String path) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}
		// configuration = Configuration.getIstance();
		configuration = gson.fromJson(br, Configuration.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Configuration [getMongo_server_address()="
				+ getMongo_server_address() + ", getMongo_port()="
				+ getMongo_port() + ", getMongodb_name()=" + getMongodb_name()
				+ ", getLog_collection()=" + getLog_collection()
				+ ", getMunicipalities_collection()="
				+ getMunicipalities_collection() + ", getSchool_collection()="
				+ getSchool_collection() + ", getWatches_collection()="
				+ getWatches_collection() + ", getNeo_username()="
				+ getNeo_username() + ", getNeo_password()="
				+ getNeo_password() + ", getNeo_server_address()="
				+ getNeo_server_address() + ", getNeo_port()=" + getNeo_port()
				+ "]";
	}

}
