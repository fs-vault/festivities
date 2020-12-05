package com.firestartermc.festivities;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.festivities.command.GiveItemArchetype;
import com.firestartermc.festivities.item.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Festivities extends JavaPlugin {

    public static Festivities INSTANCE;
    public final Map<String, ItemArchetype> registeredItems = new HashMap<>();

    @Override
    public void onEnable() {
        INSTANCE = this;
        getCommand("giveitemarchetype").setExecutor(new GiveItemArchetype(this));
        register(new MagicalSnowGlobe());
        register(new SnowmanScanner());
        register(new FrozenFishing());
        register(new CookiesAndMilk());
        register(new TreeFelling(this));
        register(new ChristmasTreeSapling());
        register(new HotCocoa());
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
