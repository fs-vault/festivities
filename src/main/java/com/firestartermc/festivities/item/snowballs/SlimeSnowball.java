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
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class SlimeSnowball extends AbstractSnowball {

    public SlimeSnowball() {
        super("slime_snowball", "Slime Snowball");
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapelessRecipe(new NamespacedKey(festivities, getId()), getItem());
        recipe.addIngredient(1, Material.SNOWBALL);
        recipe.addIngredient(1, Material.SLIME_BALL);
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.SNOWBALL)
                .name("&#b0ffb4&lSlime Snowball")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .modelData(6)
                .build();
    }

    @Override
    protected void onHitEntity(LivingEntity entity) {
        entity.addPotionEffect(PotionEffectType.SLOW.createEffect(80, 6));
    }
}
