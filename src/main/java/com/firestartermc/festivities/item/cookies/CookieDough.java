package com.firestartermc.festivities.item.cookies;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CookieDough extends ItemArchetype implements Listener {

    private final NamespacedKey recipeKey;

    public CookieDough() {
        super("cookie_dough", "Cookie Dough");
        this.recipeKey = new NamespacedKey(Festivities.INSTANCE, getId());
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapelessRecipe(recipeKey, getItem());
        recipe.addIngredient(2, Material.EGG);
        recipe.addIngredient(1, Material.WATER_BUCKET);
        recipe.addIngredient(1, Material.WHEAT);
        recipe.addIngredient(1, Material.SUGAR);
        festivities.getServer().addRecipe(recipe);
    }

    @Override
    public void unregister(Festivities festivities) {
        festivities.getServer().removeRecipe(recipeKey);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.ORANGE_DYE)
                .name("&#ffe4c4&lCookie Dough")
                .lore(
                        "&fTastes pretty good on its own,",
                        "&fbut is a lot better when baked",
                        "&finto a nice, homemade cookie.",
                        "&r ",
                        "&7Ingredients:",
                        "&72 Eggs, Water, Wheat, and Sugar"
                )
                .modelData(1)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }
}
