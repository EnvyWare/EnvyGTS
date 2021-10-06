package com.envyful.reforged.gts.forge.impl.trade.type;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.TradeData;
import com.envyful.reforged.gts.api.gui.SortType;
import com.envyful.reforged.gts.api.sql.ReforgedGTSQueries;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.impl.trade.ForgeTrade;
import com.envyful.reforged.gts.forge.ui.ViewTradesUI;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * Represents a pokemon {@link Trade} in the GTS
 *
 */
public class PokemonTrade extends ForgeTrade {

    private final Pokemon pokemon;
    private final TradeData tradeData;

    public PokemonTrade(UUID owner, String ownerName, double cost, long expiry, Pokemon pokemon, boolean removed,
                        boolean purchased) {
        super(owner, ownerName, cost, expiry, removed, purchased);

        this.pokemon = pokemon;
        this.tradeData = new TradeData(this.pokemon.getDisplayName(), expiry);
    }

    @Override
    public void collect(EnvyPlayer<?> player) {
        EntityPlayerMP parent = (EntityPlayerMP) player.getParent();

        parent.closeScreen();

        UtilPixelmonPlayer.getParty((EntityPlayerMP) player.getParent()).add(this.pokemon);
        ReforgedGTSForge.getInstance().getTradeManager().removeTrade(this);
        UtilConcurrency.runAsync(this::delete);
    }

    @Override
    public void adminRemove(EnvyPlayer<?> admin) {
        EntityPlayerMP parent = (EntityPlayerMP) admin.getParent();

        parent.closeScreen();

        UtilPixelmonPlayer.getParty((EntityPlayerMP) admin.getParent()).add(this.pokemon);
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
                .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(pokemon))
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
        try (Connection connection = ReforgedGTSForge.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ReforgedGTSQueries.REMOVE_TRADE)) {
            preparedStatement.setString(1, this.owner.toString());
            preparedStatement.setLong(2, this.expiry);
            preparedStatement.setDouble(3, this.cost);
            preparedStatement.setString(4, "p");
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
            preparedStatement.setLong(3, this.expiry);
            preparedStatement.setDouble(4, this.cost);
            preparedStatement.setInt(5, this.removed ? 1 : 0);
            preparedStatement.setString(6, "INSTANT_BUY");
            preparedStatement.setString(7, "p");
            preparedStatement.setString(8, this.getPokemonJson());
            preparedStatement.setInt(9, 0);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getPokemonJson() {
        NBTTagCompound tag = new NBTTagCompound();
        this.pokemon.writeToNBT(tag);
        return tag.toString();
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
            try {
                NBTTagCompound tagCompound = JsonToNBT.getTagFromJson(contents);
                return this.contents(Pixelmon.pokemonFactory.create(tagCompound));
            } catch (NBTException e) {
                e.printStackTrace();
                this.contents(Pixelmon.pokemonFactory.create(EnumSpecies.Magikarp));
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

            return new PokemonTrade(this.owner, this.ownerName, this.cost, this.expiry, this.pokemon, this.removed,
                                    this.purchased);
        }
    }
}
