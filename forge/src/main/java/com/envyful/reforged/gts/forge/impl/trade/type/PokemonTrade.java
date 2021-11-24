package com.envyful.reforged.gts.forge.impl.trade.type;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.discord.DiscordWebHook;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.gui.type.ConfirmationUI;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.TradeData;
import com.envyful.reforged.gts.api.data.PixelmonTradeData;
import com.envyful.reforged.gts.api.discord.DiscordEvent;
import com.envyful.reforged.gts.api.gui.SortType;
import com.envyful.reforged.gts.api.sql.ReforgedGTSQueries;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.event.TradeCollectEvent;
import com.envyful.reforged.gts.forge.event.TradeRemoveEvent;
import com.envyful.reforged.gts.forge.impl.trade.ForgeTrade;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import com.envyful.reforged.gts.forge.ui.ViewTradesUI;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.ExtraStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.MewStats;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 *
 * Represents a pokemon {@link Trade} in the GTS
 *
 */
public class PokemonTrade extends ForgeTrade {

    private final Pokemon pokemon;
    private final TradeData tradeData;

    public PokemonTrade(UUID owner, String ownerName, String originalOwnerName, double cost, long expiry,
                        Pokemon pokemon, boolean removed,
                        boolean purchased) {
        super(owner, ownerName, cost, expiry, originalOwnerName, removed, purchased);

        this.pokemon = pokemon;
        this.tradeData = new PixelmonTradeData(owner, this.pokemon.getDisplayName(), expiry,
                                               pokemon.writeToNBT(new NBTTagCompound()).toString());
    }

    @Override
    public String getDisplayName() {
        return this.pokemon.getLocalizedName();
    }

    @Override
    public void collect(EnvyPlayer<?> player, Consumer<EnvyPlayer<?>> returnGui) {
        EntityPlayerMP parent = (EntityPlayerMP) player.getParent();
        GTSAttribute attribute = player.getAttribute(ReforgedGTSForge.class);

        MinecraftForge.EVENT_BUS.post(new TradeCollectEvent((EnvyPlayer<EntityPlayerMP>) player, this));

        attribute.getOwnedTrades().remove(this);
        UtilPixelmonPlayer.getParty((EntityPlayerMP) player.getParent()).add(this.pokemon);
        ReforgedGTSForge.getInstance().getTradeManager().removeTrade(this);
        UtilConcurrency.runAsync(this::delete);

        if (returnGui == null) {
            parent.closeScreen();
        } else {
            returnGui.accept(player);
        }
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
                .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(pokemon,
                                                                        ReforgedGTSForge.getInstance().getGui().getSearchUIConfig().getSpriteConfig()))
                                   .addLore(this.formatLore(ReforgedGTSForge.getInstance().getLocale().getListingBelowDataLore()))
                                   .build())
                .clickHandler((envyPlayer, clickType) -> {
                    if (this.isOwner(envyPlayer) && Objects.equals(clickType,
                                                                   ReforgedGTSForge.getInstance().getConfig().getOwnerRemoveButton())) {
                        MinecraftForge.EVENT_BUS.post(new TradeRemoveEvent(this));
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

                    ConfirmationUI.builder()
                            .player(envyPlayer)
                            .playerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                            .config(ReforgedGTSForge.getInstance().getGui().getSearchUIConfig().getConfirmGuiConfig())
                            .descriptionItem(new ItemBuilder(UtilSprite.getPokemonElement(pokemon,
                                                                                          ReforgedGTSForge.getInstance().getGui().getSearchUIConfig().getSpriteConfig()))
                                                     .addLore(this.formatLore(ReforgedGTSForge.getInstance().getLocale().getListingBelowDataLore()))
                                                     .build())
                            .confirmHandler((clicker, clickType1) -> {
                                if (this.purchased) {
                                    ViewTradesUI.openUI((EnvyPlayer<EntityPlayerMP>) clicker);
                                    return;
                                }

                                if (!this.attemptPurchase(envyPlayer)) {
                                    ViewTradesUI.openUI((EnvyPlayer<EntityPlayerMP>) envyPlayer);
                                }
                            })
                            .returnHandler((envyPlayer1, clickType1) -> ViewTradesUI.openUI((EnvyPlayer<EntityPlayerMP>) envyPlayer))
                            .open();
                })
                .build());
    }

    private String[] formatLore(List<String> lore) {
        List<String> newLore = Lists.newArrayList();

        for (String s : lore) {
            newLore.add(UtilChatColour.translateColourCodes('&', s
                    .replace("%cost%",
                             String.format(ReforgedGTSForge.getInstance().getLocale().getMoneyFormat(), this.cost))
                    .replace("%duration%", UtilTimeFormat.getFormattedDuration((this.expiry - System.currentTimeMillis())))
                    .replace("%owner%", this.ownerName)
                    .replace("%buyer%", this.ownerName)
                    .replace("%original_owner%", this.originalOwnerName)));
        }

        return newLore.toArray(new String[0]);
    }

    @Override
    public void displayClaimable(int pos, Consumer<EnvyPlayer<?>> returnGui, Pane pane) {
        int posX = pos % 9;
        int posY = pos / 9;

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(pokemon,
                                                                        ReforgedGTSForge.getInstance().getGui().getSearchUIConfig().getSpriteConfig()))
                                   .addLore(this.formatLore(ReforgedGTSForge.getInstance().getLocale().getListingBelowExpiredOrClaimableLore()))
                                   .build())
                .clickHandler((envyPlayer, clickType) -> this.collect(envyPlayer, returnGui))
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
            preparedStatement.setString(3, this.originalOwnerName);
            preparedStatement.setLong(4, this.expiry);
            preparedStatement.setDouble(5, this.cost);
            preparedStatement.setInt(6, this.removed ? 1 : 0);
            preparedStatement.setString(7, "INSTANT_BUY");
            preparedStatement.setString(8, "p");
            preparedStatement.setString(9, this.getPokemonJson());
            preparedStatement.setInt(10, 0);

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

    @Override
    public DiscordWebHook getWebHook(DiscordEvent event) {
        if (!event.isPokemonEnabled()) {
            return null;
        }

        IVStore iVs = pokemon.getIVs();
        float ivHP = iVs.get(StatsType.HP);
        float ivAtk = iVs.get(StatsType.Attack);
        float ivDef = iVs.get(StatsType.Defence);
        float ivSpeed = iVs.get(StatsType.Speed);
        float ivSAtk = iVs.get(StatsType.SpecialAttack);
        float ivSDef = iVs.get(StatsType.SpecialDefence);
        int percentage = Math.round(((ivHP + ivDef + ivAtk + ivSpeed + ivSAtk + ivSDef) / 186f) * 100);
        float evHP = pokemon.getEVs().get(StatsType.HP);
        float evAtk = pokemon.getEVs().get(StatsType.Attack);
        float evDef = pokemon.getEVs().get(StatsType.Defence);
        float evSpeed = pokemon.getEVs().get(StatsType.Speed);
        float evSAtk = pokemon.getEVs().get(StatsType.SpecialAttack);
        float evSDef = pokemon.getEVs().get(StatsType.SpecialDefence);
        ExtraStats extraStats = pokemon.getExtraStats();

        String newJSON = event.getPokemonJSON()
                .replace("%buyer%", this.ownerName)
                .replace("%seller%", this.originalOwnerName)
                .replace("%expires_in%", UtilTimeFormat.getFormattedDuration(this.expiry - System.currentTimeMillis()))
                .replace("%price%", this.cost + "")
                .replace("%species%", pokemon.getSpecies().getLocalizedName())
                .replace("%species_lower%", pokemon.getSpecies().getLocalizedName().toLowerCase())
                .replace("%friendship%", pokemon.getFriendship() + "")
                .replace("%level%", pokemon.getLevel() + "")
                .replace("%gender%", pokemon.getGender().getLocalizedName())
                .replace("%unbreedable%", pokemon.hasSpecFlag("unbreedable") ? "True" : "False")
                .replace("%nature%", pokemon.getNature().getLocalizedName())
                .replace("%ability%", pokemon.getAbility().getLocalizedName())
                .replace("%untradeable%", pokemon.hasSpecFlag("untradeable") ? "True" : "False")
                .replace("%iv_percentage%", percentage + "")
                .replace("%iv_hp%", ((int) ivHP) + "")
                .replace("%iv_attack%", ((int) ivAtk) + "")
                .replace("%iv_defence%", ((int) ivDef) + "")
                .replace("%iv_spattack%", ((int) ivSAtk) + "")
                .replace("%iv_spdefence%", ((int) ivSDef) + "")
                .replace("%iv_speed%", ((int) ivSpeed) + "")
                .replace("%ev_hp%", ((int) evHP) + "")
                .replace("%ev_attack%", ((int) evAtk) + "")
                .replace("%ev_defence%", ((int) evDef) + "")
                .replace("%ev_spattack%", ((int) evSAtk) + "")
                .replace("%ev_spdefence%", ((int) evSDef) + "")
                .replace("%ev_speed%", ((int) evSpeed) + "")
                .replace("%move_1%", getMove(pokemon, 0))
                .replace("%move_2%", getMove(pokemon, 1))
                .replace("%move_3%", getMove(pokemon, 2))
                .replace("%move_4%", getMove(pokemon, 3))
                .replace("%mew_cloned%", extraStats instanceof MewStats ? (((MewStats) extraStats).numCloned + "") : "")
                .replace("%trio_gemmed%", extraStats instanceof LakeTrioStats ? (((LakeTrioStats) extraStats).numEnchanted + "") : "")
                .replace("%shiny%", pokemon.isShiny() ? "True" : "False")
                .replace("%form%", pokemon.getFormEnum().getLocalizedName())
                .replace("%size%", pokemon.getGrowth().getLocalizedName())
                .replace("%custom_texture%", pokemon.getCustomTexture());

        return DiscordWebHook.fromJson(newJSON);
    }

    private String getMove(Pokemon pokemon, int pos) {
        if (pokemon.getMoveset() == null) {
            return "";
        }

        if (pokemon.getMoveset().attacks.length <= pos) {
            return "";
        }

        if (pokemon.getMoveset().attacks[pos] == null) {
            return "";
        }

        return pokemon.getMoveset().attacks[pos].getActualMove().getLocalizedName();
    }

    @Override
    public boolean matches(Object o) {
        if (o instanceof PokemonSpec) {
            return ((PokemonSpec) o).matches(this.pokemon);
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
                ", tradeData=" + tradeData +
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
                NBTTagCompound tagCompound = JsonToNBT.getTagFromJson(contents);
                return this.contents(Pixelmon.pokemonFactory.create(tagCompound));
            } catch (Exception e) {
                e.printStackTrace();
                this.contents(Pixelmon.pokemonFactory.create(EnumSpecies.Abomasnow));
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

            return new PokemonTrade(this.owner, this.ownerName, this.originalOwnerName, this.cost, this.expiry,
                                    this.pokemon,
                                    this.removed,
                                    this.purchased);
        }
    }
}
