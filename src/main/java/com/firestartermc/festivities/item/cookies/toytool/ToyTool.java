package com.firestartermc.festivities.item.cookies.toytool;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.lib.fastutil.objects.Object2LongMap;
import com.firestartermc.kerosene.lib.fastutil.objects.Object2LongOpenHashMap;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ToyTool extends ItemArchetype implements Listener {

    private final Object2LongMap<UUID> cooldowns = new Object2LongOpenHashMap<>();

    public ToyTool(@NotNull String id, @NotNull String name) {
        super(id, name);
    }

    // Play squeak sound on block break
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var item = event.getPlayer().getItemInHand();

        if (!matches(item)) {
            return;
        }

        var player = event.getPlayer();

        if (cooldowns.getOrDefault(player.getUniqueId(), 0) > System.currentTimeMillis()) {
            return;
        }

        squeak(player);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(350, 500));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        var item = ((Player) event.getDamager()).getInventory().getItemInMainHand();
        if (!matches(item)) {
            return;
        }

        squeak((Player) event.getDamager());
    }

    protected void squeak(Player player) {
        var location = player.getLocation();
        location.getWorld().playSound(location, "minecraft:item.toy_pickaxe.squeak", 0.7f, 0.9f);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cooldowns.removeLong(event.getPlayer().getUniqueId());
    }

    // idiot proofing it this time :>>
    @EventHandler
    public void onConvert(InventoryClickEvent event) {
        var inventory = event.getInventory();
        if (inventory.getType() != InventoryType.SMITHING) {
            return;
        }

        if (matches(inventory.getItem(0))) {
            event.setCancelled(true);
        }
    }
}
