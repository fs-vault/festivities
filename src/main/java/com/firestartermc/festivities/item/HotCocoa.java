package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.item.SkullBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import com.firestartermc.kerosene.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public HotCocoa() {
        super("hot_cocoa_cup", "Hot Cocoa Cup");

        this.hotCocoaKey = new NamespacedKey(getId(), "hot_cocoa");
        this.hotCocoa = new SkullBuilder()
                .name("&#e6d0ba&lHOT COCOA")
                .lore(
                        "&fMakes you feel all warm and",
                        "&ffuzzy inside. Quite a nice drink.",
                        "&7&o(best before 12/25/20)"
                )
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmIyOGFhMzAzYjRjYWU4NzdkN2M2ZDI3YWE4Mzc2MWEyNjkxZjlkNzhhM2Q5Mjg5NTMwY2E2YWJiNWZkZjRjIn19fQ==")
                .persistData(hotCocoaKey, PersistentDataType.BYTE, (byte) 1)
                .build();

        this.cocoaPowderKey = new NamespacedKey(getId(), "cocoa_powder");
        this.cocoaPowder = ItemBuilder.of(Material.BROWN_DYE)
                .name("&#966d42&lCocoa Powder")
                .lore(
                        "&7Way too bitter to use on its own.",
                        "&7Needs some added milk and sugar."
                )
                .persistData(cocoaPowderKey, PersistentDataType.BYTE, (byte) 1)
                .build();
    }

    @Override
    public @NotNull ItemStack getItem() {
        return new SkullBuilder()
                .name("&f&lEmpty Cup")
                .lore(
                        "&fMake some Hot Cocoa! &fIngredients:",
                        "&7- &#e6d0baMilk Bucket",
                        "&7- &#e6d0baCocoa Powder &7(x2)",
                        "&7- &#e6d0baSugar &7(x2)"
                )
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI4MDk4ODlkYTM2YWRmNTYxODU4MWJiZjdiNjZkMmQ4ODM5ZTJlYjcyNTRjMzMzMmU0ZjNhMjMwZmEifX19")
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .build();
    }

    @EventHandler
    public void onPlaceCup(PlayerInteractEvent event) {
        var player = event.getPlayer();
        if (matches(player.getInventory().getItemInMainHand()) || matches(player.getInventory().getItemInOffHand())) {
            event.setCancelled(true);
        }
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
        item.subtract(1);

        for (int i = 0; i < 3; i++) {
            final int sip = i;
            Bukkit.getScheduler().runTaskLater(Festivities.INSTANCE, () -> {
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1.0f, 1.0f);
                if (sip == 2) {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 3));
                    player.setFoodLevel(Math.min(player.getFoodLevel() + 8, 20));
                    player.setSaturation(Math.min(player.getSaturation() + 8, 20));
                    PlayerUtils.giveOrDropItem(player, getItem());
                }
            }, 4L * sip);
        }
    }

    @EventHandler
    public void onCocoaBeanGrind(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.COCOA_BEANS) {
            return;
        }

        if (!hasEmptyCup(player)) {
            return;
        }

        if (!event.hasBlock() || event.getClickedBlock().getType() != Material.GRINDSTONE) {
            return;
        }

        event.setCancelled(true);
        player.getInventory().setItemInMainHand(item.subtract(1));
        player.playSound(event.getClickedBlock().getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.5f, 1.0f);
        PlayerUtils.giveOrDropItem(player, cocoaPowder);

        checkAndCraft(player);
    }

    @EventHandler
    public void onCocoaBeanPickUp(PlayerAttemptPickupItemEvent event) {
        var item = event.getItem().getItemStack();

        if (item.getType() != Material.COCOA_BEANS)
            return;

        if (!hasEmptyCup(event.getPlayer())) {
            return;
        }

        var meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.formatColors("&#966d42&lCococa Beans", true));
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Can be ground down to cocoa",
                ChatColor.GRAY + "powder using a grindstone."
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
        PlayerUtils.giveOrDropItem(player, hotCocoa);
        player.sendMessage(MessageUtils.formatColors("&#e6d0ba&lHOT COCOA: &fYou made some hot cocoa! Enjoy :>", true));
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

    private boolean hasEmptyCup(Player player) {
        for (var item : player.getInventory().getContents()) {
            if (isEmptyCup(item)) {
                return true;
            }
        }

        return false;
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
