package fr.crafter.Heykinox.Owner;

import java.sql.SQLException;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockListener;

public class BuildListener extends BlockListener
{
	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getBlockPlaced();

		if(player != null && block != null) 
	    {
			int xPose = block.getX();
			int yPose = block.getY();
			int zPose = block.getZ();
			String name = player.getName();
			
			try
			{
				int owner = new DataManager().searchFirstOwnerDown(xPose, yPose, zPose);
				
				if(owner == -1 || owner == Global.player_list.get(name).get_id() || Global.player_list.get(name).is_trusted_by(owner)) //Si la zone n'appartient à personne ou à nous, on peut construire dessus.
				{
					String playerBehavior = Global.player_list.get(name).getBehavior();
					
					if(playerBehavior.equals("own"))
					{
						if(!block.getType().name().contains("FIRE"))
							new DataManager().saveOwner(xPose, yPose, zPose, Global.player_list.get(name).get_id());
						
						if(block.getType().name().contains("DOOR"))
							new DataManager().saveOwner(xPose, yPose + 1, zPose, Global.player_list.get(name).get_id());
					}
				}
				else
				{
					event.setCancelled(true);
					player.sendMessage("You can't build on the property of " + new DataManager().getPlayerName(owner) + " !");
				}	
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
	    }
	}
}