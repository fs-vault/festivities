package com.firestartermc.festivities.api;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public abstract class ItemArchetype implements Listener {

    private static final NamespacedKey TYPE_KEY = new NamespacedKey("firestarter", "item_type");
    private final String id;

    public ItemArchetype(@NotNull String id) {
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public ItemStack getItem() {
        return new ItemStack(Material.AIR);
    }

    public boolean matches(ItemStack item) {
        if (item == null || item.getAmount() < 1) {
            return false;
        }

        var container = item.getItemMeta().getPersistentDataContainer();
        var type = container.get(TYPE_KEY, PersistentDataType.STRING);
        return type != null && type.equals(getId());
    }
}
