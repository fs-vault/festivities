package com.firestartermc.festivities.item.candycanes;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class CandyCane extends ItemArchetype implements Listener {

    private final NamespacedKey red, green, purple;
    
    public CandyCane() {
        super("candy_cane", "Candy Cane");
        this.red = new NamespacedKey(Festivities.INSTANCE, getId() + "_red");
        this.green = new NamespacedKey(Festivities.INSTANCE, getId() + "_green");
        this.purple = new NamespacedKey(Festivities.INSTANCE, getId() + "_purple");
    }

    @Override
    public void register(Festivities festivities) {
        Bukkit.getServer().addRecipe(new ShapedRecipe(red, getItemWithType(1))
                .shape("***", "*x*", "***")
                .setIngredient('*', Material.SUGAR)
                .setIngredient('x', Material.RED_DYE));

        Bukkit.getServer().addRecipe(new ShapedRecipe(green, getItemWithType(2))
                .shape("***", "*x*", "***")
                .setIngredient('*', Material.SUGAR)
                .setIngredient('x', Material.GREEN_DYE));

        Bukkit.getServer().addRecipe(new ShapedRecipe(purple, getItemWithType(3))
                .shape("***", "*x*", "***")
                .setIngredient('*', Material.SUGAR)
                .setIngredient('x', Material.PURPLE_DYE));
    }

    @Override
    public void unregister(Festivities festivities) {
        Bukkit.getServer().removeRecipe(red);
        Bukkit.getServer().removeRecipe(green);
        Bukkit.getServer().removeRecipe(purple);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return getItemWithType(1);
    }

    @NotNull
    public ItemStack getItemWithType(int type) {
        return ItemBuilder.of(Material.TROPICAL_FISH)
                .name("&#ffd9d9&lCandy Cane")
                .lore(
                        "&fA lovely little snack.",
                        "&7Crafted with sugar around",
                        "&7a dye (red, green, purple)."
                )
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .modelData(type)
                .build();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onConsume(PlayerItemConsumeEvent event) {
        if (!matches(event.getItem())) {
            return;
        }

        var player = event.getPlayer();
        player.addPotionEffect(getEffect(event.getItem().getItemMeta().getCustomModelData()));
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 8));
        player.setSaturation(Math.min(20, player.getSaturation() + 8));
    }

    private PotionEffect getEffect(int type) {
        return switch (type) {
            case 1 -> PotionEffectType.REGENERATION.createEffect(2400, 2);
            case 2 -> PotionEffectType.LUCK.createEffect(2400, 2);
            case 3 -> PotionEffectType.DOLPHINS_GRACE.createEffect(2400, 2);
            default -> PotionEffectType.SPEED.createEffect(2400, 1);
        };
    }
}
