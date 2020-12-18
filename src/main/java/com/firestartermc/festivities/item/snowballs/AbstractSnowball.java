package com.firestartermc.festivities.item.snowballs;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class AbstractSnowball extends ItemArchetype implements Listener {

    public AbstractSnowball(@NotNull String id, @NotNull String name) {
        super(id, name);
    }

    public AbstractSnowball() {
        super("snowball_fight", "Snowball Fight");
    }

    @Override
    public @NotNull ItemStack getItem() {
        return ItemBuilder.of(Material.SNOWBALL)
                .name("&f&lSnowball Fight")
                .lore(
                        "&7&oWho puts rocks in snowballs?",
                        "&r ",
                        "&fWe've introduced 5 new snowball",
                        "&ftypes! Read the Lectern for recipes,",
                        "&fmake some, and have a snowball fight!"
                )
                .build();
    }

    protected void onHitEntity(LivingEntity entity) {
    }

    @EventHandler
    public void onThrow(PlayerLaunchProjectileEvent event) {
        var player = event.getPlayer();
        var inventory = player.getInventory();

        if (event.getProjectile().getType() != EntityType.SNOWBALL) {
            return;
        }

        if (!matches(event.getItemStack())) {
            return;
        }

        var data = event.getProjectile().getPersistentDataContainer();
        data.set(TYPE_KEY, PersistentDataType.STRING, getId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onHit(EntityDamageByEntityEvent event) {
        var entity = event.getEntity();

        if (!(event.getDamager() instanceof Snowball)) {
            return;
        }

        var data = event.getDamager().getPersistentDataContainer();
        if (!data.has(TYPE_KEY, PersistentDataType.STRING)) {
            return;
        }

        if (!data.get(TYPE_KEY, PersistentDataType.STRING).equals(getId())) {
            return;
        }

        if (entity instanceof LivingEntity) {
            onHitEntity((LivingEntity) entity);
        }
    }
}
