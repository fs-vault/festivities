package com.firestartermc.festivities.item.cookies.toytool;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ToyAxe extends ToyTool {

    private final NamespacedKey recipeKey;

    public ToyAxe() {
        super("toy_axe", "Toy Axe");
        this.recipeKey = new NamespacedKey(Festivities.INSTANCE, getId());
    }

    @Override
    public void register(Festivities festivities) {
        var recipe = new ShapedRecipe(recipeKey, getItem())
                .shape(" **", " x*", " x ")
                .setIngredient('*', new ChunkOfPlastic().getItem())
                .setIngredient('x', new PlasticShaft().getItem());
        festivities.getServer().addRecipe(recipe);
    }

    @Override
    public void unregister(Festivities festivities) {
        festivities.getServer().removeRecipe(recipeKey);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.DIAMOND_AXE)
                .name("&#3d98d4&lToy &#f13f3f&lAxe")
                .lore("&f*squeak*")
                .enchantUnsafe(Enchantment.DIG_SPEED, 6)
                .enchantUnsafe(Enchantment.DAMAGE_ALL, 6)
                .modelData(1)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        var item = ((Player) event.getDamager()).getInventory().getItemInMainHand();
        if (!matches(item)) {
            return;
        }

        var entity = event.getEntity();
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1.5f);

        if (entity instanceof LivingEntity) {
            var living = (LivingEntity) entity;
            living.addPotionEffect(PotionEffectType.LEVITATION.createEffect(1, 30));
            living.addPotionEffect(PotionEffectType.CONFUSION.createEffect(90, 0));
        }

        squeak((Player) event.getDamager());
    }
}
