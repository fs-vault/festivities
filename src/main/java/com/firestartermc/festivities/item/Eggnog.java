package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.PotionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

public class Eggnog extends ItemArchetype implements Listener {

    public Eggnog() {
        super("eggnog", "Eggnog");
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapelessRecipe(new NamespacedKey(festivities, getId()), getItem());
        recipe.addIngredient(1, Material.MILK_BUCKET);
        recipe.addIngredient(1, Material.GLASS_BOTTLE);
        recipe.addIngredient(1, Material.SUGAR);
        recipe.addIngredient(1, Material.EGG);
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return new PotionBuilder()
                .name("&#fff9d4&lEggnog")
                .lore(
                        "&fNice and warm. Crafted with a bucket",
                        "&fof milk, eggs, sugar, and a bottle."
                )
                .base(PotionType.MUNDANE)
                .color(Color.fromRGB(255, 249, 212))
                .addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        if (!matches(event.getItem())) {
            return;
        }

        var player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.0f);
        player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(200, 3));
        player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(200, 1));
        player.addPotionEffect(PotionEffectType.JUMP.createEffect(200, 4));
        player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(600, 2));
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
    }
}
