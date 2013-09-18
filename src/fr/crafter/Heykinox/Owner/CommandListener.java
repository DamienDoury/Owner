package fr.crafter.Heykinox.Owner;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

public class CommandListener extends PlayerListener
{
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		if(player != null && !message.isEmpty()) 
	    {
			String name = player.getName();
			
			if(message.matches("/[a-z]{3,}([ ][a-zA-Z0-9_]+[ ]?)?")) //Ajout dans le tableau de la liste des joueurs.
			{
				if(message.matches("/trust [a-zA-Z0-9_]+[ ]?")) //SOCIAL : Créé un lien de confiance (la cible pourra construire/détruire sur la propriété du joueur).
				{
					player.sendRawMessage(trust(name, message));
				}
				else if(message.matches("/notrust [a-zA-Z0-9_]+[ ]?")) //SOCIAL : Retire un lien de confiance.
				{
					player.sendRawMessage(notrust(name, message));
				}
				else if(message.matches("/trustlist[ ]?")) //SOCIAL : Affiche la liste des personnes de confiance.
				{
					trustList(name, player);
				}
				else if(message.matches("/paranoid[ ]?")) //SOCIAL : Efface la liste de confiance.
				{
					player.sendRawMessage(paranoid(name));
				}
				else if(message.matches("/own[ ]?")) //COMPORTEMENT : Le joueur possède les blocs qu'il pose.
				{
					player.sendRawMessage(setBehavior(name, message));
				}
				else if(message.matches("/share[ ]?")) //COMPORTEMENT : Le joueur donne la possibilité aux personnes de confiance d'utiliser ses objets.
				{
					player.sendRawMessage(setBehavior(name, message));
				}
				else if(message.matches("/free[ ]?")) //COMPORTEMENT : Le joueur libère ses blocs qu'il touche et ceux qu'il pose.
				{
					player.sendRawMessage(setBehavior(name, message));
				}
				else if(message.matches("/giveup [a-zA-Z0-9_]+[ ]?")) //COMPORTEMENT : Le joueur donne ses blocs qu'il touche et ceux qu'il pose au joueur spécifié.
				{
					player.sendRawMessage(giveUp(name, message));
				}
				else
				{
					player.sendRawMessage("Command error.");
				}
				
				event.setCancelled(true);
			}
	    }
	}
	
	private String trust(String nameFirstPlayer, String message)
	{
		String answer = "Coding error.";
		
		String nameSecondPlayer = "";
		nameSecondPlayer = message.substring(7);
		nameSecondPlayer = nameSecondPlayer.replaceAll("( )+", "");
		
		try
		{
			answer = new DataManager().trust(Global.player_list.get(nameFirstPlayer).get_id(), nameSecondPlayer);
			if(answer.equals("Trust link added."))
			{
				if(Global.player_list.containsKey(nameSecondPlayer))
					Global.player_list.get(nameSecondPlayer).add_trust_link(Global.player_list.get(nameFirstPlayer).get_id());			
			}	
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return answer;
	}
	
	private String notrust(String nameFirstPlayer, String message)
	{
		String answer = "Coding error.";
		
		String nameSecondPlayer = "";
		nameSecondPlayer = message.substring(9);
		nameSecondPlayer = nameSecondPlayer.replaceAll("( )+", "");
		
		try
		{
			answer = new DataManager().notrust(Global.player_list.get(nameFirstPlayer).get_id(), nameSecondPlayer);
			if(answer.equals("Trust link deleted."))
			{
				if(Global.player_list.containsKey(nameSecondPlayer))
					Global.player_list.get(nameSecondPlayer).del_trust_link(Global.player_list.get(nameFirstPlayer).get_id());		
			}	
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return answer;
	}
	
	private void trustList(String name, Player player)
	{
		try
		{
			Global.player_list.get(name).showTrustList(player);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private String paranoid(String name)
	{
		try
		{
			ArrayList<String> trustList = new ArrayList<String>();
			trustList = new DataManager().getTrustList(Global.player_list.get(name).get_id());
			
			if(trustList != null)
			{
				for(String friendName : trustList)
					if(Global.player_list.containsKey(friendName))
						Global.player_list.get(friendName).del_trust_link(Global.player_list.get(name).get_id());
			}
			
			return new DataManager().paranoid(Global.player_list.get(name).get_id());
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return "Coding error.";
	}
	
	private String setBehavior(String name, String message)
	{
		String answer = "Coding error.";
		
		String behavior = "";
		behavior = message.substring(1);
		behavior = behavior.replaceAll("( )+", "");
		
		try
		{			
			answer = new DataManager().setBehavior(Global.player_list.get(name).get_id(), behavior);
			if(answer.equals("Behavior activated."))
				Global.player_list.get(name).setBehavior(behavior);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return answer;
	}
	
	private String giveUp(String nameFirstPlayer, String message)
	{
		String nameSecondPlayer = "";
		nameSecondPlayer = message.substring(8);
		nameSecondPlayer = nameSecondPlayer.replaceAll("( )+", "");
		
		try
		{
			int idTarget = -1;
			idTarget = new DataManager().setGiveUpTarget(Global.player_list.get(nameFirstPlayer).get_id(), nameSecondPlayer);
			if(idTarget > 0)
			{
				Global.player_list.get(nameFirstPlayer).set_id_target(idTarget);
				return setBehavior(nameFirstPlayer, "/giveup");
			}	
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return "Coding error.";
	}
}
