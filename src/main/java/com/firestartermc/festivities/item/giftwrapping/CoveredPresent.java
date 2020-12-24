package com.firestartermc.festivities.item.giftwrapping;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.SkullBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CoveredPresent extends ItemArchetype implements Listener {

    public CoveredPresent() {
        super("covered_present", "Covered Present");
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapedRecipe(new NamespacedKey(festivities, getId()), getItem());
        recipe.shape("yxy", "x@x", "yxy");
        recipe.setIngredient('y', Material.RED_DYE);
        recipe.setIngredient('x', Material.PAPER);

        var basePresent = new BasePresent().getItem();
        recipe.setIngredient('@', new MaterialData(basePresent.getType(), basePresent.getData().getData()));
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return new SkullBuilder()
                .name("&#ff504a&lCOVERED PRESENT")
                .lore(
                        "&fA present covered in wrapping paper.",
                        "&fUnfortunately, since it's not tied",
                        "&ftogether, the wrapping paper is falling",
                        "&fapart. Get some string and surround",
                        "&fthe present in it to finish wrapping!"
                )
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJl" +
                        "cy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmEzMjlkYjRlZWU0NjU4N2QxO" +
                        "TQ4ZDIyZTUwNTQ4NGYxZWM4MjZiYWExOTM4ZGVkYzUzMjBmYTYzZGYxYTVlIn19fQ==")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (matches(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }
}
