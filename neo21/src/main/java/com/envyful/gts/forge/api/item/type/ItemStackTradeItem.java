package com.envyful.gts.forge.api.item.type;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.forge.api.item.TradeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ItemStackTradeItem implements TradeItem {

    private final ItemStack itemStack;

    public ItemStackTradeItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public Displayable.Builder<ItemStack> display() {
        return GuiFactory.displayableBuilder(this.itemStack.copy());
    }

    @Override
    public String displayName() {
        return this.itemStack.getDisplayName().getString();
    }

    @Override
    public boolean collect(EnvyPlayer<?> player) {
        var forgePlayer = (ServerPlayer) player.getParent();

        if (forgePlayer.getInventory().getFreeSlot() == -1) {
            return false;
        }

        forgePlayer.getInventory().add(this.itemStack.copy());
        return true;
    }

    @Override
    public String serialize() {
        return this.itemStack.save(ServerLifecycleHooks.getCurrentServer().registryAccess(), new CompoundTag()).toString();
    }
}
