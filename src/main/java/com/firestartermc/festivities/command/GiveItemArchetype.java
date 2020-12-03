package com.firestartermc.festivities.command;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class GiveItemArchetype implements TabExecutor {

    private final Festivities festivities;

    public GiveItemArchetype(Festivities festivities) {
        this.festivities = festivities;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 2) {
            return false;
        }

        var inputPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!inputPlayer.isOnline()) {
            sender.sendMessage(ChatColor.RED + "The player '" + args[0] + "' is not online.");
            return false;
        }

        var itemOptional = festivities.getItem(args[1]);
        if (itemOptional.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "The item '" + args[1] + "' does not exist.");
            return true;
        }

        var player = (Player) inputPlayer;
        var item = itemOptional.get();
        PlayerUtils.giveOrDropItem(player, item.getItem());
        sender.sendMessage(ChatColor.GREEN + "Gave " + item.getName() + " to " + player.getName() + ".");
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 2) {
            return festivities.getRegisteredItems().values().stream()
                    .map(ItemArchetype::getId)
                    .filter(id -> id.toLowerCase().contains(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }
}
