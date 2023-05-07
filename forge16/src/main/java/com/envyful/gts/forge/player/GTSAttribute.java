package com.envyful.gts.forge.player;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.forge.player.attribute.AbstractForgeAttribute;
import com.envyful.api.json.UtilGson;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.player.PlayerSettings;
import com.envyful.gts.api.sql.EnvyGTSQueries;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.DataSaveMode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GTSAttribute extends AbstractForgeAttribute<EnvyGTSForge> {

    private Set<Trade> ownedTrades = Sets.newHashSet();
    private int selectedSlot = -1;
    private double currentPrice = -1;
    private double currentMinPrice = -1;
    private long currentDuration = -1;
    private PlayerSettings settings = new PlayerSettings();

    public GTSAttribute(EnvyGTSForge manager, ForgePlayerManager playerManager) {
        super(manager, playerManager);
    }

    public Set<Trade> getOwnedTrades() {
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
    public void load() {
        for (Trade allTrade : EnvyGTSForge.getTradeManager().getAllTrades()) {
            if (allTrade.isOwner(this.parent.getUuid())) {
                this.ownedTrades.add(allTrade);
            }
        }

        try (Connection connection = EnvyGTSForge.getDatabase().getConnection();
             PreparedStatement settingsStatement = connection.prepareStatement(EnvyGTSQueries.GET_PLAYER_SETTINGS)) {
            settingsStatement.setString(1, this.parent.getUuid().toString());

            ResultSet settingsSet = settingsStatement.executeQuery();

            if (!settingsSet.next()) {
                return;
            }

            this.settings = UtilGson.GSON.fromJson(settingsSet.getString("settings"), PlayerSettings.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
    public void save() {

        String settingsQuery;
        if (EnvyGTSForge.getConfig().getSaveMode() == DataSaveMode.SQLITE) {
            settingsQuery = EnvyGTSQueries.UPDATE_OR_CREATE_SETTINGS_SQLITE;
        } else {
            settingsQuery = EnvyGTSQueries.UPDATE_OR_CREATE_SETTINGS;
        }
        try (Connection connection = EnvyGTSForge.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EnvyGTSQueries.UPDATE_PLAYER_NAME);
             PreparedStatement settingsStatement = connection.prepareStatement(settingsQuery)) {
            preparedStatement.setString(1, this.parent.getParent().getName().getString());
            preparedStatement.setString(2, this.parent.getUuid().toString());
            settingsStatement.setString(1, this.parent.getUuid().toString());
            settingsStatement.setString(2, UtilGson.GSON.toJson(this.settings));

            preparedStatement.executeUpdate();
            settingsStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
