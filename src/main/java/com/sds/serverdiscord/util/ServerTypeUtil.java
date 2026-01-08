package com.sds.serverdiscord.util;

import org.bukkit.Bukkit;

public class ServerTypeUtil {

    public static String get() {
        boolean geyser = Bukkit.getPluginManager().isPluginEnabled("Geyser-Spigot");

        if (geyser) {
            return "JAVA + BEDROCK";
        }
        return "JAVA";
    }
}