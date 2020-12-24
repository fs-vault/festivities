package com.firestartermc.festivities.entity.bossfight;

import com.firestartermc.festivities.item.snowballs.SlimeSnowball;
import com.firestartermc.festivities.item.snowballs.SplotchedSnowball;
import com.firestartermc.festivities.item.snowballs.WarCrimeSnowball;
import com.firestartermc.kerosene.item.PotionBuilder;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityPotion;
import net.minecraft.server.v1_16_R3.EntitySnowball;
import net.minecraft.server.v1_16_R3.EntitySnowman;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R3.SoundEffects;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;

public class SnowmanBoss extends EntitySnowman {

    private static final ItemStack HARMING_POT = CraftItemStack.asNMSCopy(new PotionBuilder().base(PotionType.INSTANT_DAMAGE).splash().build());

    public SnowmanBoss(Location location) {
        super(EntityTypes.SNOW_GOLEM, ((CraftWorld) location.getWorld()).getHandle());
        setPosition(location.getX(), location.getY(), location.getZ());
        setHeadRotation(location.getPitch());
        setHasPumpkin(false);
        world.addEntity(this);
    }

    /**
     * Shoots a random snowball from the list above at
     * the closest targeted entity.
     */
    @Override
    public void a(EntityLiving entity, float f) {
        var potion = new EntityPotion(this.world, this);
        potion.setItem(HARMING_POT);

        double d0 = entity.getHeadY() - 1.100000023841858D;
        double d1 = entity.locX() - this.locX();
        double d2 = d0 - potion.locY();
        double d3 = entity.locZ() - this.locZ();
        float f1 = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;

        potion.shoot(d1, d2 + f1, d3, 1.6F, 12.0F);
        playSound(SoundEffects.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (SHARED_RANDOM.nextFloat() * 0.4F + 0.8F));
        world.addEntity(potion);
    }

    /**
     * This snowman is hostile towards players only,
     * considering it's a boss.
     */
    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false, entityLiving -> {
            return entityLiving instanceof EntityPlayer;
        }));
    }

    /**
     * Bulk of the bullshit. Controls special AI actions.
     */
    @Override
    public void movementTick() {
        super.movementTick();

        if (ticksLived % 100 == 0) {

        }
    }
}
