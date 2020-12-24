package com.firestartermc.festivities.item.cookies.toytool;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PlasticShaft extends ItemArchetype {

    private final NamespacedKey recipeKey;

    public PlasticShaft() {
        super("plastic_shaft", "Plastic Shaft");
        this.recipeKey = new NamespacedKey(Festivities.INSTANCE, getId());
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapedRecipe(recipeKey, getItem())
                .shape("*", "*")
                .setIngredient('*', new ChunkOfPlastic().getItem());
        festivities.getServer().addRecipe(recipe);
    }

    @Override
    public void unregister(Festivities festivities) {
        festivities.getServer().removeRecipe(recipeKey);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.STICK)
                .name("&#3d98d4&lPlastic &#f13f3f&lShaft")
                .lore(
                        "&fA strong base for a tool rod."
                )
                .modelData(1)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }
}
