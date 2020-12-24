package com.firestartermc.festivities.item.giftwrapping;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.SkullBuilder;
import com.firestartermc.kerosene.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class WrappedPresent extends ItemArchetype implements Listener {

    public WrappedPresent() {
        super("wrapped_present", "Wrapped Present");
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapedRecipe(new NamespacedKey(festivities, getId()), getItem());
        recipe.shape("yyy", "yxy", "yyy");
        recipe.setIngredient('y', Material.STRING);

        var coveredPresent = new CoveredPresent().getItem();
        recipe.setIngredient('x', new MaterialData(coveredPresent.getType(), coveredPresent.getData().getData()));
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return new SkullBuilder()
                .name("&#ff524d&lWRAPPED PRESENT")
                .lore(
                        "&7&oWonder what's inside...",
                        "&r",
                        "&eRight-click to unwrap it!"
                )
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJl" +
                        "cy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQxZmJlYTljMmQxOTA0MGU1N" +
                        "jdmMzg3YWI0NmIyZjhhM2ExZGE4ZWVjOWQzOTllMmU0YWRjZjA1YWRhOGEyYSJ9fX0=")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            var player = event.getPlayer();

            if (!matches(event.getItem())) {
                return;
            }

            Festivities.INSTANCE.getItem("whooping_stick").ifPresent(stick -> {
                event.getItem().subtract(1);
                PlayerUtils.giveOrDropItem(player, stick.getItem());
                player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.5f);
            });
            event.setCancelled(true);
        }
    }
}
