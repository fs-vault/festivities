package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.Kerosene;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.item.LeatherArmorBuilder;
import com.firestartermc.kerosene.item.SkullBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import com.firestartermc.kerosene.util.PlayerUtils;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityZombie;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftFishHook;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ElfFishing extends ItemArchetype implements Listener {

    private final NamespacedKey elfKey;

    public ElfFishing() {
        super("elf_fishing", "Elf Fishing");
        this.elfKey = new NamespacedKey(getId(), "elf");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.FISHING_ROD)
                .name("&#b1e6b7&lElf Fishing")
                .lore(
                        "&fThe elves are everywhere. Catch",
                        "&fone with your rod and feed it a",
                        "&fcookie or two (they are quite fond",
                        "&fof chocolate chip ones) to recieve",
                        "&fthe goodies it carries. It might ",
                        "&ftake a few tries until you hook onto",
                        "&fone, however."
                )
                .enchantUnsafe(Enchantment.LURE, 3)
                .addItemFlags()
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        var player = event.getPlayer();
        var inventory = player.getInventory();

        if (matches(inventory.getItemInMainHand())) {
            if (handleReel(event)) inventory.getItemInMainHand().subtract(1);
        } else if (matches(player.getInventory().getItemInOffHand())) {
            if (handleReel(event)) inventory.getItemInOffHand().subtract(1);
        }
    }

    @EventHandler
    public void onFeed(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ZOMBIE) {
            return;
        }

        var data = event.getRightClicked().getPersistentDataContainer();
        if (!data.has(elfKey, PersistentDataType.BYTE)) {
            return;
        }

        if (event.getRightClicked().isDead()) {
            return;
        }

        var player = event.getPlayer();
        if (!hasCookie(player)) {
            return;
        }

        var elf = (LivingEntity) event.getRightClicked();
        elf.damage(100L);

        var loc = elf.getLocation();
        var firework = (Firework) loc.getWorld().spawnEntity(loc.add(0.5, 6, 0.5), EntityType.FIREWORK);
        var fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(2);
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.RED).trail(true).flicker(true).build());
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.LIME).trail(true).flicker(true).with(FireworkEffect.Type.BALL_LARGE).build());
        firework.setFireworkMeta(fireworkMeta);
        Bukkit.getScheduler().runTaskLater(Festivities.INSTANCE, firework::detonate, 10L);

        player.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        player.playSound(loc, Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
        player.sendMessage(MessageUtils.formatColors("&#b1e6b7&lELF: &fYou caught an elf! Received $500 and some gold blocks!", true));
        PlayerUtils.giveOrDropItem(player, new ItemStack(Material.GOLD_BLOCK, 4));
        Kerosene.getKerosene().getEconomy().depositPlayer(player, 500);
    }

    private boolean hasCookie(Player player) {
        var inventory = player.getInventory();
        if (inventory.getItemInMainHand().getType() == Material.COOKIE) {
            inventory.getItemInMainHand().subtract(1);
            return true;
        }

        if (inventory.getItemInOffHand().getType() == Material.COOKIE) {
            inventory.getItemInOffHand().subtract(1);
            return true;
        }

        return false;
    }

    private boolean handleReel(PlayerFishEvent event) {
        if (Math.random() > 0.5) {
            var hook = event.getHook();
            var zombie = (Zombie) hook.getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.ZOMBIE);
            zombie.setBaby();
            dressZombie(zombie);
            zombie.setCustomName(MessageUtils.formatColors("&#c2f0ca&lElf", true));
            zombie.setCustomNameVisible(true);
            zombie.getPersistentDataContainer().set(elfKey, PersistentDataType.BYTE, (byte) 1);
            hook.addPassenger(zombie);
            return true;
        }

        return false;
    }

    private void dressZombie(Zombie zombie) {
        var equipment = zombie.getEquipment();
        equipment.setHelmet(new SkullBuilder()
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZ" +
                        "Dg4YWRiMjIxYzZlYTRmNGU4YWRiYTc2ZWY4NGMyODExYTYwODQ0YjQzMTNmYzkyM2QxMzU5YmNlNTE3MTRjYSJ9fX0=")
                .build());

        equipment.setChestplate(new LeatherArmorBuilder(LeatherArmorBuilder.LeatherArmorType.CHESTPLATE)
                .color(Color.fromRGB(68, 145, 95))
                .build());

        equipment.setLeggings(new LeatherArmorBuilder(LeatherArmorBuilder.LeatherArmorType.LEGGINGS)
                .color(Color.fromRGB(78, 89, 82))
                .build());

        equipment.setBoots(new LeatherArmorBuilder(LeatherArmorBuilder.LeatherArmorType.BOOTS)
                .color(Color.fromRGB(78, 89, 82))
                .build());
    }
}
