package com.sds.serverdiscord.bot;

import com.sds.serverdiscord.Main;
import com.sds.serverdiscord.util.TextUtil;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;

import java.util.EnumSet;
import java.util.logging.Level;

public class DiscordBot extends ListenerAdapter {

    private final Main plugin;
    private JDA jda;
    private Message statusMessage;

    public DiscordBot(Main plugin) {
        this.plugin = plugin;
    }

    public void start() {
        try {
            jda = JDABuilder.createDefault(plugin.getConfig().getString("bot.token"))
                    .enableIntents(EnumSet.of(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT
                    ))
                    .addEventListeners(this)
                    .build()
                    .awaitReady();

            jda.upsertCommand("status", "Обновить статус сервера").queue();
            jda.upsertCommand("reload", "Перезагрузить плагин").queue();

            loadStatusMessage();

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Discord bot start failed", e);
        }
    }

    private void loadStatusMessage() {
        long messageId = plugin.getConfig().getLong("status.message_id", 0);
        if (messageId == 0) return;

        TextChannel channel = jda.getTextChannelById(plugin.getConfig().getLong("channels.status"));
        if (channel != null) {
            channel.retrieveMessageById(messageId).queue(
                    msg -> statusMessage = msg,
                    error -> plugin.getLogger().warning("Не удалось загрузить сообщение статуса: " + error.getMessage())
            );
        }
    }

    public void shutdown() {
        if (jda != null) jda.shutdownNow();
    }

    public void send(long channelId, String message, boolean tts) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage(message).setTTS(tts).queue();
        }
    }

    public void sendOrEditStatus(MessageEmbed embed) {
        TextChannel channel = jda.getTextChannelById(plugin.getConfig().getLong("channels.status"));
        if (channel == null) return;

        if (statusMessage != null) {
            statusMessage.editMessageEmbeds(embed).queue(
                    success -> {},
                    error -> {
                        plugin.getLogger().warning("Не удалось обновить сообщение, отправляю новое");
                        sendNewStatusMessage(channel, embed);
                    }
            );
        } else {
            sendNewStatusMessage(channel, embed);
        }
    }

    private void sendNewStatusMessage(TextChannel channel, MessageEmbed embed) {
        channel.sendMessageEmbeds(embed).queue(msg -> {
            statusMessage = msg;
            plugin.getConfig().set("status.message_id", msg.getIdLong());
            plugin.saveConfig();
        });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;

        long chatChannel = plugin.getConfig().getLong("channels.chat");
        if (e.getChannel().getIdLong() != chatChannel) return;

        String format = plugin.getMessages().getString("messages.chat.from_discord", "&9[DC] &f%user%: &r%message%");
        String msg = format
                .replace("%user%", e.getAuthor().getName())
                .replace("%message%", e.getMessage().getContentDisplay());

        Bukkit.getScheduler().runTask(plugin, () ->
                Bukkit.broadcast(TextUtil.color(msg))
        );
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        switch (e.getName()) {
            case "status" -> {
                plugin.getStatusService().sendOnce();
                e.reply("✅ Статус обновлён").setEphemeral(true).queue();
            }
            case "reload" -> {
                plugin.reloadAll();
                e.reply("🔄 Плагин перезагружен").setEphemeral(true).queue();
            }
        }
    }
}