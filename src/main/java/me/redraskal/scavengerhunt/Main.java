package me.redraskal.scavengerhunt;

import lombok.Getter;
import me.redraskal.scavengerhunt.ghost.GhostSkull;
import me.redraskal.scavengerhunt.ghost.SkullProfile;
import me.redraskal.scavengerhunt.utils.ConfigUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Main extends JavaPlugin implements Listener {

    @Getter
    private YamlConfiguration dataFile;
    @Getter
    private File f_dataFile;

    @Getter
    private YamlConfiguration messageFile;
    @Getter
    private File f_messageFile;

    @Getter
    private List<GhostSkull> ghostSkulls;

    public void onEnable() {
        this.saveDefaultConfig();

        this.f_dataFile = new File(this.getDataFolder(), "data.yml");
        if(!f_dataFile.exists()) try {
            f_dataFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.f_messageFile = new File(this.getDataFolder(), "messages.yml");
        this.saveResource("messages.yml", false);

        this.dataFile = YamlConfiguration.loadConfiguration(f_dataFile);
        this.messageFile = YamlConfiguration.loadConfiguration(f_messageFile);

        this.ghostSkulls = new ArrayList<>();
        this.dataFile.getKeys(false).forEach(key -> {
            List<String> claimed = new ArrayList<>();
            this.dataFile.getStringList(key + ".claimed").forEach(uuid -> {
                claimed.add(uuid);
            });
            this.ghostSkulls.add(new GhostSkull(this, UUID.fromString(key), ConfigUtils.decodeLocation(dataFile.getString(key + ".location")), claimed));
        });

        this.getCommand("ghostskull").setExecutor(new GhostSkullCommand(this));
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public int getSkullCount(UUID uuid) {
        int count = 0;
        for(GhostSkull ghostSkull : this.ghostSkulls) {
            if(ghostSkull.getClaimed().contains(uuid.toString())) count++;
        }
        return count;
    }

    public ItemStack constructGhostSkull() {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&lGhost Skull"));
        skullMeta.addAttributeModifier(Attribute.HORSE_JUMP_STRENGTH, new AttributeModifier(UUID.randomUUID(), "AdditionalDamage", 1D, Operation.ADD_NUMBER));
        itemStack.setItemMeta(skullMeta);

        new SkullProfile(this.getConfig().getString("ghost-skull-hash"))
                .applyTextures(itemStack);

        return itemStack;
    }

    @EventHandler
    public void onSkullPlace(BlockPlaceEvent event) {
        if(!event.getItemInHand().hasItemMeta() || !event.getItemInHand().getItemMeta().hasDisplayName()) return;
        ItemMeta itemMeta = event.getItemInHand().getItemMeta();
        if(!ChatColor.stripColor(itemMeta.getDisplayName()).equals("Ghost Skull") || itemMeta.getAttributeModifiers(Attribute.HORSE_JUMP_STRENGTH) == null) return;
        GhostSkull ghostSkull = new GhostSkull(this, UUID.randomUUID(), event.getBlockPlaced().getLocation(), new ArrayList<>());
        this.dataFile.set(ghostSkull.getUuid() + ".location", ConfigUtils.encodeLocation(ghostSkull.getLocation()));
        this.dataFile.set(ghostSkull.getUuid() + ".claimed", new ArrayList<>());
        ghostSkull.saveConfig();
        this.ghostSkulls.add(ghostSkull);
        event.getPlayer().sendMessage(ChatColor.GREEN + "Ghost Skull has been placed.");
    }
}