import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GraphManager {

	public GraphManager() {

	}

	public void connectToGraph() {

		try {
			// Make sure Neo4j Driver is registered
			Class.forName("org.neo4j.jdbc.Driver");

			// Connect
			Connection con = DriverManager
					.getConnection("jdbc:neo4j://localhost:7474/");
			Statement stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery("MATCH (n:User) RETURN n.name");
			while (rs.next()) {
				System.out.println(rs.getString("n.name"));
			}

		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
