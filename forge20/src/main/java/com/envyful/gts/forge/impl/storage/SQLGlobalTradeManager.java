package com.envyful.gts.forge.impl.storage;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.sql.EnvyGTSQueries;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.impl.ForgeGlobalTradeManager;
import com.envyful.gts.forge.impl.TradeFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLGlobalTradeManager extends ForgeGlobalTradeManager {

    public SQLGlobalTradeManager() {
        try (Connection connection = EnvyGTSForge.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EnvyGTSQueries.GET_ALL_TRADES)) {
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
