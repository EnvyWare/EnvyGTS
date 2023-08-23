package com.envyful.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.gui.item.Displayable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import net.minecraft.nbt.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("FieldMayBeFinal")
@ConfigPath("config/EnvyGTS/config.yml")
@ConfigSerializable
public class EnvyGTSConfig extends AbstractYamlConfig {

    private SQLDatabaseDetails databaseDetails = new SQLDatabaseDetails("EnvyGTS", "0.0.0.0", 3306, "admin",
                                                                        "password", "database"
    );

    private Map<String, String> itemUrlFormats = ImmutableMap.of(
            "minecraft", "https://minecraft.fandom.com/wiki/Special:FilePath/%item_id%.png"
    );

    private String fallback = "https://minecraft.fandom.com/wiki/Special:FilePath/%item_id%.png";
    private String noURL = "https://minecraft.fandom.com/wiki/Special:FilePath/%item_id%.png";

    private long minTradeDuration = 300;
    private long defaultTradeDurationSeconds = 300;
    private long maxTradeDurationSeconds = 172800;
    private double minPokemonPrice = 10_000.00;
    private boolean enableWebHooks = false;
    private String ownerRemoveButton = "RIGHT";
    private boolean enableTax = false;
    private double taxRate = 0.95;
    private boolean enableNewListingBroadcasts = true;
    private int maxListingsPerUser = 5;
    private double maxPrice = 10_000_000;
    private boolean allowEggs = false;
    private boolean enableOpeningUIMessage = true;

    private List<String> blacklist = Lists.newArrayList("hoopa");
    private List<String> itemBlackList = Lists.newArrayList("minecraft:stone");
    private Map<String, ConfigItem.NBTValue> nbtBlacklist = ImmutableMap.of(
            "example", new ConfigItem.NBTValue("string", "example_text")
    );

    private transient List<PokemonSpecification> blacklistCache = null;
    private transient List<Item> itemBlacklistCache = null;

    private transient Displayable.ClickType cachedOwnerRemoveButton = null;

    private Map<String, PokeSpecPricing> minPriceModifiers = ImmutableMap.of(
            "example", new PokeSpecPricing("shiny:1", new PokeSpecPricing.MathHandler("*", 2.0, 1))
    );

    private List<String> unbreedableConditions = Lists.newArrayList("abs:2");

    private Map<String, String> itemReplacementURLs = ImmutableMap.of(
            "pixelmon:gracedia", "https://google.com"
    );

    public EnvyGTSConfig() {
        super();
    }

    public SQLDatabaseDetails getDatabaseDetails() {
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

    public boolean isEnableWebHooks() {
        return this.enableWebHooks;
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

    private boolean hasBlacklistedNbt(Map<String, ConfigItem.NBTValue> nbtBlacklist, CompoundTag tag) {
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

    public boolean basicNbtMatch(ConfigItem.NBTValue value, Tag nbt) {
        String data = value.getData();

        switch (value.getType().toLowerCase()) {
            case "int":
            case "integer":
                return ((IntTag) nbt).getAsInt() == Integer.parseInt(data);
            case "long":
                return ((LongTag) nbt).getAsLong() == Long.parseLong(data);
            case "byte":
                return ((ByteTag) nbt).getAsByte() == Byte.parseByte(data);
            case "double":
                return ((DoubleTag) nbt).getAsDouble() == Double.parseDouble(data);
            case "float":
                return ((FloatTag) nbt).getAsFloat() == Float.parseFloat(data);
            case "short":
                return ((ShortTag) nbt).getAsShort() == Short.parseShort(data);
            default:
            case "string":
                return nbt.getAsString().equals(data);
        }
    }

    public boolean isAllowEggs() {
        return this.allowEggs;
    }

    public boolean isEnableOpeningUIMessage() {
        return this.enableOpeningUIMessage;
    }

    public String getItemUrl(ItemStack itemStack) {
        ItemStack countIndependentCopy = itemStack.copy();
        countIndependentCopy.setCount(1);
        String format = this.getFormat(countIndependentCopy);

        if (format != null) {
            return format;
        }

        return this.itemUrlFormats.getOrDefault(ForgeRegistries.ITEMS.getKey(countIndependentCopy.getItem()).getNamespace(), this.fallback);
    }

    private String getFormat(ItemStack itemStack) {
        String key = ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString();

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

        CompoundTag save = itemStack.save(new CompoundTag());
        String jsonifiedItemUrl = this.itemReplacementURLs.get(save.getAsString());

        if (jsonifiedItemUrl != null) {
            return jsonifiedItemUrl;
        }


        return this.itemReplacementURLs.get(key);
    }

    public String getNoUrl() {
        return this.noURL;
    }
}
