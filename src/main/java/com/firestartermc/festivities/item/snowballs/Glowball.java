package com.firestartermc.festivities.item.snowballs;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class Glowball extends AbstractSnowball {

    public Glowball() {
        super("glowball", "Glowball");
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapelessRecipe(new NamespacedKey(festivities, getId()), getItem());
        recipe.addIngredient(1, Material.SNOWBALL);
        recipe.addIngredient(1, Material.GLOWSTONE_DUST);
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.SNOWBALL)
                .name("&#fcec56&lGlowball")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .modelData(1)
                .build();
    }

    @Override
    protected void onHitEntity(LivingEntity entity) {
        entity.addPotionEffect(PotionEffectType.GLOWING.createEffect(1200, 0));
    }
}
