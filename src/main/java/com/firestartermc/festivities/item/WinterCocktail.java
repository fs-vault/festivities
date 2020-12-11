package com.firestartermc.festivities.item;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.Kerosene;
import com.firestartermc.kerosene.item.PotionBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WinterCocktail extends ItemArchetype implements Listener {

    private final NamespacedKey consumedKey;
    private final List<PotionEffectType> effectTypes;

    public WinterCocktail() {
        super("winter_cocktail", "Winter Cocktail");
        this.consumedKey = new NamespacedKey(getId(), "consumed");
        this.effectTypes = Arrays.asList(
                PotionEffectType.REGENERATION,
                PotionEffectType.SPEED,
                PotionEffectType.FIRE_RESISTANCE,
                PotionEffectType.POISON,
                PotionEffectType.HEAL,
                PotionEffectType.NIGHT_VISION,
                PotionEffectType.WEAKNESS,
                PotionEffectType.INCREASE_DAMAGE,
                PotionEffectType.SLOW,
                PotionEffectType.JUMP,
                PotionEffectType.HARM,
                PotionEffectType.WATER_BREATHING,
                PotionEffectType.INVISIBILITY,
                PotionEffectType.SLOW_FALLING
        );
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return new PotionBuilder()
                .name("&#ffee54&lWINTER &#ffd454&lCOCKTAIL")
                .lore(
                        "&fCreate a questionable winter cocktail!",
                        "&fDrink one of every brewable potion.",
                        "&7&o(except Potion of the Turtle Master)",
                        "&r ",
                        getDrankString(0)
                )
                .color(Color.fromRGB(255, 177, 74))
                .addAllItemFlags()
                .enchantUnsafe(Enchantment.MENDING, 1)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    private String getDrankString(int amount) {
        return MessageUtils.formatColors("&f&lYou've drank &#ffe9d6&l" + amount + " &f&ltypes", true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        var player = (Player) event.getPlayer();
        var cocktail = get(player);
        if (cocktail.isEmpty()) {
            return;
        }

        var item = event.getItem();
        if (item.getType() != Material.POTION) {
            return;
        }

        if (matches(item)) {
            event.setCancelled(true);
            return;
        }

        var potionMeta = (PotionMeta) item.getItemMeta();
        var meta = (PotionMeta) cocktail.get().getItemMeta();
        var primaryEffect = potionMeta.getBasePotionData().getType();
        var data = meta.getPersistentDataContainer();
        var consumed = data.getOrDefault(consumedKey, PersistentDataType.INTEGER_ARRAY, new int[15]);
        var index = effectTypes.indexOf(primaryEffect.getEffectType());

        if (consumed[index] == 1) {
            return;
        }

        var completed = consumed[14] + 1;
        consumed[index] = 1;
        consumed[14] = completed;

        if (completed == 14) {
            player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
            player.sendMessage(MessageUtils.formatColors("&#ffe9d6&lCOCKTAIL: &#fff7f0You've brewed a questionable cocktail! Why don't you have a drink...", true));
            player.sendMessage(MessageUtils.formatColors("&#ffe9d6&lCOCKTAIL: &#fff7f0Received $1.2K and 1 hour of 2X Jobs + Skills XP!", true));

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "giveboost " + player.getName() + " SKILLS 2 3600000");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "giveboost " + player.getName() + " JOBS 2 3600000");
            Kerosene.getKerosene().getEconomy().depositPlayer(player, 1200);

            meta.addCustomEffect(PotionEffectType.FAST_DIGGING.createEffect(7000, 3), true);
            meta.addCustomEffect(PotionEffectType.JUMP.createEffect(7000, 3), true);
            meta.addCustomEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(7000, 3), true);
            meta.addCustomEffect(PotionEffectType.DOLPHINS_GRACE.createEffect(7000, 1), true);
            meta.addCustomEffect(PotionEffectType.GLOWING.createEffect(7000, 1), true);
            meta.addCustomEffect(PotionEffectType.LUCK.createEffect(7000, 3), true);
            meta.addCustomEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(7000, 1), true);
            meta.setLore(Collections.singletonList(ChatColor.WHITE + "Tastes a bit off..."));
            data.remove(TYPE_KEY);
            cocktail.get().setItemMeta(meta);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            data.set(consumedKey, PersistentDataType.INTEGER_ARRAY, consumed);
            var lore = meta.getLore();
            lore.set(4, getDrankString(completed));
            meta.setLore(lore);
            cocktail.get().setItemMeta(meta);
        }
    }
}
