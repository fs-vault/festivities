package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.item.SkullBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class HotCocoa extends ItemArchetype implements Listener {

    private final NamespacedKey hotCocoaKey;
    private final ItemStack hotCocoa;

    private final NamespacedKey cocoaPowderKey;
    private final ItemStack cocoaPowder;

    private final String cocoaPowerName = "&#633708&lCocoa Powder";

    public HotCocoa() {
        super("hot_cocoa_cup", "Hot Cocoa Cup");

        this.hotCocoaKey = new NamespacedKey(getId(), "hot_cocoa");
        this.hotCocoa = new SkullBuilder()
                .name("&#de4721&lHot &#593413&lCocoa")
                .lore(
                        "&f&lMakes you feel all",
                        "&f&lwarm and fuzzy inside",
                        "",
                        "&8(Christmas 2020)"
                )
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmIyOGFhMzAzYjRjYWU4NzdkN2M2ZDI3YWE4Mzc2MWEyNjkxZjlkNzhhM2Q5Mjg5NTMwY2E2YWJiNWZkZjRjIn19fQ==")
                .persistData(hotCocoaKey, PersistentDataType.BYTE, (byte)1)
                .build();

        this.cocoaPowderKey = new NamespacedKey(getId(), "cocoa_powder");
        this.cocoaPowder = ItemBuilder.of(Material.BROWN_DYE)
                .name(cocoaPowerName)
                .lore(
                        "&7Way to bitter to use on it's own.",
                        "",
                        "&8(Christmas 2020)"
                )
                .persistData(cocoaPowderKey, PersistentDataType.BYTE, (byte)1)
                .build();
    }

    @Override
    public @NotNull ItemStack getItem() {
        return new SkullBuilder()
                .name("&f&lEmpty Cup")
                .lore(
                        "&fMake some &#593413&lHot Cocoa&r&f!",
                        "&r",
                        "&7- &#fafafaMilk Bucket",
                        "&7- " + cocoaPowerName + " &7(x2)",
                        "&7- &#fafafaSugar &7(x2)",
                        "",
                        "&8(Christmas 2020)"
                )
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI4MDk4ODlkYTM2YWRmNTYxODU4MWJiZjdiNjZkMmQ4ODM5ZTJlYjcyNTRjMzMzMmU0ZjNhMjMwZmEifX19")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onDrinkHotCocoa(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.PLAYER_HEAD)
            return;

        if (!isHotCocoa(item))
            return;

        event.setCancelled(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 2));
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1.0f, 1.0f);

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(ItemBuilder.of(Material.AIR).build());
        }
    }

    @EventHandler
    public void onCocoaBeanGrind(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.COCOA_BEANS) {
            return;
        }

        if (!event.hasBlock() || event.getClickedBlock().getType() != Material.GRINDSTONE) {
            return;
        }

        event.setCancelled(true);
        player.getInventory().setItemInMainHand(item.add(-1));
        player.getInventory().addItem(this.cocoaPowder);

        checkAndCraft(player);
    }

    @EventHandler
    public void onCocoaBeanPickUp(PlayerAttemptPickupItemEvent event) {
        var item = event.getItem().getItemStack();

        if (item.getType() != Material.COCOA_BEANS)
            return;

        var meta = item.getItemMeta();
        meta.setLore(Arrays.asList(
                MessageUtils.formatColors("&7Can be grinded to " + cocoaPowerName, true),
                MessageUtils.formatColors("&7by right clicking on a grindstone.", false),
                MessageUtils.formatColors("&r", false),
                MessageUtils.formatColors("&8(Christmas 2020)", false)
        ));
        item.setItemMeta(meta);
    }

    @EventHandler
    public void onPickupCraftCheck(PlayerAttemptPickupItemEvent event) {
        if (event.isCancelled())
            return;

        var item = event.getItem().getItemStack();

        if (item.getType() != Material.SUGAR
                && item.getType() != Material.BROWN_DYE
                && item.getType() != Material.MILK_BUCKET
                && item.getType() != Material.PLAYER_HEAD) {
            return;
        }

        if (item.getType() == Material.PLAYER_HEAD && !isEmptyCup(item)) {
            return;
        }

        if (item.getType() == Material.BROWN_DYE && !isCocoaPowder(item)) {
            return;
        }

        checkAndCraftWithDelay(event.getPlayer());
    }

    @EventHandler
    public void onMilk(PlayerInteractEntityEvent event) {
        var entity = event.getRightClicked();
        if (entity.getType() != EntityType.COW) {
            return;
        }

        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.BUCKET) {
            return;
        }

        checkAndCraftWithDelay(player);
    }

    private void checkAndCraftWithDelay(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Festivities.INSTANCE, () -> checkAndCraft(player), 10);
    }

    private void checkAndCraft(Player player) {
        Inventory inventory = player.getInventory();

        if (!takeRequiredComponents(inventory)) {
            return;
        }

        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        inventory.addItem(this.hotCocoa);
        player.sendMessage(MessageUtils.formatColors("&#de4721&lHot &#593413&lCocoa: &#cd91ffYou made some Hot Cocoa. Enjoy!", true));
    }

    private boolean takeRequiredComponents(Inventory inventory) {
        if (!inventory.contains(Material.SUGAR, 2)
                || !inventory.contains(Material.MILK_BUCKET, 1)
                || !inventory.contains(Material.BROWN_DYE, 2)
                || !inventory.contains(Material.PLAYER_HEAD, 1)) {
            return false;
        }

        var brownDyes = inventory.all(Material.BROWN_DYE);
        List<ItemStack> cocoaPowder = new ArrayList<>();
        for (var brownDyeStack : brownDyes.values()) {
            if (isCocoaPowder(brownDyeStack)) {
                cocoaPowder.add(brownDyeStack);
            }
        }

        var totalCocoa = (Integer) cocoaPowder.stream().mapToInt(ItemStack::getAmount).sum();
        if (totalCocoa < 2) {
            return false;
        }

        var heads = inventory.all(Material.PLAYER_HEAD);
        ItemStack emptyCup = null;
        for (var headStack : heads.values()) {
            if (isEmptyCup(headStack)) {
                emptyCup = headStack;
            }
        }

        if (emptyCup == null) {
            return false;
        }

        removeFromInventory(inventory, Material.SUGAR, 2, null);
        removeFromInventory(inventory, Material.BROWN_DYE, 2, this::isCocoaPowder);
        var milkSlot = inventory.first(Material.MILK_BUCKET);
        inventory.setItem(milkSlot, ItemBuilder.of(Material.AIR).build());
        inventory.remove(emptyCup);
        return true;
    }

    private void removeFromInventory(Inventory inventory, Material material, int amount, Predicate<ItemStack> filter) {
        var cocoaPowderToRemove = amount;
        for (var brownDyeStack : inventory.all(material).values()) {
            if (filter != null && !filter.test(brownDyeStack)) continue;

            if (brownDyeStack.getAmount() > cocoaPowderToRemove) {
                brownDyeStack.setAmount(brownDyeStack.getAmount() - cocoaPowderToRemove);
                break;
            }

            if (brownDyeStack.getAmount() == cocoaPowderToRemove) {
                inventory.remove(brownDyeStack);
                break;
            }

            cocoaPowderToRemove = cocoaPowderToRemove - brownDyeStack.getAmount();
            inventory.remove(brownDyeStack);
        }
    }

    private boolean isEmptyCup(ItemStack stack) {
        return matches(stack);
    }

    private boolean isCocoaPowder(ItemStack stack) {
        return stack.getItemMeta()
                .getPersistentDataContainer()
                .has(cocoaPowderKey, PersistentDataType.BYTE);
    }

    private boolean isHotCocoa(ItemStack stack) {
        return stack.getItemMeta()
                .getPersistentDataContainer()
                .has(hotCocoaKey, PersistentDataType.BYTE);
    }
}
