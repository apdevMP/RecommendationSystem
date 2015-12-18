package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import utils.Configuration;

/**
 * @author Vanessa
 * 
 * Classe GraphManager per la gestione delle connessioni e delle query al db di
 * neo4j. Implementato come singleton per una corretta gestione delle
 * connessioni.
 *
 */
public class GraphManager
{

	private Connection				connection;
	private static GraphManager		manager			= null;
	private static Configuration	configuration	= null;

	private GraphManager()
	{
		if (configuration == null)
		{
			configuration = Configuration.getIstance();

		}
		String neo_usernameString = configuration.getNeo_username();
		String neo_passwordString = configuration.getNeo_password();
		System.out.println(configuration.getMongo_port());
		System.out.println("GraphManager, username=" + neo_usernameString + ", password=" + neo_passwordString);
		connectToGraph(neo_usernameString, neo_passwordString);
	}

	/**
	 * Restituisce l'stanza del manager del database di Neo4j
	 * 
	 * @return
	 */
	public static GraphManager getIstance()
	{

		//Se l'istanza � nulla ne crea una altrimenti la restituisce

		if (manager == null)
			manager = new GraphManager();
		return manager;

	}

	public void connectToGraph(String user, String password)
	{

		try
		{
			// Make sure Neo4j Driver is registered
			Class.forName("org.neo4j.jdbc.Driver");

			//Authentication
			Properties properties = new Properties();
			properties.put("user", user);
			properties.put("password", password);

			// Connect
			connection = DriverManager.getConnection("jdbc:neo4j://localhost:7474/", properties);

		} catch (SQLException | ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * TODO da eliminare, non utilizzato
	 * Permette di recuperare le scuole con maggior numero di trasferimenti in
	 * uscita all'interno di una regione
	 * 
	 * @param regionName
	 * @throws SQLException
	 */
	public void queryMostQuotedSchoolsInRegion(String regionName) throws SQLException
	{

		/*
		 * MATCH (n:School {provinceCode:"RG"})-[o:TRANSFER_MAIN]->c OPTIONAL
		 * MATCH (n:School {provinceCode:"RG"})<-[i:TRANSFER_MAIN]-c RETURN
		 * distinct n.municipalityCode, count(o) AS outgoing, count(i) AS input,
		 * count(o)-count(i) AS difference ORDER BY outgoing - input DESC
		 */

		/*
		 * SOLO USCENTI MATCH (n:School
		 * {provinceCode:"RG"})-[o:TRANSFER_MAIN]->c RETURN distinct
		 * n.municipalityCode, n.municipalityName, count(o) AS outgoing ORDER BY
		 * outgoing DESC
		 */

		/*
		 * SOLO ENTRANTI MATCH c-[i:TRANSFER_MAIN]->(n:School
		 * {provinceCode:"RG"}) RETURN distinct n.municipalityCode,
		 * n.municipalityName, count(i) AS incoming ORDER BY incoming DESC
		 */

		Statement stmt = connection.createStatement();

		/*
		 * per ora viene data la classifica in ordine di trasferimenti; possiamo
		 * limitare il numero di risultati aggiungendo LIMIT
		 */
		ResultSet rs = stmt.executeQuery("MATCH (n:School {regionName:'" + regionName + "'}) " + "WITH size((n)-[:TRANSFERS]->()) as outgoing,  "
				+ "size((n)<-[:TRANSFERS]-()) as incoming, n " + "WHERE (outgoing - incoming) > 0 "
				+ "RETURN n.code,incoming, out, (out - in) as diff " + "ORDER BY diff DESC");
		while (rs.next())
		{
			System.out.println(rs.getString("n.code") + "  ,uscenti:" + rs.getInt("outgoing") + ",entranti:" + rs.getInt("incoming") + ",diff:"
					+ rs.getInt("diff"));
		}

	}

	/**
	 * TODO da eliminare, non utilizzato
	 * Permette di recuperare le scuole con maggior numero di trasferimenti in
	 * uscita all'interno di una provincia
	 * 
	 * @param region
	 * @throws SQLException
	 */
	public void queryMostQuotedSchoolInProvince(String provinceCode) throws SQLException
	{

		Statement stmt = connection.createStatement();

		/*
		 * per ora viene data la classifica in ordine di trasferimenti; possiamo
		 * limitare il numero di risultati aggiungendo LIMIT
		 */
		ResultSet rs = stmt.executeQuery("MATCH (n:School)-[r:TRANSFER_MAIN]->c WHERE n.provinceCode = '" + provinceCode
				+ "' RETURN n.code, count(r) AS number_of_connections ORDER BY number_of_connections DESC");
		while (rs.next())
		{
			System.out.println(rs.getString("n.code") + "  ,#traferimenti in uscita:" + rs.getString("number_of_connections"));
		}

	}

	/**
	 * TODO da eliminare, non utilizzato
	 * Permette di recuperare le scuole con maggior numero di trasferimenti in
	 * uscita all'interno di una regione
	 * 
	 * @param region
	 * @throws SQLException
	 */
	public void queryMostQuotedSchoolInRegion(Integer regionId) throws SQLException
	{

		Statement stmt = connection.createStatement();

		/*
		 * per ora viene data la classifica in ordine di trasferimenti e
		 * visualizzati i primi 10 risultati
		 */
		ResultSet rs = stmt.executeQuery("MATCH (n:School)-[r:TRANSFER_MAIN]->c WHERE n.regionId = " + regionId
				+ " RETURN n.code, count(r) AS number_of_connections ORDER BY number_of_connections DESC LIMIT 10");
		while (rs.next())
		{
			System.out.println(rs.getString("n.code") + "  ,#traferimenti in uscita:" + rs.getString("number_of_connections"));
		}

	}

	/**
	 * Controlla se una scuola prevede il teachingRole passato per argomento
	 * come ruolo di insegnamento al suo interno. Si presume che, se ci sono
	 * stati trasferimenti in uscita per quel ruolo, allora la scuola risponda
	 * al requisito.
	 * 
	 * @param schoolId id della scuola
	 * @return true se la scuola ha posti liberi per l'area di insegnamento
	 * specificata
	 * @throws SQLException
	 */
	public int queryTeachingRoleInSchool(String schoolId, String teachingRole) throws SQLException
	{

		Statement stmt = connection.createStatement();
		int numberOfResults = 0;

		ResultSet rs = stmt.executeQuery("MATCH (n:School {code:'" + schoolId + "'})-[r:TRANSFER_MAIN]->() WHERE r.teachingRoleArea = '"
				+ teachingRole + "' RETURN count(r) as numberOfResults");

		//se ci sono stati trasferimenti in uscita per quel teachingRole, allora restituisce true
		while (rs.next())
		{
			System.out.println(rs.getInt("numberOfResults"));
			numberOfResults += rs.getInt("numberOfResults");
		}
		
		return numberOfResults;

	}
	
	/**
	 * Controlla il numero di posti liberi per il dato teachingRole all'interno di una scuola,
	 * come differenza tra il numero di trasferimenti in uscita e in entrata.
	 * Se tale differenza è maggiore di 0 allora la scuola prevede posti liberi al suo interno
	 * 
	 * 
	 * @param schoolId
	 * @param teachingRole
	 * @return
	 * @throws SQLException
	 */
	public int queryFreePositionsInSchool(String schoolId, String teachingRole) throws SQLException{
		
		Statement stmt = connection.createStatement();
		int numberOfResults = 0;

		ResultSet rs = stmt.executeQuery("MATCH (n:School {code:'" + schoolId + "'}) WITH size((n)-[:TRANSFER_MAIN {teachingRoleArea:'"+teachingRole+"'}]->()) as outgoing,"
    +" size((n)<-[:TRANSFER_MAIN {teachingRoleArea:'"+teachingRole+"'}]-()) as incoming,"
    +" n RETURN (outgoing - incoming) as freePositions LIMIT 1 ");

		//se ci sono stati trasferimenti in uscita per quel teachingRole, allora restituisce true
		while (rs.next())
		{
			System.out.println(rs.getInt("freePositions"));
			numberOfResults = rs.getInt("freePositions");
		}
		
		return numberOfResults;
	}

	/**
	 * TODO non utilizzato
	 * Permette di recuperare le scuole con maggior numero di trasferimenti in
	 * uscita all'interno di un comune
	 * 
	 * @param region
	 * @throws SQLException
	 */
	public void queryMostQuotedSchoolInMunicipality(String municipalityCode) throws SQLException
	{

		Statement stmt = connection.createStatement();

		//dato che in un comune il numero di scuole è limitato,per ora le visualizziamo tutte nella classifica
		ResultSet rs = stmt.executeQuery("MATCH (n:School)-[r:TRANSFER_MAIN]->c WHERE n.municipalityCode = '" + municipalityCode
				+ "' RETURN n.code, count(r) AS number_of_connections ORDER BY number_of_connections DESC");
		while (rs.next())
		{
			System.out.println(rs.getString("n.code") + "  ,#traferimenti in uscita:" + rs.getString("number_of_connections"));
		}

	}

	/**
	 * Recupera il nome del comune a partire dal codice identificativo e lo
	 * restituisce
	 * 
	 * @param municipalityCode
	 * @return
	 * @throws SQLException
	 */
	public String queryMunicipalityName(String municipalityCode) throws SQLException
	{

		Statement stmt = connection.createStatement();

		//TODO
		ResultSet rs = stmt.executeQuery("MATCH (n:School) WHERE n.municipalityCode = '" + municipalityCode + "' RETURN n.municipalityName LIMIT 1");
		String municipality = null;
		while (rs.next())
		{
			String appString = rs.getString("n.municipalityName");
			if (appString != null)
				municipality = appString;
		}
		return municipality;
	}

	/**
	 * @param municipalityCode
	 * @return
	 * @throws SQLException
	 */
	public long queryMunicipalityId(String municipalityCode) throws SQLException
	{
		Statement stmt = connection.createStatement();

		//TODO
		ResultSet rs = stmt.executeQuery("MATCH (n:School) WHERE n.municipalityCode = '" + municipalityCode + "' RETURN n.municipalityId LIMIT 1");
		long municipalityId = 0;
		while (rs.next())
		{
			long appString = rs.getLong("n.municipalityId");
			if (appString != 0)
				municipalityId = appString;
		}
		return municipalityId;
	}

	public ArrayList<String> retrieveClassCodes() throws SQLException
	{
		Statement stmt = connection.createStatement();

		ArrayList<String> classCodeStrings = new ArrayList<>();

		//dato che in un comune il numero di scuole è limitato,per ora le visualizziamo tutte nella classifica
		ResultSet rs = stmt
				.executeQuery("MATCH (n) WHERE has(n.teachingRoleArea) RETURN DISTINCT \"node\" as element, n.teachingRoleArea AS teachingRoleArea LIMIT 25 UNION ALL MATCH ()-[r]-() WHERE has(r.teachingRoleArea) RETURN DISTINCT \"relationship\" AS element, r.teachingRoleArea AS teachingRoleArea");
		int count = 0;

		while (rs.next())
		{

			//System.out.println(rs.getString("teachingRoleArea"));
			classCodeStrings.add(rs.getString("teachingRoleArea"));
			count++;

		}
		//System.out.println("#classi:" + count);
		return classCodeStrings;
	}

}
