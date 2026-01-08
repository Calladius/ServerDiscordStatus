package com.sds.serverdiscord.util;

import com.sds.serverdiscord.Main;
import java.io.File;

public class DiskUtil {
    public static String getUsage(Main plugin) {
        File f = new File(plugin.getConfig().getString("server.disk_path", "."));
        long total = f.getTotalSpace();
        long free = f.getFreeSpace();
        return String.format("%.1f%%", 100D * (total - free) / total);
    }
}