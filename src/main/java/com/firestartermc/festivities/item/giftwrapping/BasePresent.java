package com.firestartermc.festivities.item.giftwrapping;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.SkullBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class BasePresent extends ItemArchetype implements Listener {

    public BasePresent() {
        super("base_present", "Base Present");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return new SkullBuilder()
                .name("&#ffe5ba&lGIFT WRAPPING")
                .lore(
                        "&fLet's wrap some presents in preparation",
                        "&ffor Christmas! Cover this teddy bear in",
                        "&fwrapping paper and dye (recipe in the",
                        "&flectern for reference)."
                )
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0" +
                        "dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVmNWJmODUxMGZmY" +
                        "2QzYTVlOWQ3ODI1YjY0MzMzYTEyMWQ1NjFmZTJjZGQ3NjdjN2UxOG" +
                        "I4Y2M1MjFiNiJ9fX0=")
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
