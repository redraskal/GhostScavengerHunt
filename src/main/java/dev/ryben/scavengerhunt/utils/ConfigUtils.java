package dev.ryben.scavengerhunt.utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ConfigUtils {

    public static Location decodeLocation(String object) {
        String[] parts = object.split("@");
        return new Location(Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]), Float.parseFloat(parts[4]));
    }

    public static String encodeLocation(Location object) {
        return object.getWorld().getName() + "@" + object.getX() + "@" + object.getY() + "@" + object.getZ()
                + "@" + object.getYaw() + "@" + object.getPitch();
    }

    public static Color decodeColor(String object) {
        object = object.replace("#", "");
        return Color.fromRGB(
                Integer.valueOf(object.substring(0, 2), 16),
                Integer.valueOf(object.substring(2, 4), 16),
                Integer.valueOf(object.substring(4, 6), 16));
    }
}