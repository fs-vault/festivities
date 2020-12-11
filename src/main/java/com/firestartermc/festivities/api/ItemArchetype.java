package com.firestartermc.festivities.api;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.Option;
import java.util.Optional;

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

    public Optional<ItemStack> get(Player player) {
        for (var item : player.getInventory().getContents()) {
            if (matches(item)) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }
}
