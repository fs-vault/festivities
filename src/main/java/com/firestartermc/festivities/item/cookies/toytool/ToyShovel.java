package com.firestartermc.festivities.item.cookies.toytool;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ToyShovel extends ToyTool {

    private final NamespacedKey recipeKey;

    public ToyShovel() {
        super("toy_shovel", "Toy Shovel");
        this.recipeKey = new NamespacedKey(Festivities.INSTANCE, getId());
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapedRecipe(recipeKey, getItem())
                .shape("*", "x", "x")
                .setIngredient('*', new ChunkOfPlastic().getItem())
                .setIngredient('x', new PlasticShaft().getItem());
        festivities.getServer().addRecipe(recipe);
    }

    @Override
    public void unregister(Festivities festivities) {
        festivities.getServer().removeRecipe(recipeKey);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.DIAMOND_SHOVEL)
                .name("&#3d98d4&lToy &#f13f3f&lShovel")
                .lore("&f*squeak*")
                .enchantUnsafe(Enchantment.DIG_SPEED, 8)
                .modelData(1)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }
}
