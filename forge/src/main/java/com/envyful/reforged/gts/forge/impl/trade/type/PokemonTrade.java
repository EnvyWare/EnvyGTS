package com.envyful.reforged.gts.forge.impl.trade.type;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.json.UtilGson;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.TradeData;
import com.envyful.reforged.gts.api.gui.SortType;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.impl.trade.ForgeTrade;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 *
 * Represents a pokemon {@link Trade} in the GTS
 *
 */
public class PokemonTrade extends ForgeTrade {

    private final Pokemon pokemon;
    private final TradeData tradeData;

    public PokemonTrade(UUID owner, String ownerName, double cost, long expiry, Pokemon pokemon, boolean removed) {
        super(owner, ownerName, cost, expiry, removed);

        this.pokemon = pokemon;
        this.tradeData = new TradeData(this.pokemon.getDisplayName(), expiry);
    }

    @Override
    public boolean attemptPurchase(EnvyPlayer<?> player) {
        return false; //TODO
    }

    @Override
    public void collect(EnvyPlayer<?> player) {
        UtilPixelmonPlayer.getParty((EntityPlayerMP) player.getParent()).add(this.pokemon);
    }

    @Override
    public void adminRemove(EnvyPlayer<?> admin) {
        UtilPixelmonPlayer.getParty((EntityPlayerMP) admin.getParent()).add(this.pokemon);
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
                .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(pokemon))
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
    public void displayClaimable(int pos, Pane pane) {
        int posX = pos % 9;
        int posY = pos / 9;

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(pokemon))
                                   .addLore(ReforgedGTSForge.getInstance().getLocale().getListingBelowExpiredOrClaimableLore().toArray(new String[0]))
                                   .build())
                .clickHandler((envyPlayer, clickType) -> this.collect(envyPlayer))
                .build());
    }

    @Override
    public void delete() {
        //TODO:
    }

    @Override
    public void save() {
        //TODO:
    }

    @Override
    public TradeData toData() {
        return this.tradeData;
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
            return this.contents(UtilGson.GSON.fromJson(contents, Pokemon.class));
        }

        public Builder contents(Pokemon pokemon) {
            this.pokemon = pokemon;
            return this;
        }

        @Override
        public PokemonTrade build() {
            if (this.pokemon == null) {
                return null;
            }

            return new PokemonTrade(this.owner, this.ownerName, this.cost, this.expiry, this.pokemon, this.removed);
        }
    }
}
