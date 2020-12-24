package com.firestartermc.festivities.item;

import com.firestartermc.festivities.Festivities;
import com.firestartermc.festivities.api.ItemArchetype;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class Stocking extends ItemArchetype implements Listener {

    private final NamespacedKey RECIPE_KEY = new NamespacedKey(Festivities.INSTANCE, "stocking");

    public Stocking() {
        super("stocking", "Stocking");
    }

    @Override
    public void register(Festivities festivities) {
        /*var recipe = new ShapedRecipe(RECIPE_KEY, getItem())
                .shape("***", "xyx", "xxx")
                .setIngredient('*', Material.WHITE_WOOL)
                .setIngredient('x', Material.RED_WOOL)
                .setIngredient('y', Material.CHEST);
        Bukkit.getServer().addRecipe(recipe);*/
    }

    @Override
    public void unregister(Festivities festivities) {
        Bukkit.getServer().removeRecipe(RECIPE_KEY);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return ItemBuilder.of(Material.PAPER)
                .name("&#ff5c5c&lStocking")
                .lore(
                        "&fA great way to give gifts",
                        "&fFill it up and give it to",
                        "&fanother player :>",
                        "&r ",
                        getCapacity(0)
                )
                .persistData(TYPE_KEY, PersistentDataType.STRING, getId())
                .modelData(2)
                .build();
    }

    private String getCapacity(int capacity) {
        return MessageUtils.formatColors("&7Capacity: " + capacity + "/64", false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!matches(event.getItem())) {
            return;
        }

        var result = dropContents(event.getItem(), event.getPlayer().getLocation());
        event.getPlayer().getInventory().setItem(event.getHand(), getUpdatedInfoItem(result));
        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.5f);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        var stocking = player.getInventory().getItemInMainHand();

        if (!matches(stocking)) { // TODO off-hand support
            return;
        }

        if (stocking.getAmount() > 1) {
            player.sendMessage(ChatColor.RED + "You can only pick up items with one stocking at a time.");
            return;
        }

        var item = event.getItem().getItemStack();
        var result = addItem(stocking, event.getItem().getItemStack());
        item.subtract(result.getSecond());

        if (item.getAmount() < 1) {
            event.setCancelled(true);
            event.getItem().remove();
        } else {
            event.getItem().setItemStack(item);
        }

        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.5f);
        player.getInventory().setItemInMainHand(getUpdatedInfoItem(result.getFirst()));
    }

    private ItemStack getUpdatedInfoItem(ItemStack stack) {
        var capacity = getStoredItemCount(stack);
        return ItemBuilder.of(stack)
                .removeLoreLine(5)
                .addLore(getCapacity(capacity))
                .modelData(capacity == 64 ? 3 : 2)
                .build();
    }

    private Pair<ItemStack, Integer> addItem(ItemStack stockingStack, ItemStack itemStack) {
        var stack = CraftItemStack.asNMSCopy(stockingStack);
        var stack1 = CraftItemStack.asNMSCopy(itemStack);

        if (stack1.isEmpty() || matches(itemStack) || Tag.SHULKER_BOXES.isTagged(itemStack.getType())) {
            return new Pair<>(stockingStack, 0);
        }

        var tag = stack.getOrCreateTag();

        if (!tag.hasKey("Items")) {
            tag.set("Items", new NBTTagList());
        }

        int contentWeight = getStoredItemCount(stockingStack);
        int newItemWeight = getWeight(itemStack);
        int space = Math.min(itemStack.getAmount(), (64 - contentWeight) / newItemWeight);

        if (space == 0) {
            return new Pair<>(stockingStack, 0);
        }

        var list = tag.getList("Items", 10);
        var optional = getMatchingItem(stack1, list);

        if (optional.isPresent()) {
            var tag1 = optional.get();
            var matchingStack = net.minecraft.server.v1_16_R3.ItemStack.a(tag1);

            matchingStack.add(space);
            matchingStack.save(tag1);
            list.remove(tag1);
            list.add(0, tag1);
        } else {
            var copy = stack1.cloneItemStack();
            copy.setCount(space);

            var tag1 = new NBTTagCompound();
            copy.save(tag1);
            list.add(0, tag1);
        }

        return new Pair<>(CraftItemStack.asBukkitCopy(stack), space);
    }

    private ItemStack dropContents(ItemStack stocking, Location location) {
        var stack = CraftItemStack.asNMSCopy(stocking);
        var tag = stack.getOrCreateTag();

        if (!tag.hasKey("Items")) {
            return stocking;
        }

        var list = tag.getList("Items", 10);
        for (int i = 0; i < list.size(); ++i) {
            var tag1 = list.getCompound(i);
            var item = location.getWorld().dropItemNaturally(location, CraftItemStack.asCraftMirror(net.minecraft.server.v1_16_R3.ItemStack.a(tag1)));
            item.setPickupDelay(40);
        }

        stack.removeTag("Items");
        return CraftItemStack.asBukkitCopy(stack);
    }

    private Optional<NBTTagCompound> getMatchingItem(net.minecraft.server.v1_16_R3.ItemStack itemStack, NBTTagList list) {
        return list.stream()
                .filter(NBTTagCompound.class::isInstance)
                .map(NBTTagCompound.class::cast)
                .filter(tag -> {
                    var stack = net.minecraft.server.v1_16_R3.ItemStack.a(tag);
                    return stack.doMaterialsMatch(itemStack) && net.minecraft.server.v1_16_R3.ItemStack.equals(stack, itemStack);
                })
                .findFirst();
    }

    private int getStoredItemCount(ItemStack itemStack) {
        return getContents(itemStack)
                .mapToInt(stack -> getWeight(stack) * stack.getAmount())
                .sum();
    }

    private int getWeight(ItemStack stack) {
        return 64 / stack.getMaxStackSize();
    }

    /**
     * Returns the items stored in this stocking.
     * This is stored as raw NBT and converted into
     * a stream from NMS -> Bukkit types.
     */
    private Stream<ItemStack> getContents(ItemStack itemStack) {
        var stack = CraftItemStack.asNMSCopy(itemStack);
        var tag = stack.getTag();

        if (tag == null) {
            return Stream.empty();
        } else {
            var list = tag.getList("Items", 10);
            return list.stream()
                    .map(NBTTagCompound.class::cast)
                    .map(net.minecraft.server.v1_16_R3.ItemStack::a)
                    .map(CraftItemStack::asBukkitCopy);
        }
    }

    public static class Pair<T, U> {

        private final T first;
        private final U second;

        public Pair(T f, U s) {
            this.first = f;
            this.second = s;
        }

        public T getFirst() {
            return first;
        }

        public U getSecond() {
            return second;
        }
    }
}
