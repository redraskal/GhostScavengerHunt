package me.redraskal.scavengerhunt;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright (c) Redraskal 2020.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class SkullsCommand implements CommandExecutor {

  private final Main plugin;

  public SkullsCommand(Main plugin) {
      this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
    } else {
      Player player = (Player) sender;

      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
        plugin.getMessageFile().getString("ghosts-found-command-message")
          .replace("{found}", "" + plugin.getSkullCount(player.getUniqueId()))
          .replace("{total}", "" + plugin.getGhostSkulls().size())
      ));
    }
    return false;
  }
}