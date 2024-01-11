package com.envyful.gts.forge.event;

import com.envyful.api.text.Placeholder;
import com.envyful.gts.api.Trade;
import com.google.common.collect.Lists;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class PlaceholderCollectEvent extends Event {

    private final Trade trade;
    private final List<Placeholder> placeholders = Lists.newArrayList();

    public PlaceholderCollectEvent(Trade trade, Placeholder... placeholders) {
        this.trade = trade;
        this.placeholders.addAll(Lists.newArrayList(placeholders));
    }

    public Trade getTrade() {
        return this.trade;
    }

    public List<Placeholder> getPlaceholders() {
        return this.placeholders;
    }

    public void addPlaceholder(Placeholder placeholder) {
        this.placeholders.add(placeholder);
    }

    public void addPlaceholders(Placeholder... placeholders) {
        this.placeholders.addAll(Lists.newArrayList(placeholders));
    }
}
