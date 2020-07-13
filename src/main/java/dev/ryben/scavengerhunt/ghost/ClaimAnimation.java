package dev.ryben.scavengerhunt.ghost;

import dev.ryben.scavengerhunt.utils.ConfigUtils;
import dev.ryben.scavengerhunt.utils.InventoryUtils;
import dev.ryben.scavengerhunt.utils.LocationUtils;

import java.util.Set;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ClaimAnimation implements Listener {

    private final ClaimAnimation instance;
    private final ArmorStand armorStand;

    public ClaimAnimation(Player player, GhostSkull ghostSkull) {
        this.instance = this;
        ghostSkull.getPlugin().getServer().getPluginManager().registerEvents(this, ghostSkull.getPlugin());

        this.armorStand = ghostSkull.getLocation().getWorld().spawn(
                LocationUtils.faceEntity(LocationUtils.center(ghostSkull.getLocation()), player), ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);

        armorStand.setHelmet(ghostSkull.getPlugin().constructGhostSkull());
        armorStand.setChestplate(InventoryUtils.applyArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE),
                ConfigUtils.decodeColor(ghostSkull.getPlugin().getConfig().getString("ghost-armor-color"))));

        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&',
                ghostSkull.getPlugin().getMessageFile().getString("ghost-name")));
        armorStand.setCustomNameVisible(true);

        Set<String> soundKeys = ghostSkull.getPlugin().getConfig().getConfigurationSection("ghost-claim-sounds").getKeys(false);

        if(soundKeys != null && soundKeys.size() > 0) {
            soundKeys.forEach(soundKey -> {
                Sound sound = Sound.valueOf(soundKey.toUpperCase());
                String fullKey = "ghost-claim-sounds." + soundKey.toUpperCase();

                if(sound != null) {
                    float volume = (float) ghostSkull.getPlugin().getConfig().getDouble(fullKey + ".volume", 1);
                    float pitch = (float) ghostSkull.getPlugin().getConfig().getDouble(fullKey + ".pitch", 1);

                    player.playSound(armorStand.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, volume, pitch);
                }
            });
        } else {
            player.playSound(armorStand.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 3, 0.9f);
        }

        new BukkitRunnable() {
            int frames = 0;
            public void run() {
                if(frames > 30) {
                    this.cancel();
                    armorStand.remove();
                    HandlerList.unregisterAll(instance);
                    return;
                }
                if(frames > 10) {
                    Location newLocation = armorStand.getLocation().add(0, 0.2, 0);
                    newLocation.setYaw(newLocation.getYaw()+18f);
                    armorStand.teleport(newLocation);
                }
                armorStand.getWorld().spawnParticle(Particle.CLOUD, armorStand.getLocation().clone().add(0, 0.5, 0),
                        3,
                        0, 0, 0,
                        0);
                frames++;
            }
        }.runTaskTimer(ghostSkull.getPlugin(), 0, 1L);
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if(event.getRightClicked().getUniqueId() == armorStand.getUniqueId())
            event.setCancelled(true);
    }
}