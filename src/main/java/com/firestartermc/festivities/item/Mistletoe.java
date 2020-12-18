package com.firestartermc.festivities.item;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class Mistletoe extends ItemArchetype implements Listener {

    public Mistletoe() {
        super("mistletoe", "Mistletoe");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.SWEET_BERRIES)
                .name("&#ff7a69&lMistletoe")
                .lore(
                        "&fMistletoe has been used for hundreds of years",
                        "&fto treat many medical conditions. Not only is",
                        "&fit a Christmas icon, but it's also an excellent",
                        "&fmedicine. &7You can harvest Mistletoe from Sweet",
                        "&7Berry bushes for a 25% chance drop rate. It's an",
                        "&7excellent food and restores quite a bit of health."
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.SWEET_BERRIES) {
            return;
        }

        if (!matches(event.getItem())) {
            return;
        }

        var player = event.getPlayer();
        player.addPotionEffect(PotionEffectType.HEALTH_BOOST.createEffect(60 * 20, 2));
        player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(60 * 20, 1));
        player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(80, 2));
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(80, 1));
        player.setFoodLevel(20);
        player.setSaturation(20);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onHarvest(PlayerHarvestBlockEvent event) {
        if (event.getHarvestedBlock().getType() != Material.SWEET_BERRY_BUSH) {
            return;
        }

        if (ThreadLocalRandom.current().nextInt(6) != 0) {
            return;
        }

        var harvested = event.getItemsHarvested();
        harvested.clear();
        harvested.add(getItem());
    }
}
