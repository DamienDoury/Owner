package fr.crafter.Heykinox.Owner;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;


public class LogListener extends PlayerListener
{	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		if(player != null) 
	    {
			String name = player.getName();
			
			try
			{
				int idPlayer = new DataManager().checkPlayer(name);
				if(idPlayer != -1) //Ajout dans le tableau de la liste des joueurs.
				{
					ArrayList<Integer> friends = new DataManager().getIsTrustedBy(idPlayer);
					String behavior = new DataManager().getBehavior(idPlayer);
					int idTarget = new DataManager().getGiveUpTarget(idPlayer);
					
					OwnerPlayer ownerPlayer = new OwnerPlayer(idPlayer, name, behavior, idTarget, friends);
					Global.player_list.put(name, ownerPlayer);
					
					int nbPlayersConnected = new DataManager().getNumberOfPlayersConnected();
					if(nbPlayersConnected == -1);
					else if(nbPlayersConnected < 2)
						player.sendMessage(nbPlayersConnected + " player connected.");
					else if(nbPlayersConnected < 5)
						player.sendMessage(nbPlayersConnected + " players connected.");
					else if(nbPlayersConnected < 15)
						player.sendMessage(nbPlayersConnected + " players connected !");
					else if(nbPlayersConnected < 50)
						player.sendMessage(nbPlayersConnected + " players connected !!");
					else if(nbPlayersConnected < 150)
						player.sendMessage(nbPlayersConnected + " players connected !!!");
					else if(nbPlayersConnected >= 150)
						player.sendMessage(nbPlayersConnected + " players connected !!!!!");
					
					if(behavior.equals(""));
					else if(behavior.equals("giveup"))
						player.sendMessage("You currently give blocks up to " + new DataManager().getPlayerName(idTarget) + ".");
					else
						player.sendMessage("You currently " + behavior + " blocks.");
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
	    }
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		
		if(player != null) 
	    {
			String name = player.getName();
			
			try
			{
				new DataManager().updatePlayer(name, "last_logout");
				Global.player_list.remove(name); //Suppression dans le tableau de la liste des joueurs.
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
	    }
	}
}