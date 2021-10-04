package com.envyful.reforged.gts.forge.impl.trade.type;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.json.UtilGson;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.gui.SortType;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.impl.trade.ForgeTrade;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.UUID;

public class ItemTrade extends ForgeTrade {

    private final ItemStack item;

    public ItemTrade(UUID owner, double cost, long expiry, ItemStack item) {
        super(owner, cost, expiry);

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
    public void display(int pos, Pane pane) {
        int posX = pos % 9;
        int posY = pos / 9;

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(new ItemBuilder(this.item)
                                   .addLore(this.formatLore(ReforgedGTSForge.getInstance().getLocale().getListingBelowDataLore()))
                                   .build())
                .clickHandler((envyPlayer, clickType) -> {}) //TODO: confirm UI
                .build());
    }

    private String[] formatLore(List<String> lore) {
        List<String> newLore = Lists.newArrayList();

        for (String s : lore) {
            newLore.add(UtilChatColour.translateColourCodes('&', s
                    .replace("%cost%", this.cost + "")
                    .replace("%duration%", UtilTimeFormat.getFormattedDuration((this.expiry - System.currentTimeMillis())))));
        }

        return newLore.toArray(new String[0]);
    }

    @Override
    public void delete() {
        //TODO:
    }

    @Override
    public void save() {
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
        public Builder content(String type) {
            return (Builder) super.content(type);
        }

        @Override
        public Builder contents(String contents) {
            return this.contents(UtilGson.GSON.fromJson(contents, ItemStack.class));
        }

        public Builder contents(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        @Override
        public ItemTrade build() {
            if (this.itemStack == null) {
                return null;
            }

            return new ItemTrade(this.owner, this.cost, this.expiry, this.itemStack);
        }
    }
}
