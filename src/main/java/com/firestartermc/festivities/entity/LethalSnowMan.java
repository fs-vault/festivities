package com.firestartermc.festivities.entity;

import com.firestartermc.kerosene.item.PotionBuilder;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityPotion;
import net.minecraft.server.v1_16_R3.EntitySnowman;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R3.SoundEffects;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.potion.PotionType;

public class LethalSnowMan extends EntitySnowman {

    public LethalSnowMan(EntityTypes<? extends EntitySnowman> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        var potion = new EntityPotion(this.world, this);
        potion.setItem(CraftItemStack.asNMSCopy(new PotionBuilder().base(PotionType.INSTANT_DAMAGE).splash().build()));
        double d0 = entityliving.getHeadY() - 1.100000023841858D;
        double d1 = entityliving.locX() - this.locX();
        double d2 = d0 - potion.locY();
        double d3 = entityliving.locZ() - this.locZ();
        float f1 = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        potion.shoot(d1, d2 + (double)f1, d3, 1.6F, 12.0F);
        this.playSound(SoundEffects.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(potion);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityPlayer.class, 10, true, false, (entityliving) -> {
            return entityliving instanceof EntityPlayer;
        }));
    }
}
