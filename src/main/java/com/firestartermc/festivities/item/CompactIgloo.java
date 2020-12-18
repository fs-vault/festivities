package com.firestartermc.festivities.item;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.DefinedStructure;
import net.minecraft.server.v1_16_R3.DefinedStructureInfo;
import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class CompactIgloo extends ItemArchetype implements Listener {

    private DefinedStructure structure;

    public CompactIgloo() {
        super("compact_igloo", "Compact Igloo");

        ForkJoinPool.commonPool().submit(() -> {
            try {
                var nbt = NBTCompressedStreamTools.a(Paths.get("world/generated/minecraft/structures/adventigloo.nbt").toFile());
                this.structure = new DefinedStructure();
                this.structure.b(nbt);
                System.out.println("Loaded igloo schematic!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.SNOW_BLOCK)
                .name("&#9ed8ff&lCompact Igloo")
                .lore(
                        "&fA nice getaway home. Now ",
                        "&fpackaged in a box.",
                        "&c&lWARNING: &cThis replaces",
                        "&cblocks. Place in an open area."
                )
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        if (!matches(event.getItemInHand())) {
            return;
        }

        if (true) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lDISABLED: &cDisabled due to an exploit."));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            event.setCancelled(true);
            return;
        }

        var location = event.getBlock().getLocation();
        var nmsWorld = ((CraftWorld) event.getPlayer().getWorld()).getHandle();
        var nmsPos = new BlockPosition(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());

        var info = new DefinedStructureInfo().a(false);
        structure.a(nmsWorld, nmsPos, info, new Random());
    }
}
