package com.firestartermc.festivities.api;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public abstract class ItemArchetype {

    protected static final NamespacedKey TYPE_KEY = new NamespacedKey("firestarter", "item_type");
    private final String id;
    private final String name;

    public ItemArchetype(@NotNull String id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
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
