package com.envyful.gts.forge.impl.trade.type;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.discord.DiscordWebHook;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.gui.type.ConfirmationUI;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.text.Placeholder;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.TradeData;
import com.envyful.gts.api.data.PixelmonTradeData;
import com.envyful.gts.api.discord.DiscordEvent;
import com.envyful.gts.api.gui.SortType;
import com.envyful.gts.api.sql.EnvyGTSQueries;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.EnvyGTSConfig;
import com.envyful.gts.forge.event.PlaceholderCollectEvent;
import com.envyful.gts.forge.event.TradeCollectEvent;
import com.envyful.gts.forge.event.TradeRemoveEvent;
import com.envyful.gts.forge.impl.trade.ForgeTrade;
import com.envyful.gts.forge.player.GTSAttribute;
import com.envyful.gts.forge.ui.ViewTradesUI;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.ExtraStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MewStats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
public class PokemonTrade extends ForgeTrade {

    private final Pokemon pokemon;
    private final TradeData tradeData;

    public PokemonTrade(UUID owner, String ownerName, String originalOwnerName, double cost, long expiry,
                        Pokemon pokemon, boolean removed,
                        boolean purchased) {
        super(owner, ownerName, cost, expiry, originalOwnerName, removed, purchased);

        this.pokemon = pokemon;
        this.tradeData = new PixelmonTradeData(owner, this.pokemon.getDisplayName(), expiry,
                                               pokemon.writeToNBT(new CompoundNBT()).toString());
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
        ServerPlayerEntity parent = (ServerPlayerEntity) player.getParent();

        MinecraftForge.EVENT_BUS.post(new TradeCollectEvent((ForgeEnvyPlayer) player, this));

        StorageProxy.getParty((ServerPlayerEntity) player.getParent()).add(this.pokemon);
        EnvyGTSForge.getTradeManager().removeTrade(this);

        if (returnGui == null) {
            parent.closeContainer();
        } else {
            returnGui.accept(player);
        }

        return CompletableFuture.runAsync(this::delete, UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public void adminRemove(EnvyPlayer<?> admin) {
        ServerPlayerEntity parent = (ServerPlayerEntity) admin.getParent();

        parent.closeContainer();

        StorageProxy.getParty((ServerPlayerEntity) admin.getParent()).add(this.pokemon);
        EnvyGTSForge.getTradeManager().removeTrade(this);
        UtilConcurrency.runAsync(this::delete);
    }

    @Override
    public int compare(Trade other, SortType type) {
        return type.getComparator().compare(this.toData(), other.toData());
    }

    @Override
    public Displayable display() {
        var placeholderEvent = new PlaceholderCollectEvent(this);

        MinecraftForge.EVENT_BUS.post(placeholderEvent);

        return GuiFactory.displayableBuilder(ItemStack.class)
                .singleClick()
                .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(pokemon,
                        EnvyGTSForge.getGui().getSearchUIConfig().getSpriteConfig(),placeholderEvent.getPlaceholders().toArray(new Placeholder[0])))
                        .addLore(this.formatLore(EnvyGTSForge.getLocale().getListingBelowDataLore()))
                        .build())
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> {
                    if (this.removed || this.wasPurchased() || this.hasExpired()) {
                        ((ForgeEnvyPlayer) envyPlayer).getParent().closeContainer();
                        return;
                    }

                    if (UtilPlayer.hasPermission((ServerPlayerEntity) envyPlayer.getParent(), "envygts.admin") && Objects.equals(
                            clickType,
                            EnvyGTSForge.getConfig().getOwnerRemoveButton()
                    )) {
                        this.removed = true;
                        this.adminRemove(envyPlayer);
                        return;
                    }

                    if (this.isOwner(envyPlayer) && Objects.equals(clickType,
                            EnvyGTSForge.getConfig().getOwnerRemoveButton())) {
                        this.removed = true;
                        MinecraftForge.EVENT_BUS.post(new TradeRemoveEvent(this));

                        GTSAttribute attribute = envyPlayer.getAttributeNow(GTSAttribute.class);
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
                            .descriptionItem(new ItemBuilder(UtilSprite.getPokemonElement(pokemon,
                                    EnvyGTSForge.getGui().getSearchUIConfig().getSpriteConfig()))
                                    .addLore(this.formatLore(EnvyGTSForge.getLocale().getListingBelowDataLore()))
                                    .build())
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

    private ITextComponent[] formatLore(List<String> lore) {
        List<ITextComponent> newLore = Lists.newArrayList();

        for (String s : lore) {
            newLore.add(UtilChatColour.colour(s
                    .replace("%cost%",
                             String.format(EnvyGTSForge.getLocale().getMoneyFormat(), this.cost))
                    .replace("%duration%", UtilTimeFormat.getFormattedDuration((this.expiry - System.currentTimeMillis())))
                    .replace("%owner%", this.ownerName)
                    .replace("%buyer%", this.ownerName)
                    .replace("%original_owner%", this.originalOwnerName)));
        }

        return newLore.toArray(new ITextComponent[0]);
    }

    @Override
    public void displayClaimable(int pos, Consumer<EnvyPlayer<?>> returnGui, Pane pane) {
        int posX = pos % 9;
        int posY = pos / 9;

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(pokemon,
                        EnvyGTSForge.getGui().getSearchUIConfig().getSpriteConfig()))
                                   .addLore(this.formatLore(EnvyGTSForge.getLocale().getListingBelowExpiredOrClaimableLore()))
                                   .build())
                .asyncClick(false)
                .singleClick()
                .clickHandler((envyPlayer, clickType) -> {
                    GTSAttribute attribute = envyPlayer.getAttributeNow(GTSAttribute.class);
                    attribute.getOwnedTrades().remove(this);
                    this.collect(envyPlayer, returnGui);
                })
                .build());
    }

    @Override
    public void delete() {
        try (Connection connection = EnvyGTSForge.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EnvyGTSQueries.REMOVE_TRADE)) {
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
        try (Connection connection = EnvyGTSForge.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EnvyGTSQueries.ADD_TRADE)) {
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
        CompoundNBT tag = new CompoundNBT();
        this.pokemon.writeToNBT(tag);
        return tag.toString();
    }

    @Override
    public TradeData toData() {
        return this.tradeData;
    }

    @Override
    public String replace(String name) {
        IVStore iVs = pokemon.getIVs();
        float ivHP = iVs.getStat(BattleStatsType.HP);
        float ivAtk = iVs.getStat(BattleStatsType.ATTACK);
        float ivDef = iVs.getStat(BattleStatsType.DEFENSE);
        float ivSpeed = iVs.getStat(BattleStatsType.SPEED);
        float ivSAtk = iVs.getStat(BattleStatsType.SPECIAL_ATTACK);
        float ivSDef = iVs.getStat(BattleStatsType.SPECIAL_DEFENSE);
        int percentage = Math.round(((ivHP + ivDef + ivAtk + ivSpeed + ivSAtk + ivSDef) / 186f) * 100);
        float evHP = pokemon.getEVs().getStat(BattleStatsType.HP);
        float evAtk = pokemon.getEVs().getStat(BattleStatsType.ATTACK);
        float evDef = pokemon.getEVs().getStat(BattleStatsType.DEFENSE);
        float evSpeed = pokemon.getEVs().getStat(BattleStatsType.SPEED);
        float evSAtk = pokemon.getEVs().getStat(BattleStatsType.SPECIAL_ATTACK);
        float evSDef = pokemon.getEVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
        ExtraStats extraStats = pokemon.getExtraStats();

        name = name.replace("%buyer%", this.ownerName)
                .replace("%seller%", this.originalOwnerName)
                .replace("%held_item%", pokemon.getHeldItem().getDisplayName().getString())
                .replace("%expires_in%", UtilTimeFormat.getFormattedDuration(this.expiry - System.currentTimeMillis()))
                .replace("%price%", this.cost + "")
                .replace("%species%", pokemon.getSpecies().getLocalizedName())
                .replace("%species_lower%", pokemon.getSpecies().getLocalizedName().toLowerCase())
                .replace("%friendship%", pokemon.getFriendship() + "")
                .replace("%level%", pokemon.getPokemonLevel() + "")
                .replace("%gender%", pokemon.getGender().getLocalizedName())
                .replace("%unbreedable%", pokemon.isUnbreedable() ? "True" : "False")
                .replace("%nature%", pokemon.getNature().getLocalizedName())
                .replace("%ability%", pokemon.getAbility().getLocalizedName())
                .replace("%untradeable%", pokemon.isUntradeable() ? "True" : "False")
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
                .replace("%form%", pokemon.getForm().getLocalizedName())
                .replace("%size%", pokemon.getGrowth().getLocalizedName())
                .replace("%custom_texture%", pokemon.getPalette().getLocalizedName());

        for (EnvyGTSConfig.WebhookTextReplacement replacement : EnvyGTSForge.getConfig().getReplacements()) {
            name = replacement.replace(name);
        }

        return name;
    }

    @Override
    public DiscordWebHook getWebHook(DiscordEvent event) {
        if (!event.isPokemonEnabled()) {
            return null;
        }

        String newJSON = this.replace(event.getPokemonJSON());

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
                CompoundNBT tagCompound = JsonToNBT.parseTag(contents);
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

            return new PokemonTrade(this.owner, this.ownerName, this.originalOwnerName, this.cost, this.expiry,
                                    this.pokemon,
                                    this.removed,
                                    this.purchased);
        }
    }
}
