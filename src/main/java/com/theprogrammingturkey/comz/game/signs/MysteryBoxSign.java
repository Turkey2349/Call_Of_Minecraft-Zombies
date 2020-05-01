package com.theprogrammingturkey.comz.game.signs;

import com.theprogrammingturkey.comz.economy.PointManager;
import com.theprogrammingturkey.comz.game.Game;
import com.theprogrammingturkey.comz.game.features.RandomBox;
import com.theprogrammingturkey.comz.util.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;

public class MysteryBoxSign implements IGameSign
{
	public void onBreak(Game game, Player player, Sign sign)
	{
		game.boxManager.removeBox(player, game.boxManager.getBox(sign.getLocation()));
	}

	@Override
	public void onInteract(Game game, Player player, Sign sign)
	{
		RandomBox box = game.boxManager.getBox(sign.getLocation());
		if(box == null)
			return;

		if(box.canActivate())
		{
			int points = Integer.parseInt(sign.getLine(2));
			if(game.isFireSale())
				points = 10;

			if(PointManager.canBuy(player, points))
			{
				box.Start(player, points);
				player.getLocation().getWorld().playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
			}
			else
			{
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "You don't have enough points!");
			}
		}
		else if(box.canPickGun())
		{
			box.pickUpGun(player);
		}
	}

	@Override
	public void onChange(Game game, Player player, Sign sign)
	{
		String thirdLine = ChatColor.stripColor(sign.getLine(2));
		if(thirdLine.equalsIgnoreCase(""))
			thirdLine = "950";

		sign.setLine(0, ChatColor.RED + "[Zombies]");
		sign.setLine(1, ChatColor.AQUA + "Mystery Box");
		sign.setLine(2, thirdLine);
		BlockFace facing = ((Directional) sign.getBlock().getBlockData()).getFacing();
		RandomBox box = new RandomBox(sign.getBlock().getLocation(), facing, game, game.boxManager.getNextBoxName(), Integer.parseInt(thirdLine));
		game.boxManager.addBox(box);
		player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Random Weapon Box Created!");
	}

	@Override
	public boolean requiresGame()
	{
		return true;
	}
}
