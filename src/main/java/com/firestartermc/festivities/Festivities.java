package com.firestartermc.festivities;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.festivities.command.GiveItemArchetype;
import com.firestartermc.festivities.item.FrozenFishing;
import com.firestartermc.festivities.item.MagicalSnowGlobe;
import com.firestartermc.festivities.item.SnowmanScanner;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Festivities extends JavaPlugin {

    public static final NamespacedKey ITEM_KEY = new NamespacedKey("firestarter", "item_type");
    public final Map<String, ItemArchetype> registeredItems = new HashMap<>();

    @Override
    public void onEnable() {
        getCommand("giveitemarchetype").setExecutor(new GiveItemArchetype(this));
        register(new MagicalSnowGlobe());
        register(new SnowmanScanner());
        register(new FrozenFishing());
    }

    @Override
    public void onDisable() {
    }

    private void register(@NotNull ItemArchetype item) {
        registeredItems.put(item.getId(), item);

        if (item instanceof Listener) {
            getServer().getPluginManager().registerEvents((Listener) item, this);
        }

        if (item instanceof Runnable) {
            getServer().getScheduler().runTaskTimer(this, (Runnable) item, 0L, 30L);
        }
    }

    @NotNull
    public Map<String, ItemArchetype> getRegisteredItems() {
        return registeredItems;
    }

    @NotNull
    public Optional<ItemArchetype> getItem(@NotNull String id) {
        return Optional.ofNullable(registeredItems.get(id));
    }
}
