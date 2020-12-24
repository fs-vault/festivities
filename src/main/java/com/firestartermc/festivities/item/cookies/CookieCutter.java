package com.firestartermc.festivities.item.cookies;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CookieCutter extends ItemArchetype implements Listener {

    private final NamespacedKey treeShapeKey;
    private final NamespacedKey heartShapeKey;
    private final NamespacedKey gingerbreadShapeKey;

    public CookieCutter() {
        super("cookie_cutter", "Cookie Cutter");
        this.treeShapeKey = new NamespacedKey(Festivities.INSTANCE, getId() + "_tree");
        this.heartShapeKey = new NamespacedKey(Festivities.INSTANCE, getId() + "_heart");
        this.gingerbreadShapeKey = new NamespacedKey(Festivities.INSTANCE, getId() + "_gingerbread");
    }

    @Override
    public void register(Festivities festivities) {
        var treeRecipe = new ShapedRecipe(treeShapeKey, getItemWithType(1))
                .shape("***", "*x*", "***")
                .setIngredient('*', Material.IRON_NUGGET)
                .setIngredient('x', Material.SPRUCE_SAPLING);
        festivities.getServer().addRecipe(treeRecipe);

        var heartRecipe = new ShapedRecipe(heartShapeKey, getItemWithType(2))
                .shape("* *", "***", " * ")
                .setIngredient('*', Material.IRON_NUGGET);
        festivities.getServer().addRecipe(heartRecipe);

        var gingerbreadRecipe = new ShapedRecipe(gingerbreadShapeKey, getItemWithType(3))
                .shape("***", "*x*", "***")
                .setIngredient('*', Material.IRON_NUGGET)
                .setIngredient('x', Material.ARMOR_STAND);
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
        return ItemBuilder.of(Material.BOWL)
                .name("&#c2c2c2&lCookie Cutter")
                .lore(
                        "&fA reusable mold for your",
                        "&fcookie cutting needs.",
                        "&r ",
                        "&7Shape: " + getShapeName(type)
                )
                .modelData(type)
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    public static String getShapeName(int type) {
        var name = switch (type) {
            case 1 -> "&#a7ebb0Christmas Tree";
            case 2 -> "&#f58484Heart";
            case 3 -> "&#f7e1b2Gingerbread Man";
            default -> "&#c2c2c2Cookie Cutter";
        };

        return MessageUtils.formatColors(name, true);
    }
}
