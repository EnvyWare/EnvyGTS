package com.envyful.gts.forge.api.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;

public interface TradeItemType {

    String id();

    String getDisplayName();

    TradeItem deserialize(String data) throws CommandSyntaxException;

    default List<String> getAliases() {
        return List.of(this.id());
    }

}
