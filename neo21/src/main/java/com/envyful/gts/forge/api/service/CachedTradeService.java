package com.envyful.gts.forge.api.service;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.CollectionItem;
import com.envyful.gts.forge.api.Sale;
import com.envyful.gts.forge.api.TradeService;
import com.envyful.gts.forge.api.event.TradeRemoveEvent;
import com.envyful.gts.forge.api.player.GTSAttribute;
import com.envyful.gts.forge.api.trade.Trade;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CachedTradeService implements TradeService {

    protected Map<UUID, Trade> activeListings = new HashMap<>();

    public CachedTradeService() {
        UtilConcurrency.runRepeatingTask(() -> {
            for (var activeListing : this.activeListings()) {
                if (activeListing.offer().hasExpired()) {
                    this.onTradeExpire(activeListing);
                }
            }
        }, 25, 25, TimeUnit.MILLISECONDS);
    }

    @Override
    public List<Trade> activeListings() {
        return List.copyOf(this.activeListings.values());
    }

    @Override
    public Trade activeListing(UUID offerId) {
        return this.activeListings.get(offerId);
    }

    @Override
    public List<Trade> userListings(ForgeEnvyPlayer player) {
        var userListings = new ArrayList<Trade>();

        for (var trade : this.activeListings()) {
            if (trade.isSeller(player)) {
                userListings.add(trade);
            }
        }

        return userListings;
    }

    @Override
    public void addListing(Trade trade) {
        this.activeListings.put(trade.offer().id(), trade);
    }

    protected void onTradeExpire(Trade trade) {
        this.removeListing(trade);
        var collection = new CollectionItem(trade, null);

        var player = trade.offer().seller().getPlayer();

        if (player != null && player.hasAttribute(GTSAttribute.class)) {
            var attribute = player.getAttributeNow(GTSAttribute.class);
            attribute.addCollectionItem(collection);
        } else {
            collection.record();
        }
    }

    @Override
    public void adminRemoveListing(Trade trade) {
        this.removeListing(trade);
    }

    @Override
    public void ownerRemoveListing(Trade trade) {
        this.removeListing(trade);
    }

    private void removeListing(Trade trade) {
        this.activeListings.remove(trade.offer().id());
        NeoForge.EVENT_BUS.post(new TradeRemoveEvent(trade));
    }

    @Override
    public void addSale(Sale sale) {
        this.removeListing(this.activeListing(sale.offerId()));
    }

    @Override
    public List<Trade> historicalListings() {
        return List.of();
    }

    @Override
    public List<Trade> historicalListings(ForgeEnvyPlayer player) {
        return List.of();
    }
}
