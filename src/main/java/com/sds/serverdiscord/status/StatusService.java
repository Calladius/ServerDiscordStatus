package com.sds.serverdiscord.status;

import com.sds.serverdiscord.Main;
import com.sds.serverdiscord.bot.DiscordBot;
import com.sds.serverdiscord.util.DiskUtil;
import com.sds.serverdiscord.util.ServerTypeUtil;
import com.sds.serverdiscord.util.TPSUtil;
import com.sds.serverdiscord.util.TextUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;

import java.awt.*;
import java.time.Instant;
import java.util.stream.Collectors;

public class StatusService {

    private final Main plugin;
    private final DiscordBot bot;
    private int taskId = -1;

    public StatusService(Main plugin, DiscordBot bot) {
        this.plugin = plugin;
        this.bot = bot;
    }

    public void start() {
        int interval = plugin.getConfig().getInt("status.update_interval_seconds", 60);
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::sendOnce, 20L, interval * 20L
        ).getTaskId();
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public void sendOnce() {
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();

        String players = Bukkit.getOnlinePlayers().stream()
                .map(org.bukkit.entity.Player::getName)
                .collect(Collectors.joining(", "));

        String template = plugin.getMessages().getString("messages.status.template",
                "Server: %domain%\nPlayers: %online%/%max%\nOnline: %players%\nTPS: %tps%\nDisk: %disk%\nType: %type%");

        String text = template
                .replace("%domain%", plugin.getConfig().getString("server.domain", "play.example.com"))
                .replace("%online%", String.valueOf(online))
                .replace("%max%", String.valueOf(max))
                .replace("%players%", players.isEmpty() ? "—" : players)
                .replace("%tps%", TPSUtil.getTPS())
                .replace("%disk%", DiskUtil.getUsage(plugin))
                .replace("%type%", ServerTypeUtil.get());

        Color embedColor = online > 0 ? Color.GREEN : Color.ORANGE;

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(embedColor)
                .setTitle("📊 Статус сервера")
                .setDescription(TextUtil.stripColors(text))
                .setTimestamp(Instant.now())
                .setFooter("Обновлено");

        bot.sendOrEditStatus(eb.build());
    }
}