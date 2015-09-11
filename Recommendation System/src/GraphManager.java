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
	 * Permette di recuperare la provincia con maggior numero di trasferimenti a
	 * partire dalla regione
	 * 
	 * @param region
	 * @throws SQLException
	 */
	public void queryMostQuotedCity(String region) throws SQLException
	{

		Statement stmt = connection.createStatement();

		//		ResultSet rs = stmt.executeQuery("MATCH (n:School) RETURN n.code LIMIT 25");

		//TODO
		ResultSet rs = stmt.executeQuery("MATCH (n:School");
		while (rs.next())
		{
			System.out.println(rs.getString("n.code"));
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

