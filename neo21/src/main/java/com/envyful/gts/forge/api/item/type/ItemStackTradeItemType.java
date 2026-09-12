package com.envyful.gts.forge.api.item.type;

import com.envyful.gts.forge.api.item.TradeItem;
import com.envyful.gts.forge.api.item.TradeItemType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;

public class ItemStackTradeItemType implements TradeItemType {

    @Override
    public String id() {
        return "item";
    }

    @Override
    public String getDisplayName() {
        return "Items";
    }

    @Override
    public TradeItem deserialize(String data) throws CommandSyntaxException {
        return new ItemStackTradeItem(data);
    }

    @Override
    public List<String> getAliases() {
        return List.of("item", "items");
    }
}
