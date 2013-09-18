package fr.crafter.Heykinox.Owner;

import java.sql.*;
import java.util.ArrayList;

public class DataManager
{
	private final static String url = "jdbc:mysql://localhost/Owner_world";
	private final static String login = "root";
	private final static String password = "";
	private Connection connection;
	
	static
	{
		try
		{
			Class.forName(com.mysql.jdbc.Driver.class.getName());
		}
		catch(ClassNotFoundException ex)
		{
			System.out.println("Can not load the Driver");
		}
	}
	
	public DataManager() throws SQLException
	{
		connection = DriverManager.getConnection(url, login, password);
	}
	
	public int checkPlayer(String name)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
					" SELECT id_player, name" +
					" FROM player" +
					" WHERE name LIKE BINARY '" + name + "'");
			
			if(resultat.next())
			{
				updatePlayer(resultat.getString("name"), "last_login");
				return resultat.getInt("id_player");
			}
			else
			{
				return savePlayer(name);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public int savePlayer(String name)
	{
		Statement requete;
		Statement requete2;

		try
		{
			requete = connection.createStatement();
			requete.executeUpdate(
				" INSERT INTO player (name, last_login)" +
				" VALUES ('" + name + "', " +
				" CURRENT_TIMESTAMP)");
			
			requete2 = connection.createStatement();
			ResultSet resultat = requete2.executeQuery(
				" SELECT id_player" +
				" FROM player" +
				" WHERE name LIKE BINARY '" + name + "'");
			
			if(resultat.next())
				return resultat.getInt("id_player");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public void updatePlayer(String name, String field)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			requete.executeUpdate(
				" UPDATE player" +
				" SET " + field + " = CURRENT_TIMESTAMP" +
				" WHERE name LIKE BINARY '" + name + "'");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public String getBehavior(int idPlayer)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
					" SELECT behavior" +
					" FROM player" +
					" WHERE id_player = '" + idPlayer + "'");
			
			if(resultat.next())
				return resultat.getString("behavior");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return "own";
	}
	
	public int getGiveUpTarget(int idPlayer)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
					" SELECT target" +
					" FROM player" +
					" WHERE id_player = '" + idPlayer + "'");
			
			if(resultat.next())
				return resultat.getInt("target");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public String setBehavior(int idPlayer, String behavior)
	{
		Statement requete;

		try
		{			
			requete = connection.createStatement();
			int results = requete.executeUpdate(
					" UPDATE player" +
					" SET behavior = '" + behavior + "'" +
					" WHERE id_player = '" + idPlayer + "'");
			
			if(results > 0)
				return "Behavior activated.";
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return "Error.";
	}
	
	public int setGiveUpTarget(int idPlayer, String targetName)
	{
		Statement requete;

		try
		{
			int idTarget = 0;
			
			if(targetName == "")
				idTarget = idPlayer;
			else
				idTarget = new DataManager().getPlayerId(targetName);
			
			if(idTarget < 1)
				return -1;
			
			requete = connection.createStatement();
			int results = requete.executeUpdate(
					" UPDATE player" +
					" SET target = '" + idTarget + "'" +
					" WHERE id_player = '" + idPlayer + "'");
			
			if(results > 0)
				return idTarget;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public ArrayList<Integer> getIsTrustedBy(int idPlayer)
	{
		ArrayList<Integer> trustList = new ArrayList<Integer>();
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
				" SELECT owner" +
				" FROM trust" +
				" WHERE friend = '" + idPlayer + "'");
			
			while(resultat.next())
				trustList.add(resultat.getInt("owner"));
			
			return trustList;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<String> getTrustList(int idPlayer)
	{
		ArrayList<String> trustList = new ArrayList<String>();
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
				" SELECT p.name AS 'name'" +
				" FROM trust t, player p" +
				" WHERE t.friend = p.id_player" +
				" AND owner = '" + idPlayer + "'");
			
			while(resultat.next())
				trustList.add(resultat.getString("name"));
			
			return trustList;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int getPlayerId(String pseudo)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
					" SELECT id_player" +
					" FROM player" +
					" WHERE name LIKE BINARY '" + pseudo + "'");
			
			if(resultat.next())
				return resultat.getInt("id_player");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public String getPlayerName(int idPlayer)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
					" SELECT name" +
					" FROM player" +
					" WHERE id_player = '" + idPlayer + "'");
			
			if(resultat.next())
				return resultat.getString("name");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return "";
	}
	
	public boolean trustLinkAlreadyExists(int idOwner, int idFriend)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
					" SELECT owner" +
					" FROM trust" +
					" WHERE owner = '" + idOwner + "'" +
					" AND friend = '" + idFriend + "'");
			
			if(resultat.next())
				return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String trust(int idPlayerOne, String pseudo)
	{
		Statement requete;

		try
		{
			int idPlayerTwo = getPlayerId(pseudo);
			
			if(idPlayerTwo == -1)
				return "Unknown player.";
			
			if(trustLinkAlreadyExists(idPlayerOne, idPlayerTwo))
				return "You already trusted " + pseudo + " !";
			
			
			requete = connection.createStatement();
			int results = requete.executeUpdate(
					" INSERT INTO trust(owner, friend)" +
					" VALUES ('" + idPlayerOne + "', '" + idPlayerTwo + "')");
			
			if(results > 0)
				return "Trust link added.";
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return "Error.";
	}
	
	public String notrust(int idPlayerOne, String pseudo)
	{
		Statement requete;

		try
		{
			int idPlayerTwo = getPlayerId(pseudo);
			
			if(idPlayerTwo == -1)
				return "Unknown player.";
			
			requete = connection.createStatement();
			ResultSet result = requete.executeQuery(
					" SELECT owner" +
					" FROM trust" +
					" WHERE owner = '" + idPlayerOne + "'" +
					" AND friend = '" + idPlayerTwo + "'");
			
			if(!result.next())
				return "You didn't trust " + pseudo + " !";
			
			requete = connection.createStatement();
			int results = requete.executeUpdate(
					" DELETE FROM trust" +
					" WHERE owner = '" + idPlayerOne + "'" +
					" AND friend = '" + idPlayerTwo + "'");
			
			if(results > 0)
				return "Trust link deleted.";
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return "Error.";
	}
	
	public String paranoid(int idPlayer)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet result = requete.executeQuery(
					" SELECT friend" +
					" FROM trust" +
					" WHERE owner = '" + idPlayer + "'");
			
			if(!result.next())
				return "You trusted nobody !";
			
			requete = connection.createStatement();
			int results = requete.executeUpdate(
					" DELETE FROM trust" +
					" WHERE owner = '" + idPlayer + "'");
			
			if(results > 0)
				return "Whole trust list deleted.";
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return "Error.";
	}
	
	public int searchOwner(int x, int y, int z)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
					" SELECT owner" +
					" FROM block" +
					" WHERE x = " + x +
					" AND y = " + y +
					" AND z = " + z +
					" LIMIT 0, 1");
			
			if(resultat.next())
				return resultat.getInt("owner");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public int searchFirstOwnerUp(int x, int y, int z)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
					" SELECT owner" +
					" FROM block" +
					" WHERE y >= " + y +
					" AND x = " + x +
					" AND z = " + z +
					" ORDER BY y ASC" +
					" LIMIT 0, 1");
			
			if(resultat.next())
				return resultat.getInt("owner");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public int searchFirstOwnerDown(int x, int y, int z)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet resultat = requete.executeQuery(
					" SELECT owner" +
					" FROM block" +
					" WHERE y <= " + y +
					" AND x = " + x +
					" AND z = " + z +
					" ORDER BY y DESC" +
					" LIMIT 0, 1");
			
			if(resultat.next())
				return resultat.getInt("owner");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public void saveOwner(int x, int y, int z, int idPlayer)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			requete.executeUpdate(
					"INSERT INTO block(x, y, z, owner)" +
					"VALUES('" + x + "', '" + y + "', '" + z + "', '" + idPlayer + "')");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void deleteOwner(int x, int y, int z)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			requete.executeUpdate(
					" DELETE FROM block" +
					" WHERE x = '" + x + "'" +
					" AND y = '" + y + "'" +
					" AND z = '" + z + "'"
					);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getNumberOfPlayersConnected()
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			ResultSet result = requete.executeQuery(
					" SELECT COUNT(id_player)" +
					" FROM player"+
					" WHERE last_logout < last_login");
			
			if(result.next())
				return result.getInt("COUNT(id_player)");
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public void setLogoutToAllConnectedPlayers(String set)
	{
		Statement requete;

		try
		{
			requete = connection.createStatement();
			requete.executeUpdate(
					" UPDATE player" +
					" SET last_logout = " + set +
					" WHERE last_logout < last_login");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
