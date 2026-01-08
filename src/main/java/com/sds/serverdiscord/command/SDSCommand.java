package com.sds.serverdiscord.command;

import com.sds.serverdiscord.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Stream;

public class SDSCommand implements TabExecutor {

    private final Main plugin;

    public SDSCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NonNull CommandSender sender,
            @NonNull Command command,
            @NonNull String label,
            String[] args
    ) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "reload" -> {
                if (!sender.hasPermission("sds.reload")) {
                    sender.sendMessage(Component.text("§cУ вас нет прав на эту команду."));
                    return true;
                }

                plugin.reloadAll();
                sender.sendMessage(Component.text("§aКонфигурация перезагружена."));
                return true;
            }

            case "status" -> {
                if (!sender.hasPermission("sds.status")) {
                    sender.sendMessage(Component.text("§cУ вас нет прав на эту команду."));
                    return true;
                }

                plugin.getStatusService().sendOnce();
                sender.sendMessage(Component.text("§aСтатус сервера обновлён в Discord."));
                return true;
            }

            default -> {
                sendHelp(sender);
                return true;
            }
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("§6§lServerDiscordStatus"));
        sender.sendMessage(Component.text("§e/sds reload §7- Перезагрузить конфигурацию"));
        sender.sendMessage(Component.text("§e/sds status §7- Обновить статус в Discord"));
        sender.sendMessage(Component.text("§e/sds help §7- Показать эту справку"));
    }

    @Override
    public List<String> onTabComplete(
            @NonNull CommandSender sender,
            @NonNull Command command,
            @NonNull String alias,
            String[] args
    ) {
        if (args.length == 1) {
            return Stream.of("reload", "status", "help")
                    .filter(arg ->
                            !arg.equals("reload") || sender.hasPermission("sds.reload"))
                    .filter(arg ->
                            !arg.equals("status") || sender.hasPermission("sds.status"))
                    .toList();
        }
        return List.of();
    }
}
