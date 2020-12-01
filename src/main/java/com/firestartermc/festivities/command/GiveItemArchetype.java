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

        OfflinePlayer inputPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!inputPlayer.isOnline()) {
            return false;
        }

        var itemOptional = festivities.getItem(args[1]);
        if (itemOptional.isEmpty()) {
            return false;
        }

        Player player = (Player) inputPlayer;
        PlayerUtils.giveOrDropItem(player, itemOptional.get().getItem());
        sender.sendMessage(ChatColor.GREEN + "Gave " + args[1] + " to " + player.getName() + ".");
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            return festivities.getRegisteredItems().values().stream()
                    .map(ItemArchetype::getId)
                    .collect(Collectors.toList());
        }

        return null;
    }
}
