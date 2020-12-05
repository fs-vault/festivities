package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.Kerosene;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ChristmasTreeSapling extends ItemArchetype implements Listener {

    private final World world;
    private final Material[][][] tree = new Material[3][5][3];

    public ChristmasTreeSapling() {
        super("christmas_tree_sapling", "Christmas Tree Sapling");
        this.world = Bukkit.getWorld("world");

        tree[1][0][1] = Material.SPRUCE_LOG;
        tree[1][1][1] = Material.SPRUCE_LOG;
        tree[1][2][1] = Material.SPRUCE_LOG;
        tree[0][1][0] = Material.SPRUCE_LEAVES;
        tree[1][1][0] = Material.SPRUCE_LEAVES;
        tree[2][1][0] = Material.SPRUCE_LEAVES;
        tree[0][1][1] = Material.SPRUCE_LEAVES;
        tree[0][1][2] = Material.SPRUCE_LEAVES;
        tree[1][1][2] = Material.SPRUCE_LEAVES;
        tree[2][1][2] = Material.SPRUCE_LEAVES;
        tree[2][1][1] = Material.SPRUCE_LEAVES;
        tree[1][2][0] = Material.SPRUCE_LEAVES;
        tree[0][2][1] = Material.SPRUCE_LEAVES;
        tree[1][2][2] = Material.SPRUCE_LEAVES;
        tree[2][2][1] = Material.SPRUCE_LEAVES;
        tree[1][3][1] = Material.SPRUCE_LEAVES;
        tree[0][2][0] = Material.SNOW;
        tree[2][2][0] = Material.SNOW;
        tree[0][2][2] = Material.SNOW;
        tree[2][2][2] = Material.SNOW;
        tree[0][3][1] = Material.SNOW;
        tree[1][3][0] = Material.SNOW;
        tree[1][3][2] = Material.SNOW;
        tree[2][3][1] = Material.SNOW;
        tree[1][4][1] = Material.SNOW;
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.SPRUCE_SAPLING)
                .name("&#a1e6ac&lCHRISTMAS TREE &#94d19e&lSAPLING")
                .lore("&fPlant this sapling in a flower", "&fpot and feel the Christmas spirit!")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        var block = event.getClickedBlock();
        if (block == null || !Tag.FLOWER_POTS.isTagged(block.getType())) {
            return;
        }

        // TODO offhand support
        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (item.getItemMeta() == null) return;
        var container = item.getItemMeta().getPersistentDataContainer();
        var type = container.get(TYPE_KEY, PersistentDataType.STRING);

        if (type == null || !type.equals("christmas_tree_sapling")) {
            return;
        }

        var firework = (Firework) block.getWorld().spawnEntity(block.getLocation().add(0.5, 6, 0.5), EntityType.FIREWORK);
        var fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(2);
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.RED).trail(true).flicker(true).build());
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.LIME).trail(true).flicker(true).with(FireworkEffect.Type.BALL_LARGE).build());
        firework.setFireworkMeta(fireworkMeta);

        Bukkit.getScheduler().runTaskLater(Festivities.INSTANCE, firework::detonate, 10L);

        player.sendMessage(MessageUtils.formatColors("&#cfe1ff&lFELLING: &fYou've completed today's challenge and received $1.2K and 5 vote tokens.", true));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "inferno addtokens " + player.getName() + " 20");
        Kerosene.getKerosene().getEconomy().depositPlayer(player, 1200);

        Vector origin = block.getLocation().toVector().subtract(new Vector(1, 0, 1));
        for (var x = 0; x < 3; x++) {
            for (var y = 0; y < 5; y++) {
                for (var z = 0; z < 3; z++) {
                    var currentBlock = world.getBlockAt(origin.getBlockX() + x, origin.getBlockY() + y, origin.getBlockZ() + z);
                    if (currentBlock.getType() != Material.AIR) {
                        continue;
                    }

                    var material = tree[x][y][z];
                    if (material != null) {
                        currentBlock.setType(material);
                    }
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(Festivities.INSTANCE, () -> block.setType(Material.SPRUCE_LOG), 5L);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        player.sendTitle(MessageUtils.formatColors("&#cfe1ff&lCHALLENGE COMPLETE!", true), "You've received $1.2K and 5 vote tokens", 10, 140, 10);
    }
}
