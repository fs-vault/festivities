package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BlockBingo extends ItemArchetype implements Listener {

    private final BufferedImage bingo;
    private final BufferedImage found;
    private final List<Material> blockTypes;
    private final Map<Material, BufferedImage> imageCache;

    public BlockBingo() throws IOException {
        super("block_bingo", "Block Bingo");
        this.imageCache = new HashMap<>();
        this.blockTypes = Arrays.asList(
                Material.RED_WOOL,
                Material.PINK_WOOL,
                Material.YELLOW_WOOL,
                Material.GREEN_WOOL,
                Material.LIME_WOOL,
                Material.ORANGE_WOOL,
                Material.OAK_LOG,
                Material.ACACIA_PLANKS,
                Material.NETHER_QUARTZ_ORE,
                Material.LAPIS_ORE,
                Material.HONEYCOMB_BLOCK,
                Material.LIME_SHULKER_BOX,
                Material.GRANITE,
                Material.SEA_LANTERN,
                Material.RED_SANDSTONE,
                Material.OBSIDIAN,
                Material.LODESTONE,
                Material.RESPAWN_ANCHOR,
                Material.SMITHING_TABLE,
                Material.OBSERVER,
                Material.TNT,
                Material.NOTE_BLOCK,
                Material.REDSTONE_LAMP,
                Material.STICKY_PISTON,
                Material.BARREL,
                Material.CAULDRON,
                Material.RED_NETHER_BRICKS,
                Material.RED_MUSHROOM_BLOCK,
                Material.BROWN_MUSHROOM_BLOCK,
                Material.CRYING_OBSIDIAN,
                Material.GILDED_BLACKSTONE,
                Material.SHROOMLIGHT,
                Material.NETHERITE_BLOCK,
                Material.PURPUR_BLOCK,
                Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
                Material.ANVIL,
                Material.BRAIN_CORAL_BLOCK,
                Material.MAGMA_BLOCK,
                Material.DRIED_KELP_BLOCK,
                Material.JACK_O_LANTERN,
                Material.CAKE,
                Material.SLIME_BLOCK,
                Material.SPONGE,
                Material.BOOKSHELF,
                Material.DARK_PRISMARINE,
                Material.BONE_BLOCK,
                Material.BRICKS
        );

        for (var material : this.blockTypes) {
            this.imageCache.put(material, ImageIO.read(Festivities.INSTANCE.getResource("block_bingo/blocks/" + material.name().toLowerCase() + ".png")));
        }

        this.bingo = ImageIO.read(Festivities.INSTANCE.getResource("block_bingo/bingo.png"));
        this.found = ImageIO.read(Festivities.INSTANCE.getResource("block_bingo/found.png"));
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        var view = Bukkit.getServer().createMap(Bukkit.getWorld("world"));
        view.getRenderers().forEach(view::removeRenderer);
        view.addRenderer(new GridRenderer());
        view.setLocked(true);

        var map = ItemBuilder.of(Material.FILLED_MAP)
                .name("&f&lBLOCK BINGO CARD")
                .lore(
                        "&fFill out your bingo card by collecting",
                        "&fall the block types drawn on it!",
                        "&cExpires 12/8/20"
                )
                .addAllItemFlags()
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
        var meta = (MapMeta) map.getItemMeta();
        meta.setMapView(view);
        map.setItemMeta(meta);
        return map;
    }

    @Nullable
    private ItemStack getMap(Player player) {
        for (var item : player.getInventory().getContents()) {
            if (matches(item)) {
                return item;
            }
        }

        return null;
    }

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        var map = getMap(event.getPlayer());
        if (map == null) {
            return;
        }

        var meta = map.getItemMeta();
        var renderer = (GridRenderer) ((MapMeta) meta).getMapView().getRenderers().get(0);
        renderer.found(event.getPlayer(), event.getItem().getItemStack().getType());
    }

    private class GridRenderer extends MapRenderer {

        private final List<Material> blocks = new ArrayList<>(16);
        private boolean shouldRender = true;

        public GridRenderer() {
            for (int i = 0; i < 16; i++) {
                var random = blockTypes.get(ThreadLocalRandom.current().nextInt(blockTypes.size()));
                blocks.add(random);
            }
        }

        public void found(Player player, Material type) {
            if (!blocks.contains(type)) {
                return;
            }

            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i) == type) {
                    blocks.set(i, Material.AIR);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
            }

            shouldRender = true;
        }

        @Override
        public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
            if (!shouldRender) {
                return;
            }

            if (new HashSet<>(blocks).size() == 1) {
                canvas.drawImage(0, 0, bingo);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "inferno addtokens " + player.getName() + " 10");
                player.sendMessage(MessageUtils.formatColors("&#d1daff&lBINGO: &fYou won and received 10 vote tokens!", true));
                shouldRender = false;
                return;
            }

            for (int i = 0; i < blocks.size(); i++) {
                int x = i % 4;
                int y = i / 4;

                var image = blocks.get(i) == Material.AIR ? found : imageCache.get(blocks.get(i));
                canvas.drawImage(x * 32, y * 32, image);
            }

            shouldRender = false;
        }
    }
}
