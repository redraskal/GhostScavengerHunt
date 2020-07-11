package dev.ryben.scavengerhunt;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class GhostSkullCommand implements CommandExecutor {

    private final Main plugin;

    public GhostSkullCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
        } else {
            Player player = (Player) sender;
            if(!player.hasPermission("ghostskull.spawn")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            } else {
                player.getInventory().addItem(plugin.constructGhostSkull());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1.2f);
                player.sendMessage(ChatColor.GREEN + "You have obtained a Ghost skull.");
            }
        }
        return false;
    }
}