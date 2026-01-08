package com.sds.serverdiscord.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TextUtil {

    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacyAmpersand();

    private static final PlainTextComponentSerializer PLAIN =
            PlainTextComponentSerializer.plainText();

    public static Component color(String text) {
        return LEGACY.deserialize(text == null ? "" : text);
    }

    public static String placeholders(Player player, String text) {
        if (text == null) return "";
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }

    /**
     * Удаляет все цветовые коды Minecraft из текста
     * @param text текст с цветовыми кодами (&a, &c и т.д.)
     * @return текст без цветовых кодов
     */
    public static String stripColors(String text) {
        if (text == null) return "";
        // Преобразуем текст в Component, затем обратно в plain text (без форматирования)
        Component component = LEGACY.deserialize(text);
        return PLAIN.serialize(component);
    }
}