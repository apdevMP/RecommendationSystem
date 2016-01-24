package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;

/**
 * @author Vanessa
 *
 */
public class Configuration
{

	private String					mongo_server_address;
	private int						mongo_port;
	private String					mongodb_name;
	private String					log_collection;
	private String					municipalities_collection;
	private String					school_collection;
	private String					watches_collection;
	private String					neo_username;
	private String					neo_password;
	private String					neo_server_address;
	private int						neo_port;
	private int						doc_per_page;
	private long					user_id;
	private int						recommended_items;
	private int						items_per_list;
	private int						year;
	private static Configuration	configuration	= null;

	private Configuration()
	{

	}

	/**
	 * Restituisce l'stanza del manager del database di Neo4j
	 * 
	 * @return
	 */
	public static Configuration getIstance()
	{

		if (configuration == null)
			//configuration = new Configuration();
			retrieveValues("config.json");
		return configuration;

	}

	/**
	 * @return the mongo_server_address
	 */
	public String getMongo_server_address()
	{
		return mongo_server_address;
	}

	/**
	 * @param mongo_server_address the mongo_server_address to set
	 */
	public void setMongo_server_address(String mongo_server_address)
	{
		this.mongo_server_address = mongo_server_address;
	}

	/**
	 * @return the mongo_port
	 */
	public int getMongo_port()
	{
		return mongo_port;
	}

	/**
	 * @param mongo_port the mongo_port to set
	 */
	public void setMongo_port(int mongo_port)
	{
		this.mongo_port = mongo_port;
	}

	/**
	 * @return the mongodb_name
	 */
	public String getMongodb_name()
	{
		return mongodb_name;
	}

	/**
	 * @param mongodb_name the mongodb_name to set
	 */
	public void setMongodb_name(String mongodb_name)
	{
		this.mongodb_name = mongodb_name;
	}

	/**
	 * @return the log_collection
	 */
	public String getLog_collection()
	{
		return log_collection;
	}

	/**
	 * @param log_collection the log_collection to set
	 */
	public void setLog_collection(String log_collection)
	{
		this.log_collection = log_collection;
	}

	/**
	 * @return the municipalities_collection
	 */
	public String getMunicipalities_collection()
	{
		return municipalities_collection;
	}

	/**
	 * @param municipalities_collection the municipalities_collection to set
	 */
	public void setMunicipalities_collection(String municipalities_collection)
	{
		this.municipalities_collection = municipalities_collection;
	}

	/**
	 * @return the school_collection
	 */
	public String getSchool_collection()
	{
		return school_collection;
	}

	/**
	 * @param school_collection the school_collection to set
	 */
	public void setSchool_collection(String school_collection)
	{
		this.school_collection = school_collection;
	}

	/**
	 * @return the watches_collection
	 */
	public String getWatches_collection()
	{
		return watches_collection;
	}

	/**
	 * @param watches_collection the watches_collection to set
	 */
	public void setWatches_collection(String watches_collection)
	{
		this.watches_collection = watches_collection;
	}

	/**
	 * @return the neo_username
	 */
	public String getNeo_username()
	{
		return neo_username;
	}

	/**
	 * @param neo_username the neo_username to set
	 */
	public void setNeo_username(String neo_username)
	{
		this.neo_username = neo_username;
	}

	/**
	 * @return the neo_password
	 */
	public String getNeo_password()
	{
		return neo_password;
	}

	/**
	 * @param neo_password the neo_password to set
	 */
	public void setNeo_password(String neo_password)
	{
		this.neo_password = neo_password;
	}

	/**
	 * @return the neo_server_address
	 */
	public String getNeo_server_address()
	{
		return neo_server_address;
	}

	/**
	 * @param neo_server_address the neo_server_address to set
	 */
	public void setNeo_server_address(String neo_server_address)
	{
		this.neo_server_address = neo_server_address;
	}

	/**
	 * @return the neo_port
	 */
	public int getNeo_port()
	{
		return neo_port;
	}

	/**
	 * @param neo_port the neo_port to set
	 */
	public void setNeo_port(int neo_port)
	{
		this.neo_port = neo_port;
	}

	public int getDoc_per_page()
	{
		return doc_per_page;
	}

	public void setDoc_per_page(int doc_per_page)
	{
		this.doc_per_page = doc_per_page;
	}

	public long getUserId()
	{
		return user_id;
	}

	public void setUserId(long user_id)
	{
		this.user_id = user_id;
	}

	public int getRecommended_items()
	{
		return recommended_items;
	}

	public void setRecommended_items(int recommended_items)
	{
		this.recommended_items = recommended_items;
	}

	public int getItem_per_list()
	{
		return items_per_list;
	}

	public void setItem_per_list(int item_per_list)
	{
		this.items_per_list = item_per_list;
	}

	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	private static void retrieveValues(String path)
	{
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//configuration = Configuration.getIstance();
		configuration = gson.fromJson(br, Configuration.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Configuration [getMongo_server_address()=" + getMongo_server_address() + ", getMongo_port()=" + getMongo_port()
				+ ", getMongodb_name()=" + getMongodb_name() + ", getLog_collection()=" + getLog_collection() + ", getMunicipalities_collection()="
				+ getMunicipalities_collection() + ", getSchool_collection()=" + getSchool_collection() + ", getWatches_collection()="
				+ getWatches_collection() + ", getNeo_username()=" + getNeo_username()
				+ ", getNeo_password()=" + getNeo_password() + ", getNeo_server_address()=" + getNeo_server_address() + ", getNeo_port()="
				+ getNeo_port() + "]";
	}

}
