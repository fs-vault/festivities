package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.item.LeatherArmorBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class FrozenFishing extends ItemArchetype implements Listener {

    private final ItemStack reward;

    public FrozenFishing() {
        super("frozen_fishing");
        this.reward = new LeatherArmorBuilder(LeatherArmorBuilder.LeatherArmorType.BOOTS)
                .name("&#d1daff&lFROSTY &#b8c1ff&lUGGS")
                .lore("&7So cold that they freeze water...")
                .color(Color.fromRGB(130, 170, 255))
                .enchantUnsafe(Enchantment.FROST_WALKER, 4)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .unbreakable()
                .build();
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.FISHING_ROD)
                .name("&#d1daff&lFROZEN &#b8c1ff&lFISHING")
                .lore(
                        "&fCatch a fish in a Frozen Ocean",
                        "&fbiome using this fishing rod to",
                        "&freceive a reward!"
                )
                .enchant(Enchantment.MENDING, 1)
                .enchant(Enchantment.LURE, 3)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                .persistData(Festivities.ITEM_KEY, PersistentDataType.STRING, getId())
                .unbreakable()
                .build();
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        var player = event.getPlayer();
        var location = event.getHook().getLocation();
        if (!matches(player.getInventory().getItemInMainHand())) {
            return;
        }

        if (location.getBlock().getBiome() != Biome.FROZEN_OCEAN) {
            player.playSound(location, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
            event.setCancelled(true);
            return;
        }

        var firework = (Firework) player.getWorld().spawnEntity(location.add(0.5, 0, 0.5), EntityType.FIREWORK);
        var fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(2);
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.BLUE).trail(true).flicker(true).build());
        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();

        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        event.getPlayer().playSound(event.getHook().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        ((Item) event.getCaught()).setItemStack(reward);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "inferno addtokens " + player.getName() + " 20");
        player.sendMessage(MessageUtils.formatColors("&#d1daff&lFROZEN &#b8c1ff&lFISHING: &fReceived a reward + 20 vote tokens.", true));
    }
}
