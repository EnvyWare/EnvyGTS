package com.envyful.reforged.gts.forge.impl.trade;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.gui.FilterType;
import com.envyful.reforged.gts.api.sql.ReforgedGTSQueries;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.config.ReforgedGTSConfig;
import com.envyful.reforged.gts.forge.impl.trade.type.ItemTrade;
import com.envyful.reforged.gts.forge.impl.trade.type.PokemonTrade;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;

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

    protected UUID owner;
    protected String ownerName;
    protected boolean removed;
    protected boolean purchased;

    protected ForgeTrade(UUID owner, String ownerName, double cost, long expiry, boolean removed, boolean purchased) {
        this.owner = owner;
        this.ownerName = ownerName;
        this.cost = cost;
        this.expiry = expiry;
        this.removed = removed;
        this.purchased = purchased;
    }

    @Override
    public boolean isOwner(UUID uuid) {
        return Objects.equals(this.owner, uuid);
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
    public boolean filter(EnvyPlayer<?> filterer, FilterType filterType) {
        return filterType.isAllowed(filterer, this);
    }

    @Override
    public boolean attemptPurchase(EnvyPlayer<?> player) {
        if (this.removed) {
            return false;
        }

        EntityPlayerMP parent = (EntityPlayerMP) player.getParent();
        PlayerPartyStorage party = UtilPixelmonPlayer.getParty(parent);

        if (party.getMoney() < this.cost) {
            player.message(UtilChatColour.translateColourCodes(
                    '&',
                    ReforgedGTSForge.getInstance().getLocale().getMessages().getInsufficientFunds()
            ));
            return false;
        }

        party.setMoney((int) (party.getMoney() - this.cost));

        ReforgedGTSConfig config = ReforgedGTSForge.getInstance().getConfig();
        PlayerPartyStorage target = Pixelmon.storageManager.getParty(this.owner);

        target.changeMoney((int) ((target.getMoney() + this.cost) * (config.isEnableTax() ? config.getTaxRate() : 1.0)));

        this.updateOwner(player.getUuid(), player.getName());
        this.purchased = true;
        this.setRemoved();
        parent.closeScreen();
        player.message(UtilChatColour.translateColourCodes(
                '&',
                ReforgedGTSForge.getInstance().getLocale().getMessages().getPurchasedTrade()
        ));
        return true;
    }

    protected void setRemoved() {
        this.removed = true;

        UtilConcurrency.runAsync(() -> {
            try (Connection connection = ReforgedGTSForge.getInstance().getDatabase().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(ReforgedGTSQueries.UPDATE_REMOVED)) {
                preparedStatement.setInt(1, 1);
                preparedStatement.setInt(2, this.purchased ? 1 : 0);
                preparedStatement.setString(3, this.owner.toString());
                preparedStatement.setLong(4, this.expiry);
                preparedStatement.setDouble(5, this.cost);

                if (this instanceof ItemTrade) {
                    preparedStatement.setString(5, "i");
                } else {
                    preparedStatement.setString(5, "p");
                }

                preparedStatement.setString(6, "INSTANT_BUY");
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
            try (Connection connection = ReforgedGTSForge.getInstance().getDatabase().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(ReforgedGTSQueries.UPDATE_OWNER)) {
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
