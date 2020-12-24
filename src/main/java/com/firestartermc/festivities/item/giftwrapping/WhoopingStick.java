package com.firestartermc.festivities.item.giftwrapping;

import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class WhoopingStick extends ItemArchetype implements Listener {

    public WhoopingStick() {
        super("whooping_stick", "Whooping Stick");
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.STICK)
                .name("&#ff5959&lSanta's Whooping Stick")
                .lore(
                        "&7&oWhat the hell does Santa use this for?!?!",
                        "&fA questionable stick that makes you go",
                        "&fnyooom. Also quite pointy. &#dedede*poke teehee*"
                )
                .enchantUnsafe(Enchantment.DAMAGE_ALL, 6)
                .enchantUnsafe(Enchantment.KNOCKBACK, 4)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        var item = ((Player) event.getDamager()).getInventory().getItemInMainHand();
        if (!matches(item)) {
            return;
        }

        var entity = event.getEntity();
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.5f);

        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addPotionEffect(PotionEffectType.LEVITATION.createEffect(3, 50));
        }
    }
}
