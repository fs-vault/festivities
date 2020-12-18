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

public class MudBall extends AbstractSnowball {

    public MudBall() {
        super("mud_ball", "Mud Ball");
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapelessRecipe(new NamespacedKey(festivities, getId()), getItem());
        recipe.addIngredient(1, Material.SNOWBALL);
        recipe.addIngredient(1, Material.DIRT);
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.SNOWBALL)
                .name("&#805332&lMud Ball")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .modelData(3)
                .build();
    }

    @Override
    protected void onHitEntity(LivingEntity entity) {
        entity.damage(4);
        entity.addPotionEffect(PotionEffectType.SLOW.createEffect(40, 0));
    }
}
