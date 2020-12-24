package com.firestartermc.festivities.item;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SantaHat extends ItemArchetype implements Listener {

    public SantaHat() {
        super("santa_hat", "Santa Hat");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.MOJANG_BANNER_PATTERN)
                .name("&#ff675c&lSanta's Hat")
                .lore(
                        "&fNice and comfortable.",
                        "&7Right-click in your hand",
                        "&7to wear it!"
                )
                .modelData(1)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onEquip(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (!matches(event.getItem())) {
            return;
        }

        var player = event.getPlayer();
        var equipment = player.getEquipment();

        if (equipment == null) {
            return;
        }

        var helmet = equipment.getHelmet();

        if (helmet != null && helmet.getType() != Material.AIR) {
            return;
        }

        equipment.setHelmet(getItem());
        event.getItem().subtract(1);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.0f, 1.0f);
    }

    // idiot proofing it this time :>>
    @EventHandler
    public void onConvert(InventoryClickEvent event) {
        var inventory = event.getInventory();
        if (inventory.getType() != InventoryType.LOOM) {
            return;
        }

        if (matches(inventory.getItem(2))) {
            event.setCancelled(true);
        }
    }
}
