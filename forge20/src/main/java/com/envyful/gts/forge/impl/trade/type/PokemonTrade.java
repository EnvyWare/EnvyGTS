package com.envyful.gts.forge.impl.trade.type;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.gui.type.ConfirmationUI;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.sqlite.config.SQLiteDatabaseDetailsConfig;
import com.envyful.api.text.Placeholder;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.PlaceholderCollectEvent;
import com.envyful.gts.forge.event.TradeCollectEvent;
import com.envyful.gts.forge.event.TradeRemoveEvent;
import com.envyful.gts.forge.impl.trade.ForgeTrade;
import com.envyful.gts.forge.impl.trade.type.sql.SQLPokemonTrade;
import com.envyful.gts.forge.impl.trade.type.sqlite.SQLitePokemonTrade;
import com.envyful.gts.forge.player.GTSAttribute;
import com.envyful.gts.forge.ui.ViewTradesUI;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 *
 * Represents a pokemon {@link Trade} in the GTS
 *
 */
public abstract class PokemonTrade extends ForgeTrade {

    private final Pokemon pokemon;

    public PokemonTrade(UUID owner, String ownerName, String originalOwnerName, double cost, long expiry,
                        Pokemon pokemon, boolean removed,
                        boolean purchased) {
        super(owner, ownerName, cost, expiry, originalOwnerName, removed, purchased);

        this.pokemon = pokemon;
    }

    public Pokemon getPokemon() {
        return this.pokemon;
    }

    @Override
    public String getDisplayName() {
        return this.pokemon.getLocalizedName();
    }

    @Override
    public CompletableFuture<Void> collect(EnvyPlayer<?> player, Consumer<EnvyPlayer<?>> returnGui) {
        MinecraftForge.EVENT_BUS.post(new TradeCollectEvent((ForgeEnvyPlayer) player, this));

        StorageProxy.getPartyNow((ServerPlayer) player.getParent()).add(this.pokemon);
        EnvyGTSForge.getTradeManager().removeTrade(this);

        if (returnGui == null) {
            player.closeInventory();
        } else {
            returnGui.accept(player);
        }

        return CompletableFuture.runAsync(this::delete, UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public void adminRemove(EnvyPlayer<?> admin) {
        admin.closeInventory();

        var owner = EnvyGTSForge.getPlayerManager().getPlayer(this.owner);

        if (owner != null) {
            GTSAttribute attribute = owner.getAttributeNow(GTSAttribute.class);
            attribute.getOwnedTrades().remove(this);
        }

        StorageProxy.getPartyNow((ServerPlayer) admin.getParent()).add(this.pokemon);
        EnvyGTSForge.getTradeManager().removeTrade(this);
        UtilConcurrency.runAsync(this::delete);
    }

    @Override
    public Displayable display() {
        var placeholderEvent = new PlaceholderCollectEvent(this, Placeholder.multiLine("%below_lore_data%", EnvyGTSForge.getLocale().getListingBelowDataLore()), this);

        MinecraftForge.EVENT_BUS.post(placeholderEvent);

        return GuiFactory.displayableBuilder(ItemStack.class)
                .singleClick()
                .itemStack(UtilSprite.getPokemonElement(pokemon, EnvyGTSForge.getGui().getSearchUIConfig().getSpriteConfig(),placeholderEvent.getPlaceholders().toArray(new Placeholder[0])))
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> {
                    if (this.removed || this.wasPurchased() || this.hasExpired()) {
                        ((ForgeEnvyPlayer) envyPlayer).getParent().closeContainer();
                        return;
                    }

                    if (envyPlayer.hasPermission("envygts.admin") && Objects.equals(
                            clickType,
                            EnvyGTSForge.getConfig().getOwnerRemoveButton()
                    ) && ((ServerPlayer) envyPlayer.getParent()).isCreative()) {
                        this.removed = true;
                        this.adminRemove(envyPlayer);
                        return;
                    }

                    if (this.isOwner(envyPlayer) && Objects.equals(clickType,
                            EnvyGTSForge.getConfig().getOwnerRemoveButton())) {
                        this.removed = true;
                        MinecraftForge.EVENT_BUS.post(new TradeRemoveEvent(this));

                        GTSAttribute attribute = ((ForgeEnvyPlayer) envyPlayer).getAttributeNow(GTSAttribute.class);
                        attribute.getOwnedTrades().remove(this);

                        this.collect(envyPlayer, null);
                        envyPlayer.message(UtilChatColour.colour(EnvyGTSForge.getLocale().getMessages().getRemovedOwnTrade()));
                        return;
                    }

                    if (this.isOwner(envyPlayer)) {
                        return;
                    }

                    ConfirmationUI.builder()
                            .player(envyPlayer)
                            .playerManager(EnvyGTSForge.getPlayerManager())
                            .config(EnvyGTSForge.getGui().getSearchUIConfig().getConfirmGuiConfig())
                            .descriptionItem(UtilSprite.getPokemonElement(pokemon, EnvyGTSForge.getGui().getSearchUIConfig().getSpriteConfig(),placeholderEvent.getPlaceholders().toArray(new Placeholder[0])))
                            .confirmHandler((clicker, clickType1) -> UtilForgeConcurrency.runSync(() -> {
                                if (this.purchased || this.wasRemoved() || this.hasExpired()) {
                                    ViewTradesUI.openUI((ForgeEnvyPlayer)clicker);
                                    return;
                                }

                                if (!this.attemptPurchase(envyPlayer)) {
                                    ViewTradesUI.openUI((ForgeEnvyPlayer) envyPlayer);
                                }
                            }))
                            .returnHandler((envyPlayer1, clickType1) -> ViewTradesUI.openUI((ForgeEnvyPlayer) envyPlayer))
                            .open();
                }).build();
    }

    @Override
    public void displayClaimable(int pos, Consumer<EnvyPlayer<?>> returnGui, Pane pane) {
        int posX = pos % 9;
        int posY = pos / 9;
        var placeholderEvent = new PlaceholderCollectEvent(this, Placeholder.multiLine("%below_lore_data%", EnvyGTSForge.getLocale().getListingBelowDataLore()), this);

        MinecraftForge.EVENT_BUS.post(placeholderEvent);

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(UtilSprite.getPokemonElement(pokemon, EnvyGTSForge.getGui().getSearchUIConfig().getSpriteConfig(),placeholderEvent.getPlaceholders().toArray(new Placeholder[0])))
                .asyncClick(false)
                .singleClick()
                .clickHandler((envyPlayer, clickType) -> {
                    GTSAttribute attribute = ((ForgeEnvyPlayer) envyPlayer).getAttributeNow(GTSAttribute.class);
                    attribute.getOwnedTrades().remove(this);
                    this.collect(envyPlayer, returnGui);
                })
                .build());
    }

    protected String getPokemonJson() {
        var tag = new CompoundTag();
        this.pokemon.writeToNBT(tag);
        return tag.toString();
    }

    @Override
    public List<Placeholder> placeholders() {
        var placeholders = super.placeholders();

        placeholders.addAll(UtilSprite.getPokemonPlaceholders(pokemon, SpriteConfig.DEFAULT));
        placeholders.add(Placeholder.simple("%name%", this.pokemon.getLocalizedName()));
        return placeholders;
    }

    @Override
    public boolean matches(Object o) {
        if (o instanceof PokemonSpecification) {
            return ((PokemonSpecification) o).matches(this.pokemon);
        }

        if (o instanceof Pokemon) {
            return Objects.equals(o, this.pokemon);
        }

        return false;
    }

    @Override
    public String toString() {
        return "PokemonTrade{" +
                "cost=" + cost +
                ", expiry=" + expiry +
                ", originalOwnerName='" + originalOwnerName + '\'' +
                ", owner=" + owner +
                ", ownerName='" + ownerName + '\'' +
                ", removed=" + removed +
                ", purchased=" + purchased +
                ", pokemon=" + pokemon +
                '}';
    }

    public static class Builder extends ForgeTrade.Builder {

        private Pokemon pokemon = null;

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
                var tagCompound = TagParser.parseTag(contents);
                return this.contents(PokemonFactory.create(tagCompound));
            } catch (Exception e) {
                e.printStackTrace();
                this.contents(PokemonFactory.create(PixelmonSpecies.ABOMASNOW.getValueUnsafe()));
            }
            return this;
        }

        public Builder contents(Pokemon pokemon) {
            this.pokemon = pokemon;
            return this;
        }

        @Override
        public Builder purchased(boolean purchased) {
            return (Builder) super.purchased(purchased);
        }

        @Override
        public PokemonTrade build() {
            if (this.pokemon == null) {
                return null;
            }

            if (EnvyGTSForge.getPlayerManager().getSaveManager().getSaveMode().equals(SQLiteDatabaseDetailsConfig.ID)) {
                return new SQLPokemonTrade(this.owner, this.ownerName, this.originalOwnerName, this.cost, this.expiry,
                        this.pokemon, this.removed, this.purchased);
            } else {
                return new SQLitePokemonTrade(this.owner, this.ownerName, this.originalOwnerName, this.cost, this.expiry,
                        this.pokemon, this.removed, this.purchased);
            }
        }
    }
}
