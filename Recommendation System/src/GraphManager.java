import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class GraphManager
{

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
			Connection con = DriverManager.getConnection("jdbc:neo4j://localhost:7474/",properties);
			Statement stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery("MATCH (n:School) RETURN n.code LIMIT 25");
			while (rs.next())
			{
				System.out.println(rs.getString("n.code"));
			}

		} catch (SQLException | ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
