package fr.crafter.Heykinox.Owner;

import java.sql.SQLException;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;

public class DestroyListener extends BlockListener
{	
	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getBlock();

		String warn = "Boom";
		
		if(player != null && block != null)
	    {
			int xPose = block.getX();
			int yPose = block.getY();
			int zPose = block.getZ();
			String name = player.getName();
			
			try
			{
				int owner = new DataManager().searchOwner(xPose, yPose, zPose);
				if(owner == Global.player_list.get(name).get_id() || Global.player_list.get(name).is_trusted_by(owner)) //Si on est le propriétaire du bloc que l'on casse, on l'efface de la BDD.
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
				}
				else
				{
					owner = new DataManager().searchFirstOwnerUp(xPose, yPose, zPose);
					if(owner != -1 && owner != Global.player_list.get(name).get_id() && !Global.player_list.get(name).is_trusted_by(owner)) //Si le bloc se trouve sur la propriété de quelqu'un d'autre, on annule la construction.
					{
						event.setCancelled(true);
						player.sendMessage("You can't destroy the property of " + new DataManager().getPlayerName(owner) + " !");
					}
				}				
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
	    }
		else if(block != null)
	    {
			warn += " sans joueur";
			event.setCancelled(true);
	    }
		
		System.out.println(warn);
	}
	
	
	
	
	
	//Tests with fire aren't finished.
	@Override
	public void onBlockBurn(BlockBurnEvent event)
	{
		Block block = event.getBlock();

		if(block != null)
		{
			int xPose = block.getX();
			int yPose = block.getY();
			int zPose = block.getZ();
			
			try
			{
				int owner = new DataManager().searchOwner(xPose, yPose, zPose);
				if(owner != -1) //Si le block qui est enflammé appartient à un joueur, alors on éteint le feu.
				{
					System.out.println("Bloc de " + owner + " est consume.");
					event.setCancelled(true);
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		Block block = event.getBlock();

		if(block != null)
		{
			int xPose = block.getX();
			int yPose = block.getY();
			int zPose = block.getZ();
			
			try
			{
				int owner = new DataManager().searchOwner(xPose, yPose, zPose);
				if(owner != -1) //Si le block qui est enflammé appartient à un joueur, alors on éteint le feu.
				{
					System.out.println("Bloc de " + owner + " commence a bruler.");
					event.setCancelled(true);
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}