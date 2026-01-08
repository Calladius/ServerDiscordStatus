package com.sds.serverdiscord.chat;

import com.sds.serverdiscord.Main;
import com.sds.serverdiscord.bot.DiscordBot;
import com.sds.serverdiscord.util.TextUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatSyncListener implements Listener {

    private final Main plugin;
    private final DiscordBot bot;

    public ChatSyncListener(Main plugin, DiscordBot bot) {
        this.plugin = plugin;
        this.bot = bot;
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        String raw = PlainTextComponentSerializer.plainText().serialize(e.message());

        String format = plugin.getMessages().getString("messages.chat.from_minecraft", "&7[MC] &f%player%: &r%message%");
        String msg = format
                .replace("%player%", e.getPlayer().getName())
                .replace("%message%", raw);

        msg = TextUtil.placeholders(e.getPlayer(), msg);

        // Убираем цветовые коды для Discord
        String finalMsg = TextUtil.stripColors(msg);

        bot.send(
                plugin.getConfig().getLong("channels.chat"),
                finalMsg,
                false
        );
    }
}