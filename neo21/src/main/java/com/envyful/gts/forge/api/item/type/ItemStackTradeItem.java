package com.envyful.gts.forge.api.item.type;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.forge.api.item.TradeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ItemStackTradeItem implements TradeItem {

    private final ItemStack itemStack;

    public ItemStackTradeItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack display() {
        return this.itemStack.copy();
    }

    @Override
    public String displayName() {
        return this.itemStack.getDisplayName().getString();
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
