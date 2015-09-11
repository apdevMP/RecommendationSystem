import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class GraphManager
{

	private Connection	connection;

	public GraphManager()
	{

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
	 * Permette di recuperare le scuole con maggior numero di trasferimenti in uscita all'interno di una provincia
	 * 
	 * @param region
	 * @throws SQLException
	 */
	public void queryMostQuotedSchoolInProvince(String provinceCode) throws SQLException
	{

		Statement stmt = connection.createStatement();

		/*
		 * per ora viene data la classifica in ordine di trasferimenti;
		 * possiamo limitare il numero di risultati aggiungendo LIMIT
		 */
		ResultSet rs = stmt.executeQuery("MATCH (n:School)-[r:TRANSFER_MAIN]->c WHERE n.provinceCode = '"+provinceCode+"' RETURN n.code, count(r) AS number_of_connections ORDER BY number_of_connections DESC");
		while (rs.next())
		{
			System.out.println(rs.getString("n.code")+"  ,#traferimenti in uscita:"+ rs.getString("number_of_connections"));
		}

	}
	
	
	
	/**
	 * Permette di recuperare le scuole con maggior numero di trasferimenti in uscita all'interno di una regione
	 * 
	 * @param region
	 * @throws SQLException
	 */
	public void queryMostQuotedSchoolInRegion(Integer regionId) throws SQLException
	{

		Statement stmt = connection.createStatement();

		/*
		 * per ora viene data la classifica in ordine di trasferimenti
		 * e visualizzati i primi 10 risultati
		 */
		ResultSet rs = stmt.executeQuery("MATCH (n:School)-[r:TRANSFER_MAIN]->c WHERE n.regionId = "+regionId+" RETURN n.code, count(r) AS number_of_connections ORDER BY number_of_connections DESC LIMIT 10");
		while (rs.next())
		{
			System.out.println(rs.getString("n.code")+"  ,#traferimenti in uscita:"+ rs.getString("number_of_connections"));
		}

	}
	
	
	/**
	 * Permette di recuperare le scuole con maggior numero di trasferimenti in uscita all'interno di un comune
	 * 
	 * @param region
	 * @throws SQLException
	 */
	public void queryMostQuotedSchoolInMunicipality(String municipalityCode) throws SQLException
	{

		Statement stmt = connection.createStatement();

		//dato che in un comune il numero di scuole è limitato,per ora le visualizziamo tutte nella classifica
		ResultSet rs = stmt.executeQuery("MATCH (n:School)-[r:TRANSFER_MAIN]->c WHERE n.municipalityCode = '"+municipalityCode+"' RETURN n.code, count(r) AS number_of_connections ORDER BY number_of_connections DESC");
		while (rs.next())
		{
			System.out.println(rs.getString("n.code")+"  ,#traferimenti in uscita:"+ rs.getString("number_of_connections"));
		}

	}

	
	/**
	 * Recupera il nome del comune a partire dal codice identificativo e lo restituisce
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
			if(appString != null)
				municipality = appString;
		}
		return municipality;
	}
}

