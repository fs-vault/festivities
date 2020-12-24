package com.firestartermc.festivities;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.festivities.command.GiveItemArchetype;
import com.firestartermc.festivities.item.*;
import com.firestartermc.festivities.item.candycanes.CandyCane;
import com.firestartermc.festivities.item.cookies.BakedCookie;
import com.firestartermc.festivities.item.cookies.CookieCutter;
import com.firestartermc.festivities.item.cookies.CookieDough;
import com.firestartermc.festivities.item.cookies.toytool.ChunkOfPlastic;
import com.firestartermc.festivities.item.cookies.toytool.PlasticShaft;
import com.firestartermc.festivities.item.cookies.toytool.ToyAxe;
import com.firestartermc.festivities.item.cookies.toytool.ToyPickaxe;
import com.firestartermc.festivities.item.cookies.UnbakedCookie;
import com.firestartermc.festivities.item.cookies.toytool.ToyShovel;
import com.firestartermc.festivities.item.frozenscythe.FrozenScythe;
import com.firestartermc.festivities.item.giftwrapping.BasePresent;
import com.firestartermc.festivities.item.giftwrapping.CoveredPresent;
import com.firestartermc.festivities.item.giftwrapping.WhoopingStick;
import com.firestartermc.festivities.item.giftwrapping.WrappedPresent;
import com.firestartermc.festivities.item.snowballs.AbstractSnowball;
import com.firestartermc.festivities.item.snowballs.Glowball;
import com.firestartermc.festivities.item.snowballs.MudBall;
import com.firestartermc.festivities.item.snowballs.SlimeSnowball;
import com.firestartermc.festivities.item.snowballs.SnowCoveredRock;
import com.firestartermc.festivities.item.snowballs.SplotchedSnowball;
import com.firestartermc.festivities.item.snowballs.WarCrimeSnowball;
import net.minecraft.server.v1_16_R3.IRecipe;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Festivities extends JavaPlugin {

    public static Festivities INSTANCE;
    public final Map<String, ItemArchetype> registeredItems = new HashMap<>();

    @Override
    public void onEnable() {
        INSTANCE = this;

        var craftingManager = ((CraftWorld) Bukkit.getWorld("world")).getHandle().getCraftingManager();
        try {
            var list = craftingManager.getClass().getDeclaredField("ALL_RECIPES_CACHE");
            list.setAccessible(true);
            var castList = (List<IRecipe<?>>) list.get(null);
            castList.clear();
            System.out.println(craftingManager.recipes.size());
            craftingManager.recipes.values().forEach(recipe -> {
                System.out.println(recipe.size());
                castList.addAll(recipe.values());
            });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

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
                // new CompressedFrozenShards(),
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
                new AbstractSnowball(),
                new BasePresent(),
                new CoveredPresent(),
                new WrappedPresent(),
                new WhoopingStick(),
                // new SnowballFight()

                new CookieDough(),
                new CookieCutter(),
                new UnbakedCookie(),
                new BakedCookie(),
                new ChunkOfPlastic(),
                new PlasticShaft(),
                new ToyPickaxe(),
                new ToyAxe(),
                new ToyShovel(),

                new CandyCane(),
                new SantaHat(),
                new Stocking()
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
        for (var item : registeredItems.values()) {
            try {
                item.unregister(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
