package dev.morazzer.cookies.mod.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Class to represent an item.
 */
@Getter
@SuppressWarnings("unused")
public class RepositoryItem {

    /**
     * Map to convert between 1.8.9 ids to modern ids.
     */
    private static final Map<String, String> OLD_NEW_ID_MAP = Map.ofEntries(
        new AbstractMap.SimpleEntry<>("ink_sack:1", "red_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:2", "green_dye"),
        new AbstractMap.SimpleEntry<>("cactus_green", "green_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:3", "cocoa_bean"),
        new AbstractMap.SimpleEntry<>("ink_sack:4", "lapis_lazuli"),
        new AbstractMap.SimpleEntry<>("ink_sack:5", "purple_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:6", "cyan_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:7", "light_gray_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:8", "gray_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:9", "pink_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:10", "lime_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:11", "yellow_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:12", "light_blue_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:13", "magenta_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:14", "orange_dye"),
        new AbstractMap.SimpleEntry<>("ink_sack:15", "bone_meal")
                                                                           );

    @Getter
    private final static Map<String, RepositoryItem> itemMap = new ConcurrentHashMap<>();

    /**
     * Codec to serialize and deserialize an repository item.
     */
    public final static PrimitiveCodec<RepositoryItem> CODEC = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<RepositoryItem> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input)
                      .map(m -> OLD_NEW_ID_MAP.getOrDefault(m.toLowerCase(), m.toLowerCase()))
                      .map(RepositoryItem::of);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, RepositoryItem value) {
            return ops.createString(value.internalId);
        }
    };
    @Setter(AccessLevel.PACKAGE)
    private Set<Recipe> recipes;
    @Setter(AccessLevel.PACKAGE)
    private Set<Recipe> usedInRecipeAsIngredient;
    private String name;
    @SerializedName("internal_id")
    private String realInternalId;
    private String internalId;
    private String category;
    @SerializedName("categoryb")
    private String alternativeCategory;
    private Tier tier;
    private double value;
    @SerializedName("motes_value")
    private double motesValue;
    private String power;
    private String essence;
    @SerializedName("essence_cost")
    private String essenceCost;
    private String soulboundtype;
    private String reforge;
    @SerializedName("reforge_type")
    private String reforgeType;
    private boolean tradable;
    private boolean auctionable;
    private boolean reforgeable;
    private boolean enchantable;
    private boolean museumable;
    private boolean bazaarable;
    private boolean soulboundable;
    @SerializedName("rift_item")
    private boolean riftItem;
    @SerializedName("rift_transferrable")
    private boolean riftTransferrable;
    private boolean sackable;
    private Map<String, String> stats;
    @SerializedName("rift_stats")
    private Map<String, String> riftStats;
    private Map<String, String> requirements;
    @SerializedName("dungeon_requirements")
    private Map<String, String> dungeonRequirements;
    private Map<String, String> salvageable;
    @SerializedName("reforge_requirements")
    private Map<String, String> reforgeRequirements;
    @SerializedName("collection_menu")
    private Map<String, String> collectionMenu;
    private Text lore;
    private Gemstone[] gemslots;

    /**
     * Loads a collection of items.
     *
     * @param path The path to the file.
     */
    public static void load(Path path) {
        if (!Files.exists(path)) {
            System.err.println("Unable to load item list. (FILE_NOT_FOUND)");
            return;
        }

        try {
            final String content = Files.readString(path, StandardCharsets.UTF_8);
            final Text.Serializer serializer = new Text.Serializer(DynamicRegistryManager.EMPTY);
            Gson gson = new GsonBuilder().registerTypeAdapter(Text.class, serializer).create();
            final RepositoryItem[] repositoryItems = gson.fromJson(content, new TypeToken<>() {
            });
            for (RepositoryItem repositoryItem : repositoryItems) {
                repositoryItem.setRecipes(new HashSet<>());
                repositoryItem.setUsedInRecipeAsIngredient(new HashSet<>());
                repositoryItem.internalId =
                    OLD_NEW_ID_MAP.getOrDefault(
                        repositoryItem.realInternalId.toLowerCase(),
                        repositoryItem.realInternalId);
                itemMap.put(repositoryItem.internalId.toLowerCase(Locale.ROOT), repositoryItem);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the item corresponding to the id or null.
     *
     * @param id The id.
     * @return The item or null.
     */
    public static RepositoryItem of(String id) {
        return itemMap.get(OLD_NEW_ID_MAP.getOrDefault(id.toLowerCase(), id).toLowerCase(Locale.ROOT));
    }

    /**
     * Tries to fine an item by its name.
     *
     * @param name The name of the item.
     * @return The item.
     */
    public static Optional<RepositoryItem> ofName(String name) {
        return itemMap.values().stream().filter(repositoryItem -> repositoryItem.getName().equalsIgnoreCase(name))
                      .findFirst();
    }

    /**
     * Gets the name as text with the formatting applied.
     *
     * @return The formatted name.
     */
    public Text getFormattedName() {
        return Text.literal(this.name).formatted(this.tier.formatting);
    }

    /**
     * All tiers (rarities) available as default (so without rarity upgrades)
     */
    @Getter
    @SuppressWarnings("MissingJavadoc")
    public enum Tier {
        @SerializedName("Common")
        COMMON(Formatting.WHITE),
        @SerializedName("Uncommon")
        UNCOMMON(Formatting.GREEN),
        @SerializedName("Rare")
        RARE(Formatting.BLUE),
        @SerializedName("Epic")
        EPIC(Formatting.DARK_PURPLE),
        @SerializedName("Legendary")
        LEGENDARY(Formatting.GOLD),
        @SerializedName("Mythic")
        MYTHIC(Formatting.LIGHT_PURPLE),
        @SerializedName("Special")
        SPECIAL(Formatting.RED),
        @SerializedName("Very Special")
        VERY_SPECIAL(Formatting.RED),
        @SerializedName("Ultimate")
        ULTIMATE(Formatting.DARK_RED),
        @SerializedName("Admin")
        ADMIN(Formatting.DARK_RED);


        private final Formatting formatting;

        Tier(Formatting formatting) {
            this.formatting = formatting;
        }
    }

    /**
     * A gemstone slot.
     */
    @Getter
    @SuppressWarnings("unused")
    public static class Gemstone {
        private String type;
        private String coins;
        private String[] items;
    }
}
