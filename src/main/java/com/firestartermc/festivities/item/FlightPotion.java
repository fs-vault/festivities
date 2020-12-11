package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.item.PotionBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlightPotion extends ItemArchetype implements Listener {

    private final Map<Player, Integer> tasks;
    private final ItemStack flightPotion;

    public FlightPotion() {
        super("flight_potion", "Flight Potion");
        this.tasks = new ConcurrentHashMap<>();
        this.flightPotion = new PotionBuilder()
                .name("&fPotion of Flight")
                .lore("&9Creative Flight (3:00)", "&7&o(best before 12/25/20)")
                .color(Color.fromRGB(255, 96, 89))
                .enchantUnsafe(Enchantment.MENDING, 1)
                .addAllItemFlags()
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.GLASS_BOTTLE)
                .name("&#ff6059&lKringle's &#ff7c73&lBrew")
                .lore(
                        "&#ffebebEvery year, Santa battles dragons day in and",
                        "&#ffebebday out, gathering ingredients to help his",
                        "&#ffebebreindeer come Christmas. Although not dropped",
                        "&#ffebeb100% of the time, see if you're able to slay a",
                        "&#ffebebdragon, repair whatever it drops, and... toss",
                        "&#ffebebit in a brewing stand?"
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .addAllItemFlags()
                .build();
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        if (!matches(event.getItem())) {
            return;
        }

        var player = event.getPlayer();
        player.setAllowFlight(true);
        player.setFlying(true);
        player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.0f);
        player.sendMessage(MessageUtils.formatColors("&#ff6059&lFLIGHT: &#ffebebYou now have 3 minutes of creative flight.", true));

        if (tasks.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(tasks.get(player));
        }

        var task = Bukkit.getScheduler().runTaskLater(Festivities.INSTANCE, () -> {
            player.setAllowFlight(false);
            player.setFlying(false);
            tasks.remove(player);
        }, 180 * 20L);

        tasks.put(player, task.getTaskId());
    }

    @EventHandler
    public void onClickSlot(InventoryClickEvent event) {
        var inventory = event.getClickedInventory();
        if (inventory == null || inventory.getType() != InventoryType.BREWING) {
            return;
        }

        var item = event.getCursor();
        if (item == null || item.getType() != Material.ELYTRA) {
            return;
        }

        if (event.getSlot() != 3) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(Festivities.INSTANCE, () -> {
            for (var human : inventory.getViewers()) {
                if (human instanceof Player) {
                    ((Player) human).updateInventory();
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        var ingredient = event.getContents().getIngredient();
        if (ingredient == null || ingredient.getType() != Material.ELYTRA) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            var item = event.getContents().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                event.getContents().setItem(i, flightPotion);
            }
        }
    }
}
