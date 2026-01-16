package com.envyful.gts.forge.api;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.player.PlayerInfo;
import com.envyful.gts.forge.api.trade.Trade;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record CollectionItem(TradeOffer offer, @Nullable Sale sale) {

    public CollectionItem(Trade trade, Sale sale) {
        this(trade.offer(), sale);
    }

    public UUID getId() {
        return this.offer().id();
    }

    public PlayerInfo getOwner() {
        if (this.sale != null) {
            return this.sale.buyer();
        }
        return this.offer().seller();
    }

    public void record() {
        var owner = this.getOwner();

        var query = EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.COLLECTIONS)
                .set(GTSDatabase.COLLECTIONS_PLAYER, owner.uniqueId().toString())
                .set(GTSDatabase.TRADES_OFFER_ID, this.offer().id().toString());

        if (this.sale != null) {
            query = query.set(GTSDatabase.SALES_SALE_ID, this.sale.saleId().toString());
        } else {
            query = query.setNull(GTSDatabase.SALES_SALE_ID);
        }

        query.executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }
}
