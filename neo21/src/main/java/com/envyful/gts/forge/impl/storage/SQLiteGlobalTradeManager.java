package com.envyful.gts.forge.impl.storage;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.impl.ForgeGlobalTradeManager;
import com.envyful.gts.forge.impl.TradeFactory;
import com.envyful.gts.forge.player.SQLiteGTSAttributeAdapter;

public class SQLiteGlobalTradeManager extends ForgeGlobalTradeManager {

    public SQLiteGlobalTradeManager() {
        EnvyGTSForge.getDatabase().query(SQLiteGTSAttributeAdapter.GET_ALL_TRADES)
                .converter(resultSet -> this.activeTrades.add(TradeFactory.fromResultSet(resultSet)))
                .executeWithConverter();
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
