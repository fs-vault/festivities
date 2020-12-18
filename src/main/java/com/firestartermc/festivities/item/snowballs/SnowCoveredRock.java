package com.firestartermc.festivities.item.snowballs;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SnowCoveredRock extends AbstractSnowball {

    public SnowCoveredRock() {
        super("snow_covered_rock", "Snow Covered Rock");
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapelessRecipe(new NamespacedKey(festivities, getId()), getItem());
        recipe.addIngredient(1, Material.SNOWBALL);
        recipe.addIngredient(1, Material.COBBLESTONE);
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.SNOWBALL)
                .name("&7&lSnow Covered Rock")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .modelData(5)
                .build();
    }

    @Override
    protected void onHitEntity(LivingEntity entity) {
        entity.damage(6);
    }
}
