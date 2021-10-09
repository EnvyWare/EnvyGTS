package com.envyful.reforged.gts.forge.player;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.attribute.AbstractForgeAttribute;
import com.envyful.api.json.UtilGson;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.player.PlayerSettings;
import com.envyful.reforged.gts.api.sql.ReforgedGTSQueries;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.impl.TradeFactory;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GTSAttribute extends AbstractForgeAttribute<ReforgedGTSForge> {

    private List<Trade> ownedTrades = Lists.newArrayList();
    private int selectedSlot = -1;
    private double currentPrice = -1;
    private double currentMinPrice = -1;
    private long currentDuration = -1;
    private PlayerSettings settings = new PlayerSettings();

    public GTSAttribute(ReforgedGTSForge manager, EnvyPlayer<?> parent) {
        super(manager, (ForgeEnvyPlayer) parent);
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
    public void load() {
        try (Connection connection = this.manager.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ReforgedGTSQueries.GET_ALL_PLAYER);
             PreparedStatement settingsStatement = connection.prepareStatement(ReforgedGTSQueries.GET_PLAYER_SETTINGS)) {
            preparedStatement.setString(1, this.parent.getUuid().toString());
            settingsStatement.setString(1, this.parent.getUuid().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                this.ownedTrades.add(TradeFactory.fromResultSet(resultSet));
            }

            ResultSet settingsSet = settingsStatement.executeQuery();

            if (!settingsSet.next()) {
                return;
            }

            this.settings = UtilGson.GSON.fromJson(settingsSet.getString("settings"), PlayerSettings.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try (Connection connection = this.manager.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ReforgedGTSQueries.UPDATE_PLAYER_NAME);
             PreparedStatement settingsStatement = connection.prepareStatement(ReforgedGTSQueries.UPDATE_OR_CREATE_SETTINGS)) {
            preparedStatement.setString(1, this.parent.getParent().getName());
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
