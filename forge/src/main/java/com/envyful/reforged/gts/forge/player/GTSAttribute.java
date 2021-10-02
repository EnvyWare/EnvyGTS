package com.envyful.reforged.gts.forge.player;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.attribute.AbstractForgeAttribute;
import com.envyful.reforged.gts.api.Trade;
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

    public GTSAttribute(ReforgedGTSForge manager, ForgeEnvyPlayer parent) {
        super(manager, parent);
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

    @Override
    public void load() {
        try (Connection connection = this.manager.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ReforgedGTSQueries.GET_ALL_PLAYER)) {
            preparedStatement.setString(1, this.parent.getUuid().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                this.ownedTrades.add(TradeFactory.fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try (Connection connection = this.manager.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ReforgedGTSQueries.UPDATE_PLAYER_NAME)) {
            preparedStatement.setString(1, this.parent.getParent().getName());
            preparedStatement.setString(2, this.parent.getUuid().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
