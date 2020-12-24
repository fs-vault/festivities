package com.firestartermc.festivities.item.cookies;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class BakedCookie extends ItemArchetype implements Listener {

    public BakedCookie() {
        super("baked_cookie", "Baked Cookie");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return getItemWithType(1);
    }

    @NotNull
    public ItemStack getItemWithType(int type) {
        return ItemBuilder.of(Material.COOKIE)
                .name("&#ffeccc&lBaked Cookie")
                .lore(
                        "&fNice and warm. I'm told the",
                        "&felves are quite fond of these...",
                        "&r ",
                        "&7Shape: " + CookieCutter.getShapeName(type)
                )
                .modelData(3 + type)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        if (!matches(event.getItem())) {
            return;
        }

        var player = event.getPlayer();
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(60, 2));
        player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(20, 4));
        player.addPotionEffect(getEffect(event.getItem().getItemMeta().getCustomModelData() - 3));
        player.setSaturation(20);
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 8));
    }

    private PotionEffect getEffect(int type) {
        return switch (type) {
            case 1 -> PotionEffectType.DAMAGE_RESISTANCE.createEffect(2400, 1);
            case 2 -> PotionEffectType.HEALTH_BOOST.createEffect(2400, 1);
            case 3 -> PotionEffectType.ABSORPTION.createEffect(2400, 2);
            default -> PotionEffectType.SPEED.createEffect(60, 2);
        };
    }
}
