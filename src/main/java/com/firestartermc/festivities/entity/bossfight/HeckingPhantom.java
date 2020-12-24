package com.firestartermc.festivities.entity.bossfight;

import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPhantom;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.IEntitySelector;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R3.PathfinderTargetCondition;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class HeckingPhantom extends EntityPhantom {

    public HeckingPhantom(Location location) {
        super(EntityTypes.PHANTOM, ((CraftWorld) location.getWorld()).getHandle());
        setPosition(location.getX(), location.getY(), location.getZ());
        setHeadRotation(location.getPitch());
        world.addEntity(this);
    }

    /**
     * This snowman is hostile towards players only,
     * considering it's a boss.
     */
    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.targetSelector.a(1, new AttackPlayer());
    }

    private class AttackPlayer extends PathfinderGoal {

        private final PathfinderTargetCondition b = (new PathfinderTargetCondition()).a(64.0D); // attack targeting
        private int c = 20; // next scan tick

        public boolean a() { // canUse()
            if (this.c > 0) {
                --this.c;
                return false;
            } else {
                this.c = 60;
                List<EntityHuman> list = HeckingPhantom.this.world.a(this.b, HeckingPhantom.this, HeckingPhantom.this.getBoundingBox().grow(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty()) {
                    list.sort(Comparator.comparing(Entity::locY).reversed());
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityHuman entityhuman = (EntityHuman) iterator.next();
                        if (true || HeckingPhantom.this.a(entityhuman, PathfinderTargetCondition.a)) {
                            HeckingPhantom.this.setGoalTarget(entityhuman, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        public boolean b() {
            EntityLiving entityliving = HeckingPhantom.this.getGoalTarget();
            return entityliving != null ? HeckingPhantom.this.a(entityliving, PathfinderTargetCondition.a) : false;
        }
    }
}
