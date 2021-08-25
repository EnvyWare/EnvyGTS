package com.envyful.reforged.gts.forge.impl.trade.type;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.gui.FilterType;
import com.envyful.reforged.gts.api.gui.SortType;
import com.envyful.reforged.gts.forge.impl.trade.ForgeTrade;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ItemTrade extends ForgeTrade {

    private final ItemStack item;

    public ItemTrade(UUID owner, double cost, long expiry, FilterType type, ItemStack item) {
        super(owner, cost, expiry, type);

        this.item = item;
    }

    @Override
    public boolean attemptPurchase(EnvyPlayer<?> player) {
        return false; //TODO:
    }

    @Override
    public void collect(EnvyPlayer<?> player) {
        EntityPlayerMP parent = (EntityPlayerMP) player.getParent();

        if (!parent.inventory.addItemStackToInventory(this.item)) {
            //TODO: send message
            return;
        }

        //TODO: send message
    }

    @Override
    public void adminRemove(EnvyPlayer<?> admin) {
        EntityPlayerMP parent = (EntityPlayerMP) admin.getParent();

        if (!parent.inventory.addItemStackToInventory(this.item)) {
            //TODO: send message
            return;
        }

        //TODO: send message
    }

    @Override
    public int compare(Trade other, SortType type) {
        return 0;
    }

    @Override
    public void delete() {
        //TODO:
    }
}
