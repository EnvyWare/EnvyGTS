package com.envyful.reforged.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.forge.chat.UtilChatColour;
import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/ReforgedGTS/locale.yml")
@ConfigSerializable
public class LocaleConfig extends AbstractYamlConfig {

    private List<String> listingBelowDataLore = Lists.newArrayList(
            "  ",
            "&bCost: &a$%cost%",
            "&bRemaining Time: &e%duration%"
    );

    private List<String> listingBelowExpiredOrClaimableLore = Lists.newArrayList(
            "  "
    );

    private Messages messages = new Messages();

    public LocaleConfig() {
        super();
    }

    public Messages getMessages() {
        return this.messages;
    }

    public List<String> getListingBelowDataLore() {
        return this.listingBelowDataLore;
    }

    public List<String> getListingBelowExpiredOrClaimableLore() {
        return this.listingBelowExpiredOrClaimableLore;
    }

    @ConfigSerializable
    public static class Messages {

        private String openingUi = "&e&l(!) &eOpening GTS...";
        private String sellInsuffucientArgs = "&c&l(!) &cInsufficient args! /gts s <amount> <price>";
        private String sellNoItemInHand = "&c&l(!) &cYou must have an item in your hand to sell!";

        private String amountMustBePositive = "&c&l(!) &cAmount must be a positive number!";
        private String priceMustBeMoreThanOne = "&c&l(!) &cPrice cannot be less than $1";

        private String addedItemToGts = "&e&l(!) &eSuccessfully listed item on GTS";

        public Messages() {}

        public String getOpeningUi() {
            return UtilChatColour.translateColourCodes('&', this.openingUi);
        }

        public String getSellInsuffucientArgs() {
            return UtilChatColour.translateColourCodes('&', this.sellInsuffucientArgs);
        }

        public String getSellNoItemInHand() {
            return UtilChatColour.translateColourCodes('&', this.sellNoItemInHand);
        }

        public String getAmountMustBePositive() {
            return UtilChatColour.translateColourCodes('&', this.amountMustBePositive);
        }

        public String getPriceMustBeMoreThanOne() {
            return UtilChatColour.translateColourCodes('&', this.priceMustBeMoreThanOne);
        }

        public String getAddedItemToGts() {
            return this.addedItemToGts;
        }
    }
}
