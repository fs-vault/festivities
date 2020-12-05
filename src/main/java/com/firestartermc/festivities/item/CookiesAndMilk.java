package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.Kerosene;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import com.firestartermc.kerosene.util.PlayerUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Milk 10 cows with a bucket so the whole town can have
 * enough milk to drink with their cookies.
 *
 * The UUIDs of each cow that was milked is stored as
 * a string in the NBT of the bucket.
 */
public class CookiesAndMilk extends ItemArchetype implements Listener {

    private final NamespacedKey milkedKey;
    private final NamespacedKey cookieKey;
    private final ItemStack cookies;

    public CookiesAndMilk() {
        super("cookies_and_milk", "Cookies and Milk");
        this.milkedKey = new NamespacedKey(getId(), "milked");
        this.cookieKey = new NamespacedKey(getId(), "cookies");
        this.cookies = ItemBuilder.of(Material.COOKIE)
                .amount(10)
                .name("&f&lCOOKIES DIPPED IN MILK")
                .lore(
                        "&fA great bit of food. Restores a",
                        "&ffull hunger bar and maximum",
                        "&fsaturation level. They're also",
                        "&fquite tasty, which is a bonus.",
                        "&7&o(best before 12/25/20)"
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                .persistData(cookieKey, PersistentDataType.BYTE, (byte) 1)
                .build();
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.BUCKET)
                .name("&f&lCOOKIES AND MILK")
                .lore(
                        "&fMilk 10 cows with this bucket",
                        "&fso the whole town can have enough",
                        "&fmilk to drink with their cookies.",
                        "&r ",
                        getMilkedString(0)
                )
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @NotNull
    private String getMilkedString(int milked) {
        return MessageUtils.formatColors("&f&lYou've milked &#e3f9ff&l" + milked + " &f&lcows", true);
    }

    @EventHandler
    public void onMilk(PlayerInteractEntityEvent event) {
        var entity = event.getRightClicked();
        if (entity.getType() != EntityType.COW) {
            return;
        }

        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (!matches(item)) {
            return;
        }

        event.setCancelled(true);
        var meta = item.getItemMeta();
        var data = meta.getPersistentDataContainer();
        var stored = data.getOrDefault(milkedKey, PersistentDataType.STRING, "");
        var milked = Lists.newArrayList(StringUtils.split(stored, "|"));

        if (milked.contains(entity.getUniqueId().toString())) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
            return;
        }

        milked.add(entity.getUniqueId().toString());
        player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 1.0f, 1.0f);

        if (milked.size() == 10) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            player.sendMessage(MessageUtils.formatColors("&f&lMILK: &fYou collected enough milk for your whole town! Received $200 and some cookies for your effort.", true));
            item.setAmount(0);
            PlayerUtils.giveOrDropItem(player, cookies);
            Kerosene.getKerosene().getEconomy().depositPlayer(player, 200);
        } else {
            data.set(milkedKey, PersistentDataType.STRING, String.join("|", milked));
            var lore = meta.getLore();
            lore.set(4, getMilkedString(milked.size()));
            meta.setLore(lore);
            item.setItemMeta(meta);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        var item = event.getItem();
        if (item.getType() != Material.COOKIE) {
            return;
        }

        var meta = item.getItemMeta();
        var data = meta.getPersistentDataContainer();
        var cookie = data.getOrDefault(cookieKey, PersistentDataType.BYTE, (byte) 0);

        if (cookie == 0) {
            return;
        }

        var player = event.getPlayer();
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1.0f, 1.0f);
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(15 * 20, 2));
    }
}
