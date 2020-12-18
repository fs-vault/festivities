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

public class SplotchedSnowball extends AbstractSnowball {

    public SplotchedSnowball() {
        super("splotched_snowball", "Splotched Snowball");
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapelessRecipe(new NamespacedKey(festivities, getId()), getItem());
        recipe.addIngredient(1, Material.SNOWBALL);
        recipe.addIngredient(1, Material.INK_SAC);
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.SNOWBALL)
                .name("&#414154&lSplotched Snowball")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .modelData(4)
                .build();
    }

    @Override
    protected void onHitEntity(LivingEntity entity) {
        entity.damage(4);
        entity.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(40, 0));
    }
}
