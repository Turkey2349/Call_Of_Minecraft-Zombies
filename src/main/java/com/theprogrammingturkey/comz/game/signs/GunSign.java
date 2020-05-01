package com.theprogrammingturkey.comz.game.signs;

import com.theprogrammingturkey.comz.COMZombies;
import com.theprogrammingturkey.comz.economy.PointManager;
import com.theprogrammingturkey.comz.game.Game;
import com.theprogrammingturkey.comz.guns.Gun;
import com.theprogrammingturkey.comz.guns.GunManager;
import com.theprogrammingturkey.comz.guns.GunType;
import com.theprogrammingturkey.comz.util.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class GunSign implements IGameSign
{
	@Override
	public void onBreak(Game game, Player player, Sign sign)
	{

	}

	@Override
	public void onInteract(Game game, Player player, Sign sign)
	{
		int BuyPoints = Integer.parseInt(sign.getLine(3).substring(0, sign.getLine(3).indexOf("/") - 1).trim());
		int RefillPoints = Integer.parseInt(sign.getLine(3).substring(sign.getLine(3).indexOf("/") + 2).trim());
		GunType guntype = COMZombies.getPlugin().getGun(sign.getLine(2));

		if(guntype == null)
		{
			player.sendRawMessage(COMZombies.PREFIX + " Sorry! That gun doesn't seem to exist!");
			return;
		}

		GunManager manager = game.getPlayersGun(player);
		int slot = manager.getCorrectSlot();
		Gun gun = manager.getGun(player.getInventory().getHeldItemSlot());
		if(manager.isGun() && gun.getType().name.equalsIgnoreCase(guntype.name))
		{
			if(PointManager.canBuy(player, RefillPoints))
			{
				manager.getGun(slot).maxAmmo();
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "Filling ammo!");
				PointManager.takePoints(player, RefillPoints);
				PointManager.notifyPlayer(player);
			}
			else
			{
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "You don't have enough points!");
			}
		}
		else
		{
			if(PointManager.canBuy(player, BuyPoints))
			{
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "You got the " + ChatColor.GOLD + "" + ChatColor.BOLD + guntype.name + ChatColor.RED + ChatColor.BOLD + "!");
				manager.removeGun(manager.getGun(slot));
				manager.addGun(new Gun(guntype, player, slot));
				player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);
				PointManager.takePoints(player, BuyPoints);
				PointManager.notifyPlayer(player);
			}
			else
			{
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "You don't have enough points!");
			}
		}
	}

	@Override
	public void onChange(Game game, Player player, Sign sign)
	{
		String thirdLine = ChatColor.stripColor(sign.getLine(2));
		String fourthLine = ChatColor.stripColor(sign.getLine(2));

		if(thirdLine.equalsIgnoreCase(""))
		{
			sign.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "No gun?");
			return;
		}
		sign.setLine(0, ChatColor.RED + "[Zombies]");
		sign.setLine(1, ChatColor.AQUA + "Gun");
		sign.setLine(2, thirdLine);
		if(COMZombies.getPlugin().getGun(thirdLine) == null)
		{
			sign.setLine(0, ChatColor.RED + "Invalid Gun!");
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			return;
		}
		String price = "";
		try
		{
			price += fourthLine.substring(0, fourthLine.indexOf("/")).trim();
			price += " / ";
			price += fourthLine.substring(fourthLine.indexOf("/") + 1).trim();
		} catch(Exception ex)
		{
			price = "200 / 100";
		}
		sign.setLine(3, price);
	}

	@Override
	public boolean requiresGame()
	{
		return true;
	}
}
