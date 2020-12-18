package com.firestartermc.festivities.item.frozenscythe;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class FrozenShard extends ItemArchetype implements Listener {

    public FrozenShard() {
        super("frozen_shard", "Frozen Shard");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.PRISMARINE_SHARD)
                .name("&#cadbed&lFrozen Shard")
                .lore(
                        "&fShattered by the cold and dropped by Strays.",
                        "&fSurround a gold ingot in these to craft one",
                        "&fcompressed frozen shard block."
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onStrayDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.STRAY) {
            return;
        }

        var killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        if (ThreadLocalRandom.current().nextInt(4) != 0) {
            return;
        }

        // drop a shard
        PlayerUtils.giveOrDropItem(killer, getItem());
    }
}
