package com.firestartermc.festivities.item.frozenscythe;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CompressedFrozenShards extends ItemArchetype implements Listener {

    public CompressedFrozenShards() {
        super("compressed_frozen_shards", "Compressed Frozen Shards");
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapedRecipe(new NamespacedKey(festivities, getId()), getItem());
        recipe.shape("***", "*%*", "***");
        festivities.getItem("frozen_shard").ifPresent(shard -> recipe.setIngredient('*', shard.getItem()));
        recipe.setIngredient('%', Material.GOLD_INGOT);
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.PRISMARINE_BRICKS)
                .name("&#cadbed&lCompressed Frozen Shards")
                .lore(
                        "&fVery dense and durable. Can be used",
                        "&fto craft the Frozen Scythe."
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }
    
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (matches(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }
}
