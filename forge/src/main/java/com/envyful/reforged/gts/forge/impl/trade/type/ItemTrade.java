package com.envyful.reforged.gts.forge.impl.trade.type;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.discord.DiscordWebHook;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.TradeData;
import com.envyful.reforged.gts.api.discord.DiscordEvent;
import com.envyful.reforged.gts.api.gui.SortType;
import com.envyful.reforged.gts.api.sql.ReforgedGTSQueries;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.event.TradeCollectEvent;
import com.envyful.reforged.gts.forge.impl.trade.ForgeTrade;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import com.envyful.reforged.gts.forge.ui.ViewTradesUI;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemTrade extends ForgeTrade {

    private final ItemStack item;
    private final TradeData tradeData;

    public ItemTrade(UUID owner, String ownerName, String originalOwnerName, double cost, long expiry, ItemStack item,
                     boolean removed,
                     boolean purchased) {
        super(owner, ownerName, cost, expiry, originalOwnerName, removed, purchased);

        this.item = item;
        this.tradeData = new TradeData(this.item.getDisplayName(), this.expiry);
    }

    @Override
    public String getDisplayName() {
        return this.item.getDisplayName();
    }

    @Override
    public void collect(EnvyPlayer<?> player) {
        EntityPlayerMP parent = (EntityPlayerMP) player.getParent();
        GTSAttribute attribute = player.getAttribute(ReforgedGTSForge.class);

        parent.closeScreen();

        if (!parent.inventory.addItemStackToInventory(this.item)) {
            player.message(UtilChatColour.translateColourCodes('&',
                                                               ReforgedGTSForge.getInstance().getLocale().getMessages().getInventoryFull()));
            return;
        }

        MinecraftForge.EVENT_BUS.post(new TradeCollectEvent((EnvyPlayer<EntityPlayerMP>) player, this));

        attribute.getOwnedTrades().remove(this);
        ReforgedGTSForge.getInstance().getTradeManager().removeTrade(this);
        UtilConcurrency.runAsync(this::delete);
    }

    @Override
    public void adminRemove(EnvyPlayer<?> admin) {
        EntityPlayerMP parent = (EntityPlayerMP) admin.getParent();

        parent.closeScreen();

        if (!parent.inventory.addItemStackToInventory(this.item)) {
            admin.message(UtilChatColour.translateColourCodes('&',
                                                               ReforgedGTSForge.getInstance().getLocale().getMessages().getInventoryFull()));
            return;
        }

        admin.message(UtilChatColour.translateColourCodes('&',
                                                          ReforgedGTSForge.getInstance().getLocale().getMessages().getAdminRemoveTrade()));
        ReforgedGTSForge.getInstance().getTradeManager().removeTrade(this);
        UtilConcurrency.runAsync(this::delete);
    }

    @Override
    public int compare(Trade other, SortType type) {
        return type.getComparator().compare(this.toData(), other.toData());
    }

    @Override
    public void display(int pos, Pane pane) {
        int posX = pos % 9;
        int posY = pos / 9;

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(new ItemBuilder(this.item)
                                   .addLore(this.formatLore(ReforgedGTSForge.getInstance().getLocale().getListingBelowDataLore()))
                                   .build())
                .clickHandler((envyPlayer, clickType) -> {
                    if (this.isOwner(envyPlayer) && Objects.equals(clickType,
                                                                   ReforgedGTSForge.getInstance().getConfig().getOwnerRemoveButton())) {
                        this.setRemoved();
                        envyPlayer.message(UtilChatColour.translateColourCodes(
                                '&',
                                ReforgedGTSForge.getInstance().getLocale().getMessages().getRemovedOwnTrade()
                        ));
                        ((EntityPlayerMP) envyPlayer.getParent()).closeScreen();
                        return;
                    }

                    if (this.isOwner(envyPlayer)) {
                        return;
                    }

                    if (!this.attemptPurchase(envyPlayer)) {
                        ViewTradesUI.openUI((EnvyPlayer<EntityPlayerMP>) envyPlayer);
                        return;
                    }
                })
                .build());
    }

    private String[] formatLore(List<String> lore) {
        List<String> newLore = Lists.newArrayList();

        for (String s : lore) {
            newLore.add(UtilChatColour.translateColourCodes('&', s
                    .replace("%cost%", this.cost + "")
                    .replace("%duration%", UtilTimeFormat.getFormattedDuration((this.expiry - System.currentTimeMillis())))
                    .replace("%owner%", this.ownerName)
                    .replace("%original_owner%", this.originalOwnerName)));
        }

        return newLore.toArray(new String[0]);
    }

    @Override
    public void displayClaimable(int pos, Pane pane) {
        int posX = pos % 9;
        int posY = pos / 9;

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(new ItemBuilder(this.item)
                                   .addLore(this.formatLore(ReforgedGTSForge.getInstance().getLocale().getListingBelowExpiredOrClaimableLore()))
                                   .build())
                .clickHandler((envyPlayer, clickType) -> this.collect(envyPlayer))
                .build());
    }

    @Override
    public void delete() {
        try (Connection connection = ReforgedGTSForge.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ReforgedGTSQueries.REMOVE_TRADE)) {
            preparedStatement.setString(1, this.owner.toString());
            preparedStatement.setLong(2, this.expiry);
            preparedStatement.setDouble(3, this.cost);
            preparedStatement.setString(4, "i");
            preparedStatement.setString(5, "INSTANT_BUY");

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try (Connection connection = ReforgedGTSForge.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ReforgedGTSQueries.ADD_TRADE)) {
            preparedStatement.setString(1, this.owner.toString());
            preparedStatement.setString(2, this.ownerName);
            preparedStatement.setString(3, this.originalOwnerName);
            preparedStatement.setLong(4, this.expiry);
            preparedStatement.setDouble(5, this.cost);
            preparedStatement.setInt(6, this.removed ? 1 : 0);
            preparedStatement.setString(7, "INSTANT_BUY");
            preparedStatement.setString(8, "i");
            preparedStatement.setString(9, this.getItemJson());
            preparedStatement.setInt(10, 0);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getItemJson() {
        NBTTagCompound tag = new NBTTagCompound();
        this.item.writeToNBT(tag);
        return tag.toString();
    }

    @Override
    public TradeData toData() {
        return this.tradeData;
    }

    @Override
    public DiscordWebHook getWebHook(DiscordEvent event) {
        return null;
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
        public Builder ownerName(String ownerName) {
            return (Builder) super.ownerName(ownerName);
        }

        @Override
        public Builder originalOwnerName(String originalOwnerName) {
            return (Builder) super.originalOwnerName(originalOwnerName);
        }

        @Override
        public Builder removed(boolean removed) {
            return (Builder) super.removed(removed);
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
            try {
                NBTTagCompound tagCompound = JsonToNBT.getTagFromJson(contents);
                return this.contents(new ItemStack(tagCompound));
            } catch (NBTException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder contents(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        @Override
        public Builder purchased(boolean purchased) {
            return (Builder) super.purchased(purchased);
        }

        @Override
        public ItemTrade build() {
            if (this.itemStack == null) {
                return null;
            }

            return new ItemTrade(this.owner, this.ownerName, this.originalOwnerName, this.cost, this.expiry,
                                 this.itemStack,
                                 this.removed,
                                 this.purchased
            );
        }
    }
}
