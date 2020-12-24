package com.firestartermc.festivities.item;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.festivities.entity.LethalSnowMan;
import com.firestartermc.kerosene.Kerosene;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntitySnowman;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityZombie;
import net.minecraft.server.v1_16_R3.IMonster;
import net.minecraft.server.v1_16_R3.MobEffect;
import net.minecraft.server.v1_16_R3.MobEffects;
import net.minecraft.server.v1_16_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R3.PathfinderGoalZombieAttack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftSnowman;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class WorldDomination extends ItemArchetype implements Listener {

    private boolean spawn = true;

    public WorldDomination() {
        super("world_domination", "World Domination");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.STICK)
                .name("&#bffff5&lWorld Domination")
                .lore(
                        "&fThe snowmen have become sentient and",
                        "&fare planning to take over the world.",
                        "&fAs a result, they've evolved to throw",
                        "&fharming potions instead of snowballs.",
                        "&fBuild a snowman and kill it so they",
                        "&ffeel your wrath. Be careful."
                )
                .enchantUnsafe(Enchantment.DAMAGE_ALL, 1)
                .addItemFlags()
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.SNOWMAN) {
            return;
        }

        var killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        if (matches(killer.getInventory().getItemInMainHand())) {
            killer.getInventory().getItemInMainHand().subtract(1);
            award(killer);
        } else if (matches(killer.getInventory().getItemInOffHand())) {
            killer.getInventory().getItemInOffHand().subtract(1);
            award(killer);
        }
    }

    private void award(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        Kerosene.getKerosene().getEconomy().depositPlayer(player, 500);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "inferno addtokens " + player.getName() + " 10");
        player.sendMessage(MessageUtils.formatColors("&#d1daff&lWORLD DOMINATION: &fYou've received 10 vote tokens and $500!", true));
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (true) return; // Disabled as of 12/23/20
        if (event.getEntityType() != EntityType.SNOWMAN) {
            return;
        }

        if (!spawn) {
            spawn = true;
            return;
        }

        spawn = false;

        ((Snowman) event.getEntity()).damage(50);
        var loc = event.getLocation();
        var world = ((CraftWorld) event.getLocation().getWorld()).getHandle();
        var snowman = new LethalSnowMan(EntityTypes.SNOW_GOLEM, world);
        snowman.setAbsorptionHearts(70);
        snowman.setPosition(loc.getX(), loc.getY(), loc.getZ());
        snowman.setHeadRotation(loc.getPitch());
        world.addEntity(snowman);

        /*var snowman = (EntitySnowman) ((CraftSnowman) event.getEntity()).getHandle();
        snowman.goalSelector.addGoal(1, new PathfinderGoalMeleeAttack(snowman, 1.0D, false));
        snowman.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(snowman, EntityPlayer.class, 10, true, false, (entityliving) -> {
            return entityliving instanceof EntityPlayer;
        }));*/
    }
}
