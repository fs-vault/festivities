package com.firestartermc.festivities.item;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.PlayerUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SnowballFight extends ItemArchetype implements Listener {

    private final NamespacedKey holderKey;
    private final NamespacedKey hitKey;

    public SnowballFight() {
        super("snowball_fight", "Snowball Fight");
        this.holderKey = new NamespacedKey(getId(), "holder");
        this.hitKey = new NamespacedKey(getId(), "hit");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.SNOWBALL)
                .name("&f&lSNOWBALL FIGHT")
                .lore(
                        "&fThrow this snowball at 6",
                        "&fother players on the server!"
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onThrow(PlayerLaunchProjectileEvent event) {
        var player = event.getPlayer();
        var inventory = player.getInventory();

        if (event.getProjectile().getType() != EntityType.SNOWBALL) {
            return;
        }

        if (!(matches(inventory.getItemInMainHand()) || matches(inventory.getItemInOffHand()))) {
            return;
        }

        var data = event.getProjectile().getPersistentDataContainer();
        data.set(TYPE_KEY, PersistentDataType.STRING, getId());
        data.set(holderKey, PersistentDataType.STRING, player.getName());
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        var entity = event.getHitEntity();
        if (entity == null || entity.getType() != EntityType.PLAYER) {
            return;
        }

        var data = event.getEntity().getPersistentDataContainer();
        if (!data.has(TYPE_KEY, PersistentDataType.STRING)) {
            return;
        }

        if (!data.get(TYPE_KEY, PersistentDataType.STRING).equals(getId())) {
            return;
        }

        var holder = data.get(holderKey, PersistentDataType.STRING);
        var hitPlayers = Lists.newArrayList(StringUtils.split(data.getOrDefault(hitKey, PersistentDataType.STRING, ""), "|"));

        if (hitPlayers.contains(entity.getName())) {
            return;
        }

        hitPlayers.add(entity.getName());
        var holderPlayer = Bukkit.getPlayer(holder);
        holderPlayer.playSound(holderPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        if (hitPlayers.size() == 6) {
            // Award prize
        } else {
            var newItem = ItemBuilder.of(getItem())
                    .persistData(hitKey, PersistentDataType.STRING, StringUtils.join(hitPlayers, "|"))
                    .build();
            PlayerUtils.giveOrDropItem(holderPlayer, newItem);
        }
    }
}
