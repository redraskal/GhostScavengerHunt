package me.redraskal.scavengerhunt.ghost;

import lombok.Getter;
import me.redraskal.scavengerhunt.Main;
import me.redraskal.scavengerhunt.utils.LocationUtils;
import me.redraskal.scavengerhunt.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class GhostSkull implements Listener {

    @Getter
    private final Main plugin;
    @Getter
    private final UUID uuid;
    @Getter
    private final Location location;
    @Getter
    private final List<String> claimed;

    public GhostSkull(Main plugin, UUID uuid, Location location, List<String> claimed) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.location = location;
        this.claimed = claimed;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public boolean claim(UUID uuid) {
        if(claimed.contains(uuid.toString())) return false;
        claimed.add(uuid.toString());
        this.saveConfig();
        return true;
    }

    public boolean unclaim(UUID uuid) {
        if(!claimed.contains(uuid.toString())) return false;
        claimed.remove(uuid.toString());
        this.saveConfig();
        return true;
    }

    public void saveConfig() {
        try {
            plugin.getDataFile().set(this.uuid.toString() + ".claimed", this.claimed);
            plugin.getDataFile().save(plugin.getF_dataFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onSkullClick(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!LocationUtils.center(event.getClickedBlock().getLocation())
                .equals(LocationUtils.center(this.location))) return;
        if(this.claim(event.getPlayer().getUniqueId())) {
            if(this.getPlugin().getSkullCount(event.getPlayer().getUniqueId()) >= this.getPlugin().getGhostSkulls().size()) {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sounds.LEVEL_UP.spigot(), 10, 0.9f);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPlugin().getMessageFile().getString("ghosts-found-message")));
                this.getPlugin().getConfig().getStringList("reward-commands").forEach(command -> {
                    this.getPlugin().getServer().dispatchCommand(Bukkit.getConsoleSender(),
                            command.replace("{name}", event.getPlayer().getName())
                                    .replace("{uuid}", event.getPlayer().getUniqueId().toString())
                                    .replace("{total}", "" + this.getPlugin().getSkullCount(event.getPlayer().getUniqueId())));
                });
            } else {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sounds.GHAST_FIREBALL.spigot(), 10, 1.2f);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPlugin().getMessageFile().getString("ghost-found-message"))
                        .replace("{found}", "" + this.getPlugin().getSkullCount(event.getPlayer().getUniqueId()))
                        .replace("{total}", "" + this.getPlugin().getGhostSkulls().size()));
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPlugin().getMessageFile().getString("find-message")));
            }
            new ClaimAnimation(event.getPlayer(), this);
        } else {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sounds.NOTE_BASS.spigot(), 10, 1);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPlugin().getMessageFile().getString("already-found-message")));
        }
    }

    @EventHandler
    public void onSkullBreak(BlockBreakEvent event) {
        if(!LocationUtils.center(event.getBlock().getLocation()).equals(LocationUtils.center(this.location))) return;
        if(!event.getPlayer().hasPermission("ghostskull.break")) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPlugin().getMessageFile().getString("cannot-break-skull-message")));
            event.setCancelled(true);
        } else {
            HandlerList.unregisterAll(this);
            plugin.getGhostSkulls().remove(this);
            plugin.getDataFile().set(this.uuid.toString(), null);
            try {
                plugin.getDataFile().save(plugin.getF_dataFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.getPlayer().sendMessage(ChatColor.GREEN + "Ghost Skull has been deleted.");
        }
    }
}