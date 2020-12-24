package com.firestartermc.festivities.item.cookies.toytool;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.festivities.item.cookies.BakedCookie;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.item.LeatherArmorBuilder;
import com.firestartermc.kerosene.item.SkullBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class ChunkOfPlastic extends ItemArchetype implements Listener {

    private final NamespacedKey elfKey;

    public ChunkOfPlastic() {
        super("chunk_of_plastic", "Chunk of Plastic");
        this.elfKey = new NamespacedKey(getId(), "elf");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.PAPER)
                .name("&#3d98d4&lChunk of &#f13f3f&lPlastic")
                .lore(
                        "&fA pretty malleable material.",
                        "&fCan easily be shaped into other",
                        "&fmore useful things..."
                )
                .modelData(1)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    private boolean spawn = true;

    @EventHandler
    public void onZombieSpawn(EntitySpawnEvent event) {
        if (true) return; // Elf spawning disabled as of 12/23/20
        if (event.getEntityType() != EntityType.ZOMBIE) {
            return;
        }

        if (event.getEntity().fromMobSpawner()) {
            return;
        }

        if (!spawn) {
            spawn = true;
            return;
        }

        spawn = false;

        if (ThreadLocalRandom.current().nextInt(6) == 0) {
            event.setCancelled(true);
            var location = event.getLocation();
            var zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            zombie.getPersistentDataContainer().set(elfKey, PersistentDataType.BYTE, (byte) 1);
            dressZombie(zombie);
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
        var location = elf.getLocation();
        var world = location.getWorld();

        world.playSound(location, Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.3f);
        world.playSound(location, Sound.ENTITY_PLAYER_BURP, 1.0f, 1.3f);
        world.dropItemNaturally(location, getItem());
        world.spawnParticle(Particle.SPELL_WITCH, location, 2);
        elf.remove();
    }

    private boolean hasCookie(Player player) {
        var bakedCookie = new BakedCookie();
        var inventory = player.getInventory();

        if (bakedCookie.matches(inventory.getItemInMainHand())) {
            inventory.getItemInMainHand().subtract(1);
            return true;
        }

        if (bakedCookie.matches(inventory.getItemInOffHand())) {
            inventory.getItemInOffHand().subtract(1);
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
                .color(Color.fromRGB(128, 199, 31))
                .build());

        equipment.setLeggings(new LeatherArmorBuilder(LeatherArmorBuilder.LeatherArmorType.LEGGINGS)
                .color(Color.fromRGB(94, 124, 22))
                .build());

        equipment.setBoots(new LeatherArmorBuilder(LeatherArmorBuilder.LeatherArmorType.BOOTS)
                .color(Color.fromRGB(128, 199, 31))
                .build());
    }
}
