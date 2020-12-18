package com.firestartermc.festivities;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.festivities.command.GiveItemArchetype;
import com.firestartermc.festivities.item.*;
import com.firestartermc.festivities.item.frozenscythe.CompressedFrozenShards;
import com.firestartermc.festivities.item.frozenscythe.FrozenScythe;
import com.firestartermc.festivities.item.frozenscythe.FrozenShard;
import com.firestartermc.festivities.item.snowballs.AbstractSnowball;
import com.firestartermc.festivities.item.snowballs.Glowball;
import com.firestartermc.festivities.item.snowballs.MudBall;
import com.firestartermc.festivities.item.snowballs.SlimeSnowball;
import com.firestartermc.festivities.item.snowballs.SnowCoveredRock;
import com.firestartermc.festivities.item.snowballs.SplotchedSnowball;
import com.firestartermc.festivities.item.snowballs.WarCrimeSnowball;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
        register(
                new MagicalSnowGlobe(),
                new SnowmanScanner(),
                new FrozenFishing(),
                new CookiesAndMilk(),
                new TreeFelling(),
                new ChristmasTreeSapling(),
                new HotCocoa(),
                new WinterCocktail(),
                new BottleOfLuck(),
                new FlightPotion(),
                new WorldDomination(),
                new ElfFishing(),
                // new FrozenShard(),
                //new CompressedFrozenShards(),
                new FrozenScythe(),
                new Mistletoe(),
                new Eggnog(),
                new CompactIgloo(),
                new Glowball(),
                new WarCrimeSnowball(),
                new MudBall(),
                new SplotchedSnowball(),
                new SnowCoveredRock(),
                new SlimeSnowball(),
                new AbstractSnowball()
                // new SnowballFight()
        );

        /*
        try {
            register(new BlockBingo());
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
    }

    @Override
    public void onDisable() {
    }

    private void register(@NotNull ItemArchetype... items) {
        for (var item : items) {
            registeredItems.put(item.getId(), item);

            if (item instanceof Listener) {
                getServer().getPluginManager().registerEvents((Listener) item, this);
            }

            if (item instanceof Runnable) {
                getServer().getScheduler().runTaskTimer(this, (Runnable) item, 0L, 30L);
            }

            try {
                item.register(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
