package com.sds.serverdiscord.util;

import org.bukkit.Bukkit;

public class TPSUtil {

    /**
     * Получает текущий TPS (Ticks Per Second) сервера
     * @return строка с TPS и цветовым индикатором
     */
    public static String getTPS() {
        try {
            double tps = Bukkit.getTPS()[0]; // Последняя минута

            String color;
            if (tps >= 19.5) {
                color = "🟢"; // Отлично
            } else if (tps >= 17.0) {
                color = "🟡"; // Средне
            } else {
                color = "🔴"; // Плохо
            }

            return String.format("%s %.1f", color, Math.min(tps, 20.0));
        } catch (Exception e) {
            return "⚪ N/A";
        }
    }
}