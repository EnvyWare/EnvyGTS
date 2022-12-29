package com.envyful.gts.forge.impl.trade;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.gui.FilterType;
import com.envyful.gts.api.sql.EnvyGTSQueries;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.EnvyGTSConfig;
import com.envyful.gts.forge.event.PostTradePurchaseEvent;
import com.envyful.gts.forge.event.TradePurchaseEvent;
import com.envyful.gts.forge.impl.trade.type.ItemTrade;
import com.envyful.gts.forge.impl.trade.type.PokemonTrade;
import com.envyful.gts.forge.player.GTSAttribute;
import com.pixelmonmod.pixelmon.api.economy.BankAccount;
import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * Abstract implementation of the {@link Trade} interface covering the basics
 *
 */
public abstract class ForgeTrade implements Trade {

    protected final double cost;
    protected final long expiry;
    protected final String originalOwnerName;

    protected UUID owner;
    protected String ownerName;
    protected boolean removed;
    protected boolean purchased;

    protected ForgeTrade(UUID owner, String ownerName, double cost, long expiry, String originalOwnerName, boolean removed, boolean purchased) {
        this.owner = owner;
        this.ownerName = ownerName;
        this.cost = cost;
        this.expiry = expiry;
        this.originalOwnerName = originalOwnerName;
        this.removed = removed;
        this.purchased = purchased;
    }

    @Override
    public boolean isOwner(UUID uuid) {
        return Objects.equals(this.owner, uuid);
    }

    @Override
    public double getCost() {
        return this.cost;
    }

    @Override
    public boolean hasExpired() {
        return System.currentTimeMillis() >= this.expiry;
    }

    @Override
    public boolean wasPurchased() {
        return this.purchased;
    }

    @Override
    public boolean wasRemoved() {
        return this.removed;
    }

    @Override
    public boolean filter(EnvyPlayer<?> filterer, FilterType filterType) {
        return filterType.isAllowed(filterer, this);
    }

    @Override
    public boolean attemptPurchase(EnvyPlayer<?> player) {
        if (this.removed) {
            return false;
        }

        ServerPlayerEntity parent = (ServerPlayerEntity) player.getParent();
        BankAccount iPixelmonBankAccount = BankAccountProxy.getBankAccountUnsafe(parent);

        if (iPixelmonBankAccount.getBalance().doubleValue() < this.cost) {
            player.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getInsufficientFunds()
            ));
            return false;
        }

        if (MinecraftForge.EVENT_BUS.post(new TradePurchaseEvent((ForgeEnvyPlayer)player, this))) {
            return false;
        }

        iPixelmonBankAccount.take(this.cost);

        EnvyGTSConfig config = EnvyGTSForge.getConfig();
        BankAccount target = BankAccountProxy.getBankAccountUnsafe(this.owner);

        if (target == null) {
            return false;
        }

        target.add((this.cost * (config.isEnableTax() ? config.getTaxRate() : 1.0)));

        this.attemptSendMessage(this.owner, player.getName(), (this.cost * (1 - (config.isEnableTax() ?
                config.getTaxRate() : 1.0))));

        this.updateOwnership((EnvyPlayer<ServerPlayerEntity>) player, this.owner);
        this.purchased = true;
        this.setRemoved();
        this.collect(player, null);

        MinecraftForge.EVENT_BUS.post(new PostTradePurchaseEvent((ForgeEnvyPlayer) player, this));

        player.message(UtilChatColour.colour(EnvyGTSForge.getLocale().getMessages().getPurchasedTrade()));
        return true;
    }

    private void attemptSendMessage(UUID owner, String buyerName, double taxTaken) {
        ServerPlayerEntity target = UtilPlayer.getOnlinePlayer(owner);

        if (target == null) {
            return;
        }

        target.sendMessage(UtilChatColour.colour(
                EnvyGTSForge.getLocale().getMessages().getItemWasPurchased()
                .replace("%item%", this.getDisplayName())
                .replace("%buyer%", buyerName)
                .replace("%tax%", String.format("%.2f", taxTaken))
                .replace("%price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), this.getCost()))
        ), Util.NIL_UUID);
    }

    private void updateOwnership(EnvyPlayer<ServerPlayerEntity> purchaser, UUID oldOwner) {
        GTSAttribute purchaserAttribute = purchaser.getAttribute(EnvyGTSForge.class);

        purchaserAttribute.getOwnedTrades().add(this);

        EnvyPlayer<?> seller = EnvyGTSForge.getPlayerManager().getPlayer(oldOwner);

        if (seller == null) {
            return;
        }

        this.owner = purchaser.getUuid();
        this.ownerName = purchaser.getName();
        GTSAttribute sellerAttribute = seller.getAttribute(EnvyGTSForge.class);
        sellerAttribute.getOwnedTrades().remove(this);
    }

    protected void setRemoved() {
        this.removed = true;

        UtilConcurrency.runAsync(() -> {
            try (Connection connection = EnvyGTSForge.getDatabase().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(EnvyGTSQueries.UPDATE_REMOVED)) {
                preparedStatement.setInt(1, 1);
                preparedStatement.setInt(2, this.purchased ? 1 : 0);
                preparedStatement.setString(3, this.owner.toString());
                preparedStatement.setLong(4, this.expiry);
                preparedStatement.setDouble(5, this.cost);

                if (this instanceof ItemTrade) {
                    preparedStatement.setString(6, "i");
                } else {
                    preparedStatement.setString(6, "p");
                }

                preparedStatement.setString(7, "INSTANT_BUY");
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    protected void updateOwner(UUID newOwner, String newOwnerName) {
        UUID owner = this.owner;
        this.owner = newOwner;
        this.ownerName = newOwnerName;

        UtilConcurrency.runAsync(() -> {
            try (Connection connection = EnvyGTSForge.getDatabase().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(EnvyGTSQueries.UPDATE_OWNER)) {
                preparedStatement.setString(1, newOwner.toString());
                preparedStatement.setString(2, newOwnerName);
                preparedStatement.setString(3, owner.toString());
                preparedStatement.setLong(4, this.expiry);
                preparedStatement.setDouble(5, this.cost);

                if (this instanceof ItemTrade) {
                    preparedStatement.setString(6, "i");
                } else {
                    preparedStatement.setString(6, "p");
                }

                preparedStatement.setString(7, "INSTANT_BUY");
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        protected UUID owner = null;
        protected String ownerName = "";
        protected String originalOwnerName = "";
        protected double cost = -1;
        protected long expiry = -1;
        protected boolean removed = false;
        protected boolean purchased = false;

        protected Builder() {}

        public Builder owner(EnvyPlayer<?> player) {
            this.ownerName(player.getName());
            return this.owner(player.getUuid());
        }

        public Builder owner(UUID owner) {
            this.owner = owner;
            return this;
        }

        public Builder ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public Builder originalOwnerName(String originalOwnerName) {
            this.originalOwnerName = originalOwnerName;
            return this;
        }

        public Builder removed(boolean removed) {
            this.removed = removed;
            return this;
        }

        public Builder cost(double cost) {
            this.cost = cost;
            return this;
        }

        public Builder expiry(long expiry) {
            this.expiry = expiry;
            return this;
        }

        public Builder purchased(boolean purchased) {
            this.purchased = purchased;
            return this;
        }

        public Builder content(String type) {
            Builder builder = null;

            switch (type.toLowerCase()) {
                case "p":
                    builder = new PokemonTrade.Builder();
                    break;
                case "i":
                default:
                    builder = new ItemTrade.Builder();
                    break;
            }

            if (this.owner != null) {
                builder.owner(this.owner);
            }

            if (this.cost != -1) {
                builder.cost(this.cost);
            }

            if (this.expiry != -1) {
                builder.expiry(expiry);
            }

            builder.removed(this.removed);
            builder.ownerName(this.ownerName);
            builder.purchased(this.purchased);
            builder.originalOwnerName(this.originalOwnerName);

            return builder;
        }

        public Builder contents(String contents) {
            return this;
        }

        public Trade build() {
            return null;
        }
    }
}
