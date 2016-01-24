package core.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final Logger			LOGGER	= Logger.getLogger(GraphManager.class.getName());

	private GraphManager()
	{
		if (configuration == null)
		{
			configuration = Configuration.getIstance();

		}
		String neo_usernameString = configuration.getNeo_username();
		String neo_passwordString = configuration.getNeo_password();
		connectToGraph(neo_usernameString, neo_passwordString);
	}

	/**
	 * Restituisce l'stanza del manager del database di Neo4j
	 * 
	 * @return
	 */
	public static GraphManager getIstance()
	{

		//Se l'istanza è nulla ne crea una altrimenti la restituisce

		if (manager == null)
		{
			//	System.out.println("manager è null");
			manager = new GraphManager();
		}
		//	else System.out.println("manager non è null");
		return manager;

	}

	public void connectToGraph(String user, String password)
	{
		LOGGER.info("[" + GraphManager.class.getName() + "] Connecting to Neo4j..");

		try
		{
			// Make sure Neo4j Driver is registered
			Class.forName("org.neo4j.jdbc.Driver");
			//Authentication
			Properties properties = new Properties();
			properties.put("user", user);
			properties.put("password", password);
			connection = DriverManager.getConnection("jdbc:neo4j://" + configuration.getNeo_server_address() + ":" + configuration.getNeo_port()
					+ "/", properties);
			connection.clearWarnings();

		} catch (ClassNotFoundException e)
		{
			LOGGER.log(Level.SEVERE, "[" + GraphManager.class.getName() + "]  org.neo4j.jdbc.Driver not found");
			System.exit(1);
		} catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "[" + GraphManager.class.getName() + "]  Cannot connect to Neo4j");
			System.exit(1);
		}

	}

	public String queryMunicipalityCodeFromSchool(String schoolId) throws SQLException
	{
		Statement stmt = connection.createStatement();

		ResultSet rs = stmt.executeQuery("MATCH (n:School {code:'" + schoolId + "'}) RETURN n.municipalityCode LIMIT 1");
		stmt.setQueryTimeout(3);
		String municipalityCode = null;
		while (rs.next())
		{
			String appString = rs.getString("n.municipalityCode");
			if (appString != null)
				municipalityCode = appString;
		}
		stmt.closeOnCompletion();
		return municipalityCode;
	}

	public String queryProvinceCodeFromSchool(String schoolId) throws SQLException
	{
		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(3);
		ResultSet rs = stmt.executeQuery("MATCH (n:School {code:'" + schoolId + "'}) RETURN n.provinceCode LIMIT 1");
		String provinceCode = null;
		while (rs.next())
		{
			String appString = rs.getString("n.provinceCode");
			if (appString != null)
				provinceCode = appString;
		}
		stmt.closeOnCompletion();
		return provinceCode;
	}

	/**
	 * 
	 * Controlla il numero di posti liberi per il dato teachingRole all'interno
	 * di una provincia, come differenza tra il numero di trasferimenti in
	 * uscita e in entrata. Se tale differenza è maggiore di 0 allora la
	 * provincia ha scuole con posti liberi per quella materia di insegnamento
	 * 
	 * @param
	 * @throws SQLException
	 */
	public boolean queryFreePositionInProvince(String provinceCode, String teachingRole) throws SQLException
	{

		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(3);

		ResultSet rs = stmt.executeQuery("MATCH (n:School {provinceCode:'" + provinceCode + "'}) WITH size((n)-[:TRANSFER_MAIN {teachingRoleArea:'"
				+ teachingRole + "'}]->()) as outgoing," + " size((n)<-[:TRANSFER_MAIN {teachingRoleArea:'" + teachingRole + "'}]-()) as incoming,"
				+ " n WHERE (outgoing-incoming)>0 RETURN (outgoing - incoming) as freePositions LIMIT 1");
		/*
		 * la query restituisce un unico risultato nel caso in cui esso sia
		 * positivo, altrimenti l'insieme sarà vuoto e il che vorrà dire che non
		 * ci sono posizioni aperte
		 */

		boolean freePositionAvailable = false;
		while (rs.next())
		{

			if (rs.getInt("freePositions") > 0)
			{
				freePositionAvailable = true;
			}

		}
		stmt.closeOnCompletion();
		return freePositionAvailable;

	}

	/**
	 * 
	 * Controlla il numero di posti liberi per il dato teachingRole all'interno
	 * di un comune, come differenza tra il numero di trasferimenti in uscita e
	 * in entrata. Se tale differenza è maggiore di 0 allora il comune ha scuole
	 * con posti liberi per quella materia di insegnamento
	 * 
	 * @param
	 * @throws SQLException
	 */
	public boolean queryFreePositionInMunicipality(String municipalityCode, String teachingRole) throws SQLException
	{

		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(3);

		ResultSet rs = stmt.executeQuery("MATCH (n:School {municipalityCode:'" + municipalityCode
				+ "'}) WITH size((n)-[:TRANSFER_MAIN {teachingRoleArea:'" + teachingRole + "'}]->()) as outgoing,"
				+ " size((n)<-[:TRANSFER_MAIN {teachingRoleArea:'" + teachingRole + "'}]-()) as incoming,"
				+ " n WHERE (outgoing-incoming)>0 RETURN (outgoing - incoming) as freePositions LIMIT 1"); 

		/*
		 * la query restituisce un unico risultato nel caso in cui esso sia
		 * positivo, altrimenti l'insieme sarà vuoto e il che vorrà dire che non
		 * ci sono posizioni aperte
		 */

		boolean freePositionAvailable = false;
		while (rs.next())
		{

			if (rs.getInt("freePositions") > 0)
			{
				freePositionAvailable = true;
			}

		}

		stmt.closeOnCompletion();
		return freePositionAvailable;

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
		stmt.setQueryTimeout(3);
		int numberOfResults = 0;

		ResultSet rs = stmt.executeQuery("MATCH (n:School {code:'" + schoolId + "'})-[r:TRANSFER_MAIN]->() WHERE r.teachingRoleArea = '"
				+ teachingRole + "' RETURN count(r) as numberOfResults");

		while (rs.next())
		{

			numberOfResults += rs.getInt("numberOfResults");

		}

		stmt.closeOnCompletion();
		return numberOfResults;

	}

	/**
	 * Controlla sul grafo se tra gli utenti trasferiti da quella scuola ce ne
	 * sono alcuni con score relazionabile a quello dell'utente (minore o
	 * uguale)
	 * 
	 * @param schoolId
	 * @param teachingRole
	 * @param score
	 * @return
	 * @throws SQLException
	 */
	public int queryScoreMatching(String schoolId, String teachingRole, Double score) throws SQLException
	{

		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(3);
		int numberOfResults = 0;

		ResultSet rs = stmt.executeQuery("MATCH (n:School {code:'" + schoolId + "'})-[r:TRANSFER_MAIN]->() WHERE r.teachingRoleArea = '"
				+ teachingRole + "' RETURN r.score as resultScore");

		while (rs.next())
		{

			Double resultScore = rs.getDouble("resultScore");
			if (resultScore <= score)
				numberOfResults++;

		}

		stmt.closeOnCompletion();
		//ritorna il numero di matching per la condizione specificata
		return numberOfResults;

	}

	/**
	 * Controlla il numero di posti liberi per il dato teachingRole all'interno
	 * di una scuola, come differenza tra il numero di trasferimenti in uscita e
	 * in entrata. Se tale differenza è maggiore di 0 allora la scuola prevede
	 * posti liberi al suo interno
	 * 
	 * 
	 * @param schoolId
	 * @param teachingRole
	 * @return
	 * @throws SQLException
	 */
	public int queryFreePositionsInSchool(String schoolId, String teachingRole) throws SQLException
	{

		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(3);
		int numberOfResults = 0;

		ResultSet rs = stmt.executeQuery("MATCH (n:School {code:'" + schoolId + "'}) WITH size((n)-[:TRANSFER_MAIN {teachingRoleArea:'"
				+ teachingRole + "'}]->()) as outgoing," + " size((n)<-[:TRANSFER_MAIN {teachingRoleArea:'" + teachingRole + "'}]-()) as incoming,"
				+ " n RETURN (outgoing - incoming) as freePositions");

		//se ci sono stati trasferimenti in uscita per quel teachingRole, allora restituisce true
		while (rs.next())
		{
			numberOfResults += rs.getInt("freePositions");
		}

		stmt.closeOnCompletion();
		return numberOfResults;
	}


	/**
	 * Recupera il nome del comune a partire dal codice identificativo e lo
	 * restituisce
	 * 
	 * @param municipalityCode
	 * @return
	 * @throws SQLException
	 */
	public String queryMunicipalityName(String municipalityCode) throws SQLException, SQLTimeoutException
	{

		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(3);
		stmt.closeOnCompletion();

		ResultSet rs = stmt.executeQuery("MATCH (n:School) WHERE n.municipalityCode = '" + municipalityCode + "' RETURN n.municipalityName LIMIT 1");

		String municipality = null;
		while (rs.next())
		{
			String appString = rs.getString("n.municipalityName");
			if (appString != null)
				municipality = appString;
		}
		stmt.closeOnCompletion();
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
		stmt.setQueryTimeout(3);

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
		stmt.setQueryTimeout(3);
		ArrayList<String> classCodeStrings = new ArrayList<>();

		//dato che in un comune il numero di scuole è limitato,per ora le visualizziamo tutte nella classifica
		ResultSet rs = stmt
				.executeQuery("MATCH (n) WHERE has(n.teachingRoleArea) RETURN DISTINCT \"node\" as element, n.teachingRoleArea AS teachingRoleArea LIMIT 25 UNION ALL MATCH ()-[r]-() WHERE has(r.teachingRoleArea) RETURN DISTINCT \"relationship\" AS element, r.teachingRoleArea AS teachingRoleArea");

		while (rs.next())
		{

			classCodeStrings.add(rs.getString("teachingRoleArea"));

		}
		return classCodeStrings;
	}

	public void closeConnection() throws SQLException
	{
		connection.close();
		manager = null;

	}

}
