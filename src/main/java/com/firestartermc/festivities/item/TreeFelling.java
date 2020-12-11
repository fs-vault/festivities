package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.Kerosene;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import com.firestartermc.kerosene.util.PlayerUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class TreeFelling extends ItemArchetype implements Listener {

    private final NamespacedKey felledKey = new NamespacedKey(getId(), "felled");
    private final ListMultimap<Material, Biome> biomeMapping = ArrayListMultimap.create();

    public TreeFelling() {
        super("tree_felling", "Tree Felling");

        var spruceBiomes = Arrays.asList(
                Biome.TAIGA,
                Biome.TAIGA_HILLS,
                Biome.TAIGA_MOUNTAINS,
                Biome.WOODED_MOUNTAINS,
                Biome.SNOWY_TUNDRA,
                Biome.SNOWY_TAIGA,
                Biome.SNOWY_TAIGA_HILLS,
                Biome.SNOWY_TAIGA_MOUNTAINS,
                Biome.GIANT_TREE_TAIGA,
                Biome.GIANT_TREE_TAIGA_HILLS,
                Biome.MOUNTAINS,
                Biome.MOUNTAIN_EDGE
        );

        var oakBiomes = new ArrayList<>(Arrays.asList(Biome.values()));
        oakBiomes.remove(Biome.DESERT);
        oakBiomes.remove(Biome.DESERT_HILLS);
        oakBiomes.remove(Biome.DESERT_LAKES);
        oakBiomes.remove(Biome.BIRCH_FOREST);

        var birchBiomes = Arrays.asList(
                Biome.FOREST,
                Biome.BIRCH_FOREST,
                Biome.DARK_FOREST,
                Biome.TALL_BIRCH_FOREST,
                Biome.TALL_BIRCH_HILLS,
                Biome.WOODED_HILLS,
                Biome.FLOWER_FOREST
        );

        var jungleBiomes = Arrays.asList(
                Biome.BAMBOO_JUNGLE,
                Biome.BAMBOO_JUNGLE_HILLS,
                Biome.JUNGLE,
                Biome.JUNGLE_EDGE,
                Biome.JUNGLE_HILLS,
                Biome.MODIFIED_JUNGLE_EDGE
        );

        var acaciaBiomes = Arrays.asList(
                Biome.SAVANNA,
                Biome.SAVANNA_PLATEAU,
                Biome.SHATTERED_SAVANNA,
                Biome.SHATTERED_SAVANNA_PLATEAU
        );

        var darkOakBiomes = Arrays.asList(
                Biome.DARK_FOREST,
                Biome.DARK_FOREST_HILLS
        );

        var mushroomBiomes = Arrays.asList(
                Biome.MUSHROOM_FIELDS,
                Biome.MUSHROOM_FIELD_SHORE,
                Biome.DARK_FOREST,
                Biome.DARK_FOREST_HILLS,
                Biome.FOREST
        );

        var chorusBiomes = Arrays.asList(
                Biome.END_BARRENS,
                Biome.END_HIGHLANDS,
                Biome.END_MIDLANDS,
                Biome.SMALL_END_ISLANDS
        );

        this.biomeMapping.putAll(Material.OAK_LOG, oakBiomes);
        this.biomeMapping.putAll(Material.SPRUCE_LOG, spruceBiomes);
        this.biomeMapping.putAll(Material.BIRCH_LOG, birchBiomes);
        this.biomeMapping.putAll(Material.JUNGLE_LOG, jungleBiomes);
        this.biomeMapping.putAll(Material.ACACIA_LOG, acaciaBiomes);
        this.biomeMapping.putAll(Material.DARK_OAK_LOG, darkOakBiomes);
        this.biomeMapping.putAll(Material.MUSHROOM_STEM, mushroomBiomes);
        this.biomeMapping.putAll(Material.CHORUS_PLANT, chorusBiomes);
        this.biomeMapping.put(Material.WARPED_STEM, Biome.WARPED_FOREST);
        this.biomeMapping.put(Material.CRIMSON_STEM, Biome.CRIMSON_FOREST);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.IRON_AXE)
                .name("&#a1e6ac&lTREE FOR THE HOLIDAYS")
                .lore(
                        "&fChop down a tree of every",
                        "&ftype from its native biome!",
                        "&7&o(there's 10 types)",
                        "&r ",
                        getFelledString(0)
                )
                .enchant(Enchantment.MENDING, 1)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                .build();
    }

    @NotNull
    private String getFelledString(int amount) {
        return MessageUtils.formatColors("&f&lYou've felled &#e3f9ff&l" + amount + " &f&ltree types", true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        if (!biomeMapping.containsKey(block.getType())) {
            return;
        }

        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (!matches(item)) {
            return;
        }

        if (!biomeMapping.get(block.getType()).contains(block.getBiome())) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
            player.sendMessage(MessageUtils.formatColors("&cThis tree isn't native to this biome.", true));
            return;
        }

        var meta = item.getItemMeta();
        var data = meta.getPersistentDataContainer();
        var felledTypes = Lists.newArrayList(StringUtils.split(data.getOrDefault(felledKey, PersistentDataType.STRING, ""), "|"));

        if (felledTypes.contains(block.getType().name())) {
            return;
        }

        felledTypes.add(block.getType().name());
        var joined = String.join("|", felledTypes);
        data.set(felledKey, PersistentDataType.STRING, joined);

        if (felledTypes.size() == 10) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            player.sendMessage(MessageUtils.formatColors("&#cfe1ff&lFELLING: &fYou've chopped one of every tree type! Plant this sapling in a flower pot to receive your reward.", true));
            item.setAmount(0);
            Festivities.INSTANCE.getItem("christmas_tree_sapling").ifPresent(sapling -> PlayerUtils.giveOrDropItem(player, sapling.getItem()));
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            var lore = meta.getLore();
            lore.set(4, getFelledString(felledTypes.size()));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }
}
