package com.envyful.reforged.gts.forge.impl.trade.type;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.gui.FilterType;
import com.envyful.reforged.gts.api.gui.SortType;
import com.envyful.reforged.gts.forge.impl.trade.ForgeTrade;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
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

    public static class Builder extends ForgeTrade.Builder {

        private ItemStack itemStack = null;

        public Builder() {}

        @Override
        public Builder owner(EnvyPlayer<?> player) {
            return (Builder) super.owner(player);
        }

        @Override
        public Builder owner(UUID owner) {
            return (Builder) super.owner(owner);
        }

        @Override
        public Builder cost(double cost) {
            return (Builder) super.cost(cost);
        }

        @Override
        public Builder expiry(long expiry) {
            return (Builder) super.expiry(expiry);
        }

        @Override
        public Builder type(FilterType type) {
            return (Builder) super.type(type);
        }

        @Override
        public Builder content(String type) {
            return (Builder) super.content(type);
        }

        public Builder contents(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public ItemTrade build() {
            if (this.itemStack == null) {
                return null;
            }

            return new ItemTrade(this.owner, this.cost, this.expiry, this.type, this.itemStack);
        }
    }
}
