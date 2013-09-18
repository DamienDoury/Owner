package fr.crafter.Heykinox.Owner;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.entity.Player;

public class OwnerPlayer
{
	private int id;
	private String name;
	private String behavior = "own";
	private int id_target = 0;
	private ArrayList<Integer> is_trusted_by = new ArrayList<Integer>();
	
	private long lastSoftwareMessage = System.currentTimeMillis();
	
	public OwnerPlayer(int _id, String _name, String _behavior, int _id_target, ArrayList<Integer> trustList)
	{
		id = _id;
		name = _name;
		behavior = _behavior;
		id_target = _id_target;
		is_trusted_by = trustList;
	}
	
	public boolean softwareMessageEnabled()
	{
		if(System.currentTimeMillis() - lastSoftwareMessage > 2000)
		{
			lastSoftwareMessage = System.currentTimeMillis();
			return true;
		}

		return false;
	}
	
	public void add_trust_link(int idOwner)
	{
		is_trusted_by.add(idOwner);
	}
	
	public void del_trust_link(Integer idOwner)
	{
		if(is_trusted_by.contains(idOwner))
			is_trusted_by.remove(idOwner);
	}
	
	public boolean is_trusted_by(int idOwner)
	{
		return is_trusted_by.contains(idOwner);
	}
	
	public int get_id()
	{
		return id;
	}
	
	public String get_name()
	{
		return name;
	}
	
	public String getBehavior()
	{
		return behavior;
	}
	
	public void setBehavior(String _behavior)
	{
		behavior = _behavior;
	}
	
	public int get_id_target()
	{
		return id_target;
	}
	
	public void set_id_target(int _id_target)
	{
		id_target = _id_target;
	}
	
	public void showTrustList(Player player) throws SQLException
	{
		ArrayList<String> trustList = new ArrayList<String>();
		trustList = new DataManager().getTrustList(id);
		
		if(trustList != null && trustList.size() > 0)
		{
			player.sendRawMessage("You trust the following players : ");
			
			for(String friendName : trustList)
				player.sendRawMessage("	- " + friendName);
		}
		else
			player.sendRawMessage("You trust nobody ...");
	}
}
