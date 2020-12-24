package com.firestartermc.festivities.item.frozenscythe;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class FrozenScythe extends ItemArchetype implements Listener {

    public FrozenScythe() {
        super("frozen_scythe", "Frozen Scythe");
    }

    @Override
    public void register(Festivities festivities) {
        /*
        var recipe = new ShapedRecipe(new NamespacedKey(festivities, getId()), getItem());
        recipe.shape(" **", "  %", "  %");
        festivities.getItem("compressed_frozen_shards").ifPresent(shardBlock -> recipe.setIngredient('*', shardBlock.getItem()));
        recipe.setIngredient('%', Material.STICK);
        Bukkit.getServer().addRecipe(recipe);
         */
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.DIAMOND_HOE)
                .name("&#cadbed&lFrozen Scythe")
                .enchantUnsafe(Enchantment.DIG_SPEED, 6)
                .enchantUnsafe(Enchantment.DURABILITY, 4)
                .enchantUnsafe(Enchantment.DAMAGE_ALL, 4)
                .addLore("&7Sweeping X")
                .addLore("&7Frostbite I")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onTill(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var block = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (block == null || !(block.getType() == Material.DIRT || block.getType() == Material.GRASS_BLOCK)) {
            return;
        }

        if (!holding(event.getPlayer())) {
            return;
        }

        till(block.getRelative(BlockFace.NORTH));
        till(block.getRelative(BlockFace.EAST));
        till(block.getRelative(BlockFace.SOUTH));
        till(block.getRelative(BlockFace.WEST));
        till(block.getRelative(BlockFace.NORTH_EAST));
        till(block.getRelative(BlockFace.SOUTH_EAST));
        till(block.getRelative(BlockFace.SOUTH_WEST));
        till(block.getRelative(BlockFace.NORTH_WEST));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        var player = (Player) event.getDamager();
        if (!matches(player.getInventory().getItemInMainHand())) {
            return;
        }

        var entity = (LivingEntity) event.getEntity();
        entity.addPotionEffect(PotionEffectType.SLOW.createEffect(20 * 6, 3));
        entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);

        // sweeping-type attack
        entity.getLocation().getNearbyEntities(1, 1, 1).stream()
                .filter(nearbyEntity -> nearbyEntity instanceof LivingEntity)
                .filter(nearbyEntity -> !(nearbyEntity instanceof Player))
                .map(nearbyEntity -> (LivingEntity) nearbyEntity)
                .forEach(nearbyEntity -> nearbyEntity.damage(2));
    }

    private void till(Block block) {
        if (!(block.getType() == Material.GRASS_BLOCK || block.getType() == Material.DIRT)) {
            return;
        }

        if (block.getRelative(BlockFace.UP).getType() != Material.AIR) {
            return;
        }

        block.setType(Material.FARMLAND);
    }

    // idiot proofing it this time :>>
    @EventHandler
    public void onConvert(InventoryClickEvent event) {
        var inventory = event.getInventory();
        if (inventory.getType() != InventoryType.SMITHING) {
            return;
        }

        if (matches(inventory.getItem(0))) {
            event.setCancelled(true);
        }
    }
}
