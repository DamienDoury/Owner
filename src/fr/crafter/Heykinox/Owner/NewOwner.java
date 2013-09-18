package fr.crafter.Heykinox.Owner;

import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

public class NewOwner extends JavaPlugin
{	
	private final LogListener logListener = new LogListener();
	private final CommandListener commandListener = new CommandListener();
	private final BuildListener buildListener = new BuildListener();
	private final DestroyListener destroyListener = new DestroyListener();
	private final InteractListener interactListener = new InteractListener();

	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(logListener, this);
		
		//Managing unattented server stop.
		try
		{
			new DataManager().setLogoutToAllConnectedPlayers("last_login + 1");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		//End managing unattented server stop.
	}
	
	@Override
	public void onDisable()
	{
		//Managing wanted server stop.
		try
		{
			new DataManager().setLogoutToAllConnectedPlayers("CURRENT_TIMESTAMP");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		//End managing wanted server stop.
	}
}