package com.envyful.gts.forge.api.item.type;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.gts.forge.api.item.TradeItem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Locale;

public class ItemStackTradeItem implements TradeItem {

    private final ItemStack itemStack;

    public ItemStackTradeItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStackTradeItem(String serializedData) throws CommandSyntaxException {
        this.itemStack = ItemStack.parse(ServerLifecycleHooks.getCurrentServer().registryAccess(), TagParser.parseTag(serializedData)).get();
    }

    @Override
    public String id() {
        return "item";
    }

    @Override
    public ItemStack display(Placeholder... placeholders) {
        return this.itemStack.copy();
    }

    @Override
    public String displayName() {
        return this.itemStack.getDisplayName().getString();
    }

    @Override
    public String searchKey() {
        var name = ChatFormatting.stripFormatting(this.itemStack.getHoverName().getString());
        var itemId = BuiltInRegistries.ITEM.getKey(this.itemStack.getItem()).toString();

        if (name == null || name.isBlank()) {
            return itemId.toLowerCase(Locale.ROOT);
        }

        return (name + " " + itemId).toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean collect(EnvyPlayer<?> player) {
        if (!player.hasInventorySpace(1)) {
            return false;
        }

        ((ForgeEnvyPlayer) player).getParent().getInventory().add(this.itemStack.copy());
        return true;
    }

    @Override
    public String serialize() {
        return this.itemStack.save(ServerLifecycleHooks.getCurrentServer().registryAccess(), new CompoundTag()).toString();
    }
}
