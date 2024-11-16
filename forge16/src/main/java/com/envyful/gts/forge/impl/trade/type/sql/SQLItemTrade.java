package com.envyful.gts.forge.impl.trade.type.sql;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.database.sql.SqlType;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.impl.trade.type.ItemTrade;
import com.envyful.gts.forge.player.SQLGTSAttributeAdapter;
import com.envyful.gts.forge.player.SQLiteGTSAttributeAdapter;
import net.minecraft.item.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLItemTrade extends ItemTrade {

    public SQLItemTrade(UUID owner, String ownerName, String originalOwnerName, double cost, long expiry, ItemStack item, boolean removed, boolean purchased) {
        super(owner, ownerName, originalOwnerName, cost, expiry, item, removed, purchased);
    }

    @Override
    public void delete() {
        EnvyGTSForge.getDatabase().update(SQLGTSAttributeAdapter.REMOVE_TRADE)
                .data(
                        SqlType.text(this.owner.toString()),
                        SqlType.bigInt(this.expiry),
                        SqlType.decimal(this.cost),
                        SqlType.text("i"),
                        SqlType.text("INSTANT_BUY")
                )
                .execute();
    }

    @Override
    public void save() {
        EnvyGTSForge.getDatabase().update(SQLGTSAttributeAdapter.ADD_TRADE)
                .data(
                        SqlType.text(this.owner.toString()),
                        SqlType.text(this.ownerName),
                        SqlType.text(this.originalOwnerName),
                        SqlType.bigInt(this.expiry),
                        SqlType.decimal(this.cost),
                        SqlType.integer(this.removed ? 1 : 0),
                        SqlType.text("INSTANT_BUY"),
                        SqlType.text("i"),
                        SqlType.text(this.getItemJson()),
                        SqlType.integer(0)
                )
                .execute();
    }

    @Override
    protected CompletableFuture<Void> setRemoved() {
        this.removed = true;

        return EnvyGTSForge.getDatabase().update(SQLGTSAttributeAdapter.UPDATE_REMOVED)
                .data(
                        SqlType.integer(1),
                        SqlType.integer(this.purchased ? 1 : 0),
                        SqlType.text(this.owner.toString()),
                        SqlType.bigInt(this.expiry),
                        SqlType.decimal(this.cost),
                        SqlType.text("i"),
                        SqlType.text("INSTANT_BUY")
                )
                .executeAsync().thenRun(() -> {});
    }

    @Override
    protected void updateOwner(UUID newOwner, String newOwnerName) {
        var owner = this.owner;
        this.owner = newOwner;
        this.ownerName = newOwnerName;

        EnvyGTSForge.getDatabase().update(SQLGTSAttributeAdapter.UPDATE_OWNER)
                .data(
                        SqlType.text(newOwner.toString()),
                        SqlType.text(newOwnerName),
                        SqlType.text(owner.toString()),
                        SqlType.bigInt(this.expiry),
                        SqlType.decimal(this.cost),
                        SqlType.text("i"),
                        SqlType.text("INSTANT_BUY")
                )
                .executeAsync();
    }
}
