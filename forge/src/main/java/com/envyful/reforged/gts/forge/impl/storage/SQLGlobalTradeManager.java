package com.envyful.reforged.gts.forge.impl.storage;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.sql.ReforgedGTSQueries;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.impl.ForgeGlobalTradeManager;
import com.envyful.reforged.gts.forge.impl.TradeFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLGlobalTradeManager extends ForgeGlobalTradeManager {

    public SQLGlobalTradeManager(ReforgedGTSForge mod) {
        try (Connection connection = mod.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ReforgedGTSQueries.GET_ALL_ACTIVE)) {
            preparedStatement.setLong(1, System.currentTimeMillis());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                this.activeTrades.add(TradeFactory.fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addTrade(EnvyPlayer<?> player, Trade trade) {
        if (!super.addTrade(player, trade)) {
            return false;
        }

        trade.save();
        return true;
    }
}
