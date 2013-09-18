package fr.crafter.Heykinox.Owner;

import java.sql.SQLException;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class InteractListener extends PlayerListener
{
	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if(player != null && block != null)
	    {
			int xPose = block.getX();
			int yPose = block.getY();
			int zPose = block.getZ();
			String name = player.getName();

			try
			{
				int owner = new DataManager().searchOwner(xPose, yPose, zPose);
				
				if(owner != -1 && owner != Global.player_list.get(name).get_id() && !Global.player_list.get(name).is_trusted_by(owner))
				{
					event.setCancelled(true);
					if(event.getAction().name().contains("CLICK_BLOCK"))
						player.sendMessage("You aren't able to use the items of " + new DataManager().getPlayerName(owner) + ".");
					
					if(!event.getAction().name().contains("CLICK_BLOCK") 
					&& Global.player_list.get(name).softwareMessageEnabled())
						player.sendMessage("Go away, you aren't able to use the items of " + new DataManager().getPlayerName(owner) + ".");
				}
				else if(owner != -1 && (owner == Global.player_list.get(name).get_id() || Global.player_list.get(name).is_trusted_by(owner)))
				{
					//Note : we allow the player to free blocks that belongs to players who trust him.
					
					String playerBehavior = Global.player_list.get(name).getBehavior();
					
					if(playerBehavior.equals("free") || playerBehavior.equals("giveup"))
					{
						new DataManager().deleteOwner(xPose, yPose, zPose);
						
						if(block.getType().name().contains("DOOR"))
						{
							World world = player.getWorld();
							
							if(world.getBlockAt(xPose, yPose + 1, zPose).getType().name().contains("DOOR"))
								new DataManager().deleteOwner(xPose, yPose + 1, zPose);
							else if(world.getBlockAt(xPose, yPose - 1, zPose).getType().name().contains("DOOR"))
								new DataManager().deleteOwner(xPose, yPose - 1, zPose);
						}
					
						if(playerBehavior.equals("giveup"))
						{
							
							if(!block.getType().name().contains("FIRE"))
								new DataManager().saveOwner(xPose, yPose, zPose, Global.player_list.get(name).get_id_target());
							
							if(block.getType().name().contains("DOOR"))
							{
								World world = player.getWorld();
								
								if(world.getBlockAt(xPose, yPose + 1, zPose).getType().name().contains("DOOR"))
									new DataManager().saveOwner(xPose, yPose + 1, zPose, Global.player_list.get(name).get_id_target());
								else if(world.getBlockAt(xPose, yPose - 1, zPose).getType().name().contains("DOOR"))
									new DataManager().saveOwner(xPose, yPose - 1, zPose, Global.player_list.get(name).get_id_target());
							}
						}
					}
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
	    }
	}
}
