package com.sds.serverdiscord;

import com.sds.serverdiscord.bot.DiscordBot;
import com.sds.serverdiscord.chat.ChatSyncListener;
import com.sds.serverdiscord.command.SDSCommand;
import com.sds.serverdiscord.status.StatusService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class Main extends JavaPlugin {

    private FileConfiguration messages;
    private DiscordBot discordBot;
    private StatusService statusService;

    public FileConfiguration getMessages() {
        return messages;
    }

    public StatusService getStatusService() {
        return statusService;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);

        loadMessages();

        String token = getConfig().getString("bot.token");
        if (token == null || token.isBlank() || token.equals("PUT_DISCORD_TOKEN_HERE")) {
            getLogger().severe("❌ Discord token не установлен! Установите токен в config.yml");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        discordBot = new DiscordBot(this);
        discordBot.start();

        statusService = new StatusService(this, discordBot);
        statusService.start();

        Bukkit.getPluginManager().registerEvents(
                new ChatSyncListener(this, discordBot), this
        );

        getLogger().info("✅ ServerDiscordStatus включен успешно!");
        Objects.requireNonNull(getCommand("sds")).setExecutor(new SDSCommand(this));
    }

    @Override
    public void onDisable() {
        if (statusService != null) {
            statusService.stop();
        }
        if (discordBot != null) {
            discordBot.shutdown();
        }
        getLogger().info("❌ ServerDiscordStatus выключен");
    }

    public void reloadAll() {
        reloadConfig();
        loadMessages();

        if (statusService != null) {
            statusService.sendOnce();
        }

        getLogger().info("🔄 Конфигурация перезагружена");
    }

    private void loadMessages() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
}