/**
 * 
 */
package utils;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.text.AbstractDocument.BranchElement;

import com.google.gson.JsonObject;

import core.DBManager;

/**
 * @author Vanessa
 *
 * Classe ConfigParser per il recupero dei dati di accesso ai database dal file
 * di configurazione config.json
 * Implementato come Singleton.
 *
 */
public class ConfigParser
{

	private static ConfigParser	parser	= null;

	/**
	 * Costruttore
	 */
	private ConfigParser()
	{

		retrieveValues();
	}
	
	
	
	/**
	 * Restituisce l'stanza del parser del file di config
	 * 
	 * @return
	 */
	public static ConfigParser getIstance() {

		if (parser == null)
			parser = new ConfigParser();
		return parser;

	}

	/**
	 * 
	 */
	private void retrieveValues()
	{
		//trasforma il contenuto del file config nel formato String
		String jsonString = readConfigFile("config.json");
		
//	    JsonObject jobj = new JSONObject(jsonData);
//	    JSONArray jarr = new JSONArray(jobj.getJSONArray("keywords").toString());
//	    System.out.println("Name: " + jobj.getString("name"));
//	    for(int i = 0; i < jarr.length(); i++) {
//	        System.out.println("Keyword: " + jarr.getString(i));
//	    }
		
		

	}



	/**
	 * Accede al contenuto del file di configurazione e lo trasforma da json a stringa
	 * 
	 * @param path	percorso del file di configurazione
	 * @return		oggetto di tipo String ricavato dal json
	 */
	private String readConfigFile(String path)
	{
		//
		String jsonString = "";
	    try {
	        BufferedReader br = new BufferedReader(new FileReader(path));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            line = br.readLine();
	        }
	        jsonString = sb.toString();
	        br.close();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    
	    return jsonString;
	}
}
