package me.redraskal.scavengerhunt.ghost;

import me.redraskal.scavengerhunt.utils.ConfigUtils;
import me.redraskal.scavengerhunt.utils.InventoryUtils;
import me.redraskal.scavengerhunt.utils.LocationUtils;
import me.redraskal.scavengerhunt.utils.Sounds;
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

        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', "&f&lBoo!"));
        armorStand.setCustomNameVisible(true);

        player.playSound(armorStand.getLocation(), Sounds.FIZZ.spigot(), 3, 0.9f);

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
                player.spigot().playEffect(armorStand.getLocation().clone().add(0, 0.5, 0), Effect.CLOUD,
                        0, 0, 0, 0, 0,
                        0, 3, 15);
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