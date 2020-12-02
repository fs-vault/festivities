package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.SkullBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MagicalSnowGlobe extends ItemArchetype implements Listener, Runnable {

    private final List<Biome> requiredBiomes;

    public MagicalSnowGlobe() {
        super("magical_snow_globe");
        this.requiredBiomes = Arrays.asList(
                Biome.BADLANDS,
                Biome.FROZEN_OCEAN,
                Biome.BIRCH_FOREST,
                Biome.SUNFLOWER_PLAINS,
                Biome.BAMBOO_JUNGLE
        );
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        var req = getRequiredBiomes().stream()
                .map(Enum::name)
                .collect(Collectors.joining("|"));

        return new SkullBuilder()
                .name("&#c891ff&lMAGICAL &#9196ff&lSNOW GLOBE")
                .lore(
                        "&fVisit the following biomes to activate",
                        "&fthe snow globe and unlock a reward!",
                        "&r ",
                        "&7- &#ffa28fBadlands",
                        "&7- &#b0e5ffFrozen Ocean",
                        "&7- &#fff3b0Birch Forest",
                        "&7- &#ffdfb0Sunflower Plains",
                        "&7- &#b0ffb7Bamboo Jungle"
                )
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmU" +
                        "vNmRkNjYzMTM2Y2FmYTExODA2ZmRiY2E2YjU5NmFmZDg1MTY2YjRlYzAyMTQyYzhkNWFjODk0MWQ4OWFiNyJ9fX0=")
                .persistData(Festivities.ITEM_KEY, PersistentDataType.STRING, "magical_snow_globe")
                .persistData(new NamespacedKey("magical_snow_globe", "locked_biomes"), PersistentDataType.STRING, req)
                .build();
    }

    public List<Biome> getRequiredBiomes() {
        return requiredBiomes;
    }

    public boolean isBiomeRequired(@NotNull Biome biome) {
        return requiredBiomes.contains(biome);
    }

    @EventHandler
    public void onPlaceAttempt(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        if (matches(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            var contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (matches(contents[i])) {
                    updateSnowGlobe(player, contents[i], i);
                }
            }
        }
    }

    // yeah this is a mess, but it works fine enough considering it on a whim
    private void updateSnowGlobe(Player player, ItemStack item, int slot) {
        var biome = player.getLocation().getBlock().getBiome();
        if (!isBiomeRequired(biome)) {
            return;
        }

        var key = new NamespacedKey("magical_snow_globe", "locked_biomes");
        var meta = item.getItemMeta();
        var data = meta.getPersistentDataContainer();
        var stored = data.getOrDefault(key, PersistentDataType.STRING, "");
        var lockedBiomes = Lists.newArrayList(StringUtils.split(stored, "|"));

        if (!lockedBiomes.contains(biome.name())) {
            return;
        }

        lockedBiomes.remove(biome.name());
        data.set(key, PersistentDataType.STRING, String.join("|", lockedBiomes));

        var lore = new ArrayList<>(meta.getLore());
        for (int i = 0; i < lore.size(); i++) {
            var capitalizedName = WordUtils.capitalize(biome.name().replace("_", " ").toLowerCase());
            if (lore.get(i).contains(capitalizedName)) {
                player.sendMessage(MessageUtils.formatColors("&#9196ff&lSNOW GLOBE: &fFound " + capitalizedName + "!", true));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                lore.set(i, MessageUtils.formatColors("&#96ff9c&l✔ " + capitalizedName, true));
            }
        }

        if (lockedBiomes.size() == 0) {
            player.sendMessage(MessageUtils.formatColors("&#9196ff&lSNOW GLOBE: &#cd91ffYou activated the snow globe and received a Luminous key + $1.2K!", true));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " luminous 1");
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " 1200");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        player.getInventory().setItem(slot, item);
        player.updateInventory();
    }
}
