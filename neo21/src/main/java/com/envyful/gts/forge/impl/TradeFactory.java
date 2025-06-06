package com.envyful.gts.forge.impl;

import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.impl.trade.ForgeTrade;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TradeFactory {

    public static Trade fromResultSet(ResultSet resultSet) throws SQLException {
        return ForgeTrade.builder()
                .cost(resultSet.getDouble("cost"))
                .expiry(resultSet.getLong("expiry"))
                .owner(UUID.fromString(resultSet.getString("owner")))
                .ownerName(resultSet.getString("ownerName"))
                .originalOwnerName(resultSet.getString("originalOwner"))
                .content(resultSet.getString("content_type"))
                .contents(resultSet.getString("contents"))
                .purchased(resultSet.getInt("purchased") == 1)
                .removed(resultSet.getInt("removed") == 1)
                .build();
    }

}
