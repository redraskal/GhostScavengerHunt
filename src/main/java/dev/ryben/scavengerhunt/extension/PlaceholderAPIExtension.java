package dev.ryben.scavengerhunt.extension;

import org.bukkit.OfflinePlayer;

import dev.ryben.scavengerhunt.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIExtension extends PlaceholderExpansion {

  private final Main plugin;

  public PlaceholderAPIExtension(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String getAuthor() {
    return "redraskal";
  }

  /**
   * The placeholder identifier should go here.
   * <br>This is what tells PlaceholderAPI to call our onRequest 
   * method to obtain a value if a placeholder starts with our 
   * identifier.
   * <br>This must be unique and can not contain % or _
   *
   * @return The identifier in {@code %<identifier>_<value>%} as String.
   */
  @Override
  public String getIdentifier() {
    return "ghostscavengerhunt";
  }

  @Override
  public String getVersion() {
    return plugin.getDescription().getVersion();
  }

  /**
   * This is the method called when a placeholder with our identifier 
   * is found and needs a value.
   * <br>We specify the value identifier in this method.
   * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
   *
   * @param  player
   *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
   * @param  identifier
   *         A String containing the identifier/value.
   *
   * @return Possibly-null String of the requested identifier.
   */
  @Override
  public String onRequest(OfflinePlayer player, String identifier) {
    if(player == null) {
      if(identifier.equalsIgnoreCase("total")) {
        return "" + plugin.getGhostSkulls().size();
      } else {
        return null;
      }
    } else {
      if(identifier.equalsIgnoreCase("player_found")) {
        return "" + plugin.getSkullCount(player.getUniqueId());
      } else if(identifier.equalsIgnoreCase("player_left")) {
        return "" + (plugin.getGhostSkulls().size() - plugin.getSkullCount(player.getUniqueId()));
      } else {
        return null;
      }
    }
  }
}