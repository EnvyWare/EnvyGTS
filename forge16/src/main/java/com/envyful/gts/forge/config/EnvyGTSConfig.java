package com.envyful.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.database.DatabaseDetailsConfig;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.discord.yaml.DiscordEmbedConfig;
import com.envyful.api.discord.yaml.DiscordWebHookConfig;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.sqlite.config.SQLiteDatabaseDetailsConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings("FieldMayBeFinal")
@ConfigPath("config/EnvyGTS/config.yml")
@ConfigSerializable
public class EnvyGTSConfig extends AbstractYamlConfig {

    @Comment("The database details for EnvyGTS. For more information visit https://www.envyware.co.uk/docs/general-help/general-config/config-databases/#sqlite")
    private DatabaseDetailsConfig databaseDetails = new SQLiteDatabaseDetailsConfig("config/EnvyGTS/gts.db");

    @Comment("A map of item namespaces to their respective URL formats. %item_id% will be replaced with the item's ID. This is used for getting the item's sprite for webhooks to display in Discord")
    private Map<String, String> itemUrlFormats = ImmutableMap.of(
            "minecraft", "https://minecraft.fandom.com/wiki/Special:FilePath/%item_id%.png"
    );

    @Comment("The fallback URL for items that do not have a specified URL format")
    private String fallback = "https://minecraft.fandom.com/wiki/Special:FilePath/%item_id%.png";

    @Comment("The minimum trade duration in seconds")
    private long minTradeDuration = 300;

    @Comment("The default trade duration in seconds. This shows up as the default value in the GUI")
    private long defaultTradeDurationSeconds = 300;

    @Comment("The maximum trade duration in seconds")
    private long maxTradeDurationSeconds = 172800;

    @Comment("The minimum price a Pokemon can be listed for")
    private double minPokemonPrice = 10_000.00;

    @Comment("The button that the owner can click to remove their listing. Valid values are LEFT, RIGHT, SHIFT_LEFT, and SHIFT_RIGHT")
    private String ownerRemoveButton = "RIGHT";

    @Comment("Whether to enable tax on trades")
    private boolean enableTax = false;

    @Comment("The tax rate for trades. The final amount given to the seller is multiplied by this value")
    private double taxRate = 0.95;

    @Comment("Whether to enable broadcasts when a new listing is created")
    private boolean enableNewListingBroadcasts = true;

    @Comment("The maximum number of listings a user can have at once")
    private int maxListingsPerUser = 5;

    @Comment("The maximum price a Pokemon can be listed for")
    private double maxPrice = 10_000_000;

    @Comment("If true, eggs can be listed on the GTS")
    private boolean allowEggs = false;

    @Comment("Pokemon that are blacklisted from being traded. This is a list of Pokemon specifications")
    private List<String> blacklist = Lists.newArrayList("hoopa");

    @Comment("A list of items that are blacklisted from being traded")
    private List<String> itemBlackList = Lists.newArrayList("minecraft:stone");

    @Comment("A map of NBT keys to their respective values that are blacklisted from being traded. An item is blacklisted if it has any of the specified NBT keys with the specified values")
    private Map<String, ConfigItem.NBTValue> nbtBlacklist = ImmutableMap.of(
            "example", new ConfigItem.NBTValue("string", "example_text")
    );

    @Comment("A regex supported list of words that will be used to check the name and lore of items and pokemon attempted to be traded")
    private Map<String, BlockedWord> tradeBlockedWords = ImmutableMap.of("one", new BlockedWord("fuck|shit|cunt", "No swearing!"));

    private transient List<PokemonSpecification> blacklistCache = null;
    private transient List<Item> itemBlacklistCache = null;
    private transient Displayable.ClickType cachedOwnerRemoveButton = null;

    @Comment("A map of Pokemon specifications to their respective price modifiers. The key is the Pokemon specification and the value is the price modifier. The default results in shiny Pokemon being worth twice as much as the default \"min-pokemon-price\"")
    private Map<String, PokeSpecPricing> minPriceModifiers = ImmutableMap.of(
            "example", new PokeSpecPricing("shiny:1", new PokeSpecPricing.MathHandler("*", 2.0, 1))
    );

    @Comment("A list of conditions that Pokemon must meet to be breedable. This is a list of Pokemon specifications")
    private List<String> unbreedableConditions = Lists.newArrayList("abs:2");

    @Comment("A map of item namespaces to their respective URL formats. %item_id% will be replaced with the item's ID. This is used for getting the item's sprite for webhooks to display in Discord")
    private Map<String, String> itemReplacementURLs = ImmutableMap.of(
            "pixelmon:gracedia", "https://google.com"
    );

    @Comment("A map of patterns to their respective replacements. When a webhook is sent, the text will be replaced with the replacement")
    private Map<String, WebhookTextReplacement> webhookTextReplacement = Map.of(
            "one", new WebhookTextReplacement("WOW!", "Wow this has been replaced")
    );

    @Comment("These webhooks are executed when a trade is created, removed, or completed. The key is the event type and the value is the webhook configuration")
    private Map<String, GTSWebHook> webhooks = Map.of(
            "one", new GTSWebHook("trade_create", DiscordWebHookConfig.builder().disabled().url("https://discord.com/api/webhooks/<url here>")
                    .content("Message")
                    .avatarUrl("https://www.envyware.co.uk/img/logo.svg")
                    .username("EnvyWare Ltd")
                    .embeds(
                            DiscordEmbedConfig.builder()
                                    .title("Trade Created")
                                    .author(new DiscordEmbedConfig.Author("EnvyWare", "https://www.envyware.co.uk/img/logo.svg", "https://www.envyware.co.uk/img/logo.svg"))
                                    .description("A new trade has been created!")
                                    .color(new DiscordEmbedConfig.DiscordColor(255, 255, 255, 255))
                                    .url("https://www.envyware.co.uk")
                                    .footer(new DiscordEmbedConfig.Footer("EnvyWare", "https://www.envyware.co.uk/img/logo.svg"))
                                    .thumbnail(new DiscordEmbedConfig.Thumbnail("https://www.envyware.co.uk/img/logo.svg"))
                                    .image(new DiscordEmbedConfig.Image("https://www.envyware.co.uk/img/logo.svg"))
                                    .fields(
                                            new DiscordEmbedConfig.Field("Field 1", "Value 1", true),
                                            new DiscordEmbedConfig.Field("Price", "%price%", true)
                                    )
                                    .build()
                    )
                    .build()),
            "two", new GTSWebHook("trade_remove", DiscordWebHookConfig.builder().disabled().url("https://discord.com/api/webhooks/<url here>")
                    .content("Message")
                    .avatarUrl("https://www.envyware.co.uk/img/logo.svg")
                    .username("EnvyWare Ltd")
                    .embeds(
                            DiscordEmbedConfig.builder()
                                    .title("Trade Created")
                                    .author(new DiscordEmbedConfig.Author("EnvyWare", "https://www.envyware.co.uk/img/logo.svg", "https://www.envyware.co.uk/img/logo.svg"))
                                    .description("A new trade has been created!")
                                    .color(new DiscordEmbedConfig.DiscordColor(255, 255, 255, 255))
                                    .url("https://www.envyware.co.uk")
                                    .footer(new DiscordEmbedConfig.Footer("EnvyWare", "https://www.envyware.co.uk/img/logo.svg"))
                                    .thumbnail(new DiscordEmbedConfig.Thumbnail("https://www.envyware.co.uk/img/logo.svg"))
                                    .image(new DiscordEmbedConfig.Image("https://www.envyware.co.uk/img/logo.svg"))
                                    .fields(
                                            new DiscordEmbedConfig.Field("Field 1", "Value 1", true),
                                            new DiscordEmbedConfig.Field("Price", "%price%", true)
                                    )
                                    .build()
                    )
                    .build()),
            "three", new GTSWebHook("trade_complete", DiscordWebHookConfig.builder().disabled().url("https://discord.com/api/webhooks/<url here>")
                    .content("Message")
                    .avatarUrl("https://www.envyware.co.uk/img/logo.svg")
                    .username("EnvyWare Ltd")
                    .embeds(
                            DiscordEmbedConfig.builder()
                                    .title("Trade Created")
                                    .author(new DiscordEmbedConfig.Author("EnvyWare", "https://www.envyware.co.uk/img/logo.svg", "https://www.envyware.co.uk/img/logo.svg"))
                                    .description("A new trade has been created!")
                                    .color(new DiscordEmbedConfig.DiscordColor(255, 255, 255, 255))
                                    .url("https://www.envyware.co.uk")
                                    .footer(new DiscordEmbedConfig.Footer("EnvyWare", "https://www.envyware.co.uk/img/logo.svg"))
                                    .thumbnail(new DiscordEmbedConfig.Thumbnail("https://www.envyware.co.uk/img/logo.svg"))
                                    .image(new DiscordEmbedConfig.Image("https://www.envyware.co.uk/img/logo.svg"))
                                    .fields(
                                            new DiscordEmbedConfig.Field("Field 1", "Value 1", true),
                                            new DiscordEmbedConfig.Field("Price", "%price%", true)
                                    )
                                    .build()
                    )
                    .build())
    );

    public EnvyGTSConfig() {
        super();
    }

    public DatabaseDetailsConfig getDatabaseDetails() {
        return this.databaseDetails;
    }

    public long getMinTradeDuration() {
        return this.minTradeDuration;
    }

    public long getDefaultTradeDurationSeconds() {
        return this.defaultTradeDurationSeconds;
    }

    public double getMinPokemonPrice() {
        return this.minPokemonPrice;
    }

    public long getMaxTradeDurationSeconds() {
        return this.maxTradeDurationSeconds;
    }

    public List<PokeSpecPricing> getMinPriceModifiers() {
        return Lists.newArrayList(this.minPriceModifiers.values());
    }

    public List<String> getUnbreedableConditions() {
        return this.unbreedableConditions;
    }

    public Displayable.ClickType getOwnerRemoveButton() {
        if (this.cachedOwnerRemoveButton == null) {
            this.cachedOwnerRemoveButton = Displayable.ClickType.valueOf(this.ownerRemoveButton);
        }

        return this.cachedOwnerRemoveButton;
    }

    public boolean isEnableTax() {
        return this.enableTax;
    }

    public double getTaxRate() {
        return this.taxRate;
    }

    public boolean isEnableNewListingBroadcasts() {
        return this.enableNewListingBroadcasts;
    }

    public int getMaxListingsPerUser() {
        return this.maxListingsPerUser;
    }

    public double getMaxPrice() {
        return this.maxPrice;
    }

    public boolean isBlackListed(Pokemon pokemon) {
        if (this.blacklistCache == null) {
            List<PokemonSpecification> blacklist = Lists.newArrayList();

            for (String s : this.blacklist) {
                blacklist.add(PokemonSpecificationProxy.create(s));
            }

            this.blacklistCache = blacklist;
        }

        for (PokemonSpecification pokemonSpec : this.blacklistCache) {
            if (pokemonSpec.matches(pokemon)) {
                return true;
            }
        }

        return false;
    }

    public boolean isBlackListed(ItemStack itemStack) {
        if (this.itemBlacklistCache == null) {
            List<Item> blacklist = Lists.newArrayList();

            for (String s : this.itemBlackList) {
                blacklist.add(ForgeRegistries.ITEMS.getValue(ResourceLocationHelper.of(s)));
            }

            this.itemBlacklistCache = blacklist;
        }

        for (Item item : this.itemBlacklistCache) {
            if (Objects.equals(itemStack.getItem(), item)) {
                return true;
            }
        }

        if (!itemStack.hasTag()) {
            return false;
        }

        return this.hasBlacklistedNbt(this.nbtBlacklist, itemStack.getTag());
    }

    private boolean hasBlacklistedNbt(Map<String, ConfigItem.NBTValue> nbtBlacklist, CompoundNBT tag) {
        for (Map.Entry<String, ConfigItem.NBTValue> entry : nbtBlacklist.entrySet()) {
            if (!tag.contains(entry.getKey())) {
                continue;
            }

            if (entry.getKey().equalsIgnoreCase("nbt")) {
                return this.hasBlacklistedNbt(entry.getValue().getSubData(), tag);
            } else {
                if (this.basicNbtMatch(entry.getValue(), tag.get(entry.getKey()))) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean basicNbtMatch(ConfigItem.NBTValue value, INBT nbt) {
        String data = value.getData();

        switch (value.getType().toLowerCase()) {
            case "int":
            case "integer":
                return ((IntNBT) nbt).getAsInt() == Integer.parseInt(data);
            case "long":
                return ((LongNBT) nbt).getAsLong() == Long.parseLong(data);
            case "byte":
                return ((ByteNBT) nbt).getAsByte() == Byte.parseByte(data);
            case "double":
                return ((DoubleNBT) nbt).getAsDouble() == Double.parseDouble(data);
            case "float":
                return ((FloatNBT) nbt).getAsFloat() == Float.parseFloat(data);
            case "short":
                return ((ShortNBT) nbt).getAsShort() == Short.parseShort(data);
            default:
            case "string":
                return nbt.getAsString().equals(data);
        }
    }

    public boolean isAllowEggs() {
        return this.allowEggs;
    }

    public String getItemUrl(ItemStack itemStack) {
        ItemStack countIndependentCopy = itemStack.copy();
        countIndependentCopy.setCount(1);
        String format = this.getFormat(countIndependentCopy);

        if (format != null) {
            return format;
        }

        return this.itemUrlFormats.getOrDefault(countIndependentCopy.getItem().getRegistryName().getNamespace(), this.fallback);
    }

    private String getFormat(ItemStack itemStack) {
        String key = itemStack.getItem().getRegistryName().toString();

        if (!itemStack.hasTag()) {
            return this.itemReplacementURLs.get(key);
        }

        if (itemStack.getTag().contains("CustomModelData")) {
            String newKey = key + ":" + itemStack.getTag().getInt("CustomModelData");
            String found = this.itemReplacementURLs.get(newKey);

            if (found != null) {
                return found;
            }
        }

        CompoundNBT save = itemStack.save(new CompoundNBT());
        String jsonifiedItemUrl = this.itemReplacementURLs.get(save.getAsString());

        if (jsonifiedItemUrl != null) {
            return jsonifiedItemUrl;
        }


        return this.itemReplacementURLs.get(key);
    }

    public List<WebhookTextReplacement> getReplacements() {
        return Lists.newArrayList(this.webhookTextReplacement.values());
    }

    public String isBlocked(String text) {
        for (var tradeBlockedWord : this.tradeBlockedWords.values()) {
            if (tradeBlockedWord.pattern.matcher(text).find()) {
                return tradeBlockedWord.reason;
            }
        }

        return null;
    }

    public List<DiscordWebHookConfig> getWebhooks(String type) {
        List<DiscordWebHookConfig> configs = Lists.newArrayList();

        for (var webHook : this.webhooks.values()) {
            if (webHook.getEvent().equalsIgnoreCase(type)) {
                configs.add(webHook.getWebHook());
            }
        }

        return configs;
    }

    @ConfigSerializable
    public static class WebhookTextReplacement {

        private String pattern;
        private String replacement;

        public WebhookTextReplacement() {
        }

        public WebhookTextReplacement(String pattern, String replacement) {
            this.pattern = pattern;
            this.replacement = replacement;
        }

        public String replace(String text) {
            return text.replace(this.pattern, this.replacement);
        }
    }

    @ConfigSerializable
    public static class BlockedWord {

        private Pattern pattern;
        private String reason;

        public BlockedWord() {
        }

        public BlockedWord(String pattern, String reason) {
            this.pattern = Pattern.compile(pattern);
            this.reason = reason;
        }

        public boolean matches(String text) {
            return this.pattern.matcher(text).find();
        }

    }
}
