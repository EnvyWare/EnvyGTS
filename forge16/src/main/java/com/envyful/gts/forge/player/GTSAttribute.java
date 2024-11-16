package com.envyful.gts.forge.player;

import com.envyful.api.database.sql.SqlType;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.attribute.ManagedForgeAttribute;
import com.envyful.api.json.UtilGson;
import com.envyful.api.player.attribute.adapter.SelfAttributeAdapter;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.player.PlayerSettings;
import com.envyful.gts.api.sql.EnvyGTSQueries;
import com.envyful.gts.forge.EnvyGTSForge;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class GTSAttribute extends ManagedForgeAttribute<EnvyGTSForge> implements SelfAttributeAdapter<UUID> {

    private List<Trade> ownedTrades = Lists.newArrayList();
    private int selectedSlot = -1;
    private double currentPrice = -1;
    private double currentMinPrice = -1;
    private long currentDuration = -1;
    private PlayerSettings settings = new PlayerSettings();
    private String name;

    public GTSAttribute(UUID id) {
        super(id, EnvyGTSForge.getInstance());
    }

    public List<Trade> getOwnedTrades() {
        return this.ownedTrades;
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void setSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public double getCurrentPrice() {
        return this.currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public long getCurrentDuration() {
        return this.currentDuration;
    }

    public void setCurrentDuration(long currentDuration) {
        this.currentDuration = currentDuration;
    }

    public double getCurrentMinPrice() {
        return this.currentMinPrice;
    }

    public void setCurrentMinPrice(double currentMinPrice) {
        this.currentMinPrice = currentMinPrice;
    }

    public PlayerSettings getSettings() {
        return this.settings;
    }

    @Override
    public void setParent(ForgeEnvyPlayer parent) {
        super.setParent(parent);

        this.name = parent.getName();

        UtilForgeConcurrency.runSync(() -> {
            boolean returnMessage = false;

            for (Trade ownedTrade : Lists.newArrayList(this.ownedTrades)) {
                if (ownedTrade.hasExpired() || ownedTrade.wasPurchased() || ownedTrade.wasRemoved()) {
                    returnMessage = true;
                    ownedTrade.collect(this.parent, null);
                    this.ownedTrades.remove(ownedTrade);
                }
            }

            if (returnMessage) {
                this.parent.message(UtilChatColour.colour(EnvyGTSForge.getLocale().getMessages().getItemsToClaim()));
            }
        });
    }

    @Override
    public void load() {
        for (var allTrade : EnvyGTSForge.getTradeManager().getAllTrades()) {
            if (allTrade.isOwner(this.id)) {
                this.ownedTrades.add(allTrade);
            }
        }


        EnvyGTSForge.getDatabase()
                .query(EnvyGTSQueries.GET_PLAYER_SETTINGS)
                .data(SqlType.text(this.id.toString()))
                .converter(resultSet -> {
                    this.settings = UtilGson.GSON.fromJson(resultSet.getString("settings"), PlayerSettings.class);
                    return null;
                })
                .executeWithConverter();
    }

    @Override
    public void save() {
        EnvyGTSForge.getDatabase()
                .update(EnvyGTSQueries.UPDATE_PLAYER_NAME)
                .data(SqlType.text(this.name), SqlType.text(this.id.toString()))
                .execute();

        EnvyGTSForge.getDatabase()
                .update(EnvyGTSQueries.UPDATE_OR_CREATE_SETTINGS)
                .data(SqlType.text(this.id.toString()), SqlType.text(UtilGson.GSON.toJson(this.settings)))
                .execute();
    }
}
