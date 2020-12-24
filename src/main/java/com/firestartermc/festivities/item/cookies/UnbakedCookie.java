package com.firestartermc.festivities.item.cookies;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class UnbakedCookie extends ItemArchetype implements Listener {

    private final NamespacedKey treeShapeKey;
    private final NamespacedKey heartShapeKey;
    private final NamespacedKey gingerbreadShapeKey;

    public UnbakedCookie() {
        super("unbaked_cookie", "Unbaked Cookie");
        this.treeShapeKey = new NamespacedKey(Festivities.INSTANCE, getId() + "_tree");
        this.heartShapeKey = new NamespacedKey(Festivities.INSTANCE, getId() + "_heart");
        this.gingerbreadShapeKey = new NamespacedKey(Festivities.INSTANCE, getId() + "_gingerbread");
    }

    @Override
    public void register(Festivities festivities) {
        var dough = new CookieDough().getItem();

        var treeRecipe = new ShapelessRecipe(treeShapeKey, getItemWithType(1))
                .addIngredient(1, new CookieCutter().getItemWithType(1))
                .addIngredient(1, dough);
        festivities.getServer().addRecipe(treeRecipe);

        var heartRecipe = new ShapelessRecipe(heartShapeKey, getItemWithType(2))
                .addIngredient(1, new CookieCutter().getItemWithType(2))
                .addIngredient(1, dough);
        festivities.getServer().addRecipe(heartRecipe);

        var gingerbreadRecipe = new ShapelessRecipe(gingerbreadShapeKey, getItemWithType(3))
                .addIngredient(1, new CookieCutter().getItemWithType(3))
                .addIngredient(1, dough);
        festivities.getServer().addRecipe(gingerbreadRecipe);
    }

    @Override
    public void unregister(Festivities festivities) {
        festivities.getServer().removeRecipe(treeShapeKey);
        festivities.getServer().removeRecipe(heartShapeKey);
        festivities.getServer().removeRecipe(gingerbreadShapeKey);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return getItemWithType(1);
    }

    @NotNull
    public ItemStack getItemWithType(int type) {
        return ItemBuilder.of(Material.COOKIE)
                .name("&#ffeccc&lUnbaked Cookie")
                .lore(
                        "&fNot ready to be eaten.",
                        "&fNeeds to be baked first.",
                        "&7Right-click on a furnace",
                        "&7to bake.",
                        "&r ",
                        "&7Shape: " + CookieCutter.getShapeName(type)
                )
                .modelData(type)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        if (!matches(event.getItem())) {
            return;
        }

        var player = event.getPlayer();
        player.addPotionEffect(PotionEffectType.SLOW.createEffect(60, 2));
    }

    @EventHandler
    public void onCook(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        var block = event.getClickedBlock();
        if (block == null || !(block.getType() == Material.FURNACE || block.getType() == Material.SMOKER)) {
            return;
        }

        if (!matches(event.getItem())) {
            return;
        }

        var modelData = event.getItem().getItemMeta().getCustomModelData();
        event.getItem().subtract(1);
        PlayerUtils.giveOrDropItem(event.getPlayer(), new BakedCookie().getItemWithType(modelData));
        event.getPlayer().playSound(block.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 1.0f, 1.0f);
        event.getPlayer().playSound(block.getLocation(), Sound.ITEM_HOE_TILL, 1.0f, 1.0f);
        event.getPlayer().playSound(block.getLocation(), Sound.BLOCK_WOOL_PLACE, 1.0f, 1.0f);
        event.setCancelled(true);
    }
}
