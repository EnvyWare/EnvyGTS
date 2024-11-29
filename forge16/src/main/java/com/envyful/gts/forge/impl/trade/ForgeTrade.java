package com.envyful.gts.forge.impl.trade;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.gui.FilterType;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.PostTradePurchaseEvent;
import com.envyful.gts.forge.event.TradePurchaseEvent;
import com.envyful.gts.forge.impl.trade.type.ItemTrade;
import com.envyful.gts.forge.impl.trade.type.PokemonTrade;
import com.envyful.gts.forge.player.GTSAttribute;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public long getExpiry() {
        return this.expiry;
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
        if (this.removed || this.purchased || this.hasExpired()) {
            return false;
        }

        var parent = (ServerPlayerEntity) player.getParent();
        var bankAccount = BankAccountProxy.getBankAccountUnsafe(parent);

        if (bankAccount.getBalance().doubleValue() < this.cost) {
            player.message(UtilChatColour.colour(EnvyGTSForge.getLocale().getMessages().getInsufficientFunds()));
            return false;
        }

        if (MinecraftForge.EVENT_BUS.post(new TradePurchaseEvent((ForgeEnvyPlayer)player, this))) {
            return false;
        }

        bankAccount.take(this.cost);

        var config = EnvyGTSForge.getConfig();
        var target = BankAccountProxy.getBankAccountUnsafe(this.owner);

        if (target == null) {
            return false;
        }

        target.add((this.cost * (config.isEnableTax() ? config.getTaxRate() : 1.0)));

        this.attemptSendMessage(this.owner, player.getName(), (this.cost * (1 - (config.isEnableTax() ?
                config.getTaxRate() : 1.0))));

        this.purchased = true;
        this.setRemoved().whenCompleteAsync((unused, throwable) -> {
            this.collect(player, null).thenApply(unused1 -> {
                this.updateOwnership(player, this.owner);

                MinecraftForge.EVENT_BUS.post(new PostTradePurchaseEvent((ForgeEnvyPlayer) player, this));

                player.message(EnvyGTSForge.getLocale().getMessages().getPurchasedTrade(), this);
                return null;
            });
        }, ServerLifecycleHooks.getCurrentServer());

        return true;
    }

    private void attemptSendMessage(UUID owner, String buyerName, double taxTaken) {
        var target = EnvyGTSForge.getPlayerManager().getPlayer(owner);

        if (target == null) {
            return;
        }

        target.message(EnvyGTSForge.getLocale().getMessages().getItemWasPurchased()
                .replace("%item%", this.getDisplayName())
                .replace("%buyer%", buyerName)
                .replace("%tax%", String.format("%.2f", taxTaken))
                .replace("%price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), this.getCost())));
    }

    private void updateOwnership(EnvyPlayer<?> purchaser, UUID oldOwner) {
        this.owner = purchaser.getUniqueId();
        this.ownerName = purchaser.getName();

        var seller = EnvyGTSForge.getPlayerManager().getPlayer(oldOwner);

        if (seller == null) {
            return;
        }

        var sellerAttribute = seller.getAttributeNow(GTSAttribute.class);
        sellerAttribute.getOwnedTrades().remove(this);
    }

    protected abstract CompletableFuture<Void> setRemoved();

    protected abstract void updateOwner(UUID newOwner, String newOwnerName);

    @Override
    public List<Placeholder> placeholders() {
        return Lists.newArrayList(
                Placeholder.simple("%seller%", this.originalOwnerName),
                        Placeholder.simple("%original_owner%", this.originalOwnerName),
                        Placeholder.simple("%buyer%", this.ownerName),
                        Placeholder.simple("%price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), this.cost)),
                Placeholder.simple("%expires_in%", UtilTimeFormat.getFormattedDuration(this.expiry - System.currentTimeMillis())),
                Placeholder.simple("%date%", String.valueOf(System.currentTimeMillis()))
        );
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
            return this.owner(player.getUniqueId());
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
