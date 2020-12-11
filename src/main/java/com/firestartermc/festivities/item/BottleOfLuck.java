package com.firestartermc.festivities.item;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.PotionBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

public class BottleOfLuck extends ItemArchetype implements Listener {

    public BottleOfLuck() {
        super("bottle_of_luck", "Bottle of Luck");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return new PotionBuilder()
                .name("&#b5ffc1&lBOTTLE OF LUCK")
                .lore(
                        "&fDrink this potion to receive an",
                        "&fhour of Luck and 2x jobs and skills",
                        "&fXP boosts for 2 hours &#dbffe1(/boost)"
                )
                .base(PotionType.LUCK)
                .color(Color.fromRGB(107, 255, 132))
                .addAllItemFlags()
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        var item = event.getItem();
        if (item.getType() != Material.POTION) {
            return;
        }

        if (!matches(item)) {
            return;
        }

        var player = event.getPlayer();
        player.addPotionEffect(PotionEffectType.LUCK.createEffect(72000, 0));
        player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "giveboost " + player.getName() + " SKILLS 2 7200");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "giveboost " + player.getName() + " JOBS 2 7200");
        player.sendMessage(MessageUtils.formatColors("&#b5ffc1&lLUCK: &#e3ffe8You've received 1 hour of Luck and 2 hours of 2x jobs and skills boosts (check /boosts).", true));
    }
}
