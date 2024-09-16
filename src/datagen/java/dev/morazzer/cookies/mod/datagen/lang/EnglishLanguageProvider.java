package dev.morazzer.cookies.mod.datagen.lang;

import dev.morazzer.cookies.mod.datagen.CookiesLanguageProvider;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.registry.RegistryWrapper;

public class EnglishLanguageProvider extends CookiesLanguageProvider {
	public EnglishLanguageProvider(
			FabricDataOutput fabricDataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(fabricDataOutput, registryLookupFuture);
	}

	@Override
	protected void generateLocals(
			RegistryWrapper.WrapperLookup registryLookup, CookiesTranslationBuilder translationBuilder) {

		translationBuilder.add(MOD, "Cookies Mod");
		translationBuilder.add(KEYBIND, "Cookies Mod");
		translationBuilder.add(SEARCH, "Item Search");

		translationBuilder.add(UNEXPECTED_ERROR, "An unexpected error occurred while executing the command!");
		translationBuilder.add(INTERNAL_ERROR,
				"An internal error occurred please report this on our discord. (Click to copy)");

		translationBuilder.add(UPDATE_AVAILABLE, "Your version of the mod isn't up-to-date!");
		translationBuilder.add(UPDATE_MODRINTH, "(Click here to open modrinth)");


		translationBuilder.addConfig(CONFIG_SEARCH, "Search", "Search all config settings.");
		translationBuilder.addConfig(CONFIG_TOGGLED, "Toggled", "Show either all on or off settings!");

		translationBuilder.add(ITEM_SOURCE_ALL, "All items");
		translationBuilder.add(ITEM_SOURCE_CHEST, "Chest");
		translationBuilder.add(ITEM_SOURCE_INVENTORY, "Inventory");
		translationBuilder.add(ITEM_SOURCE_SACK, "Sack");
		translationBuilder.add(ITEM_SOURCE_STORAGE, "Storage");

		this.addItemStats(translationBuilder);
		this.addMisc(translationBuilder);
		this.addForgeRecipeScreen(translationBuilder);
		this.addItemSearchScreen(translationBuilder);

		this.addHotmUtils(translationBuilder);
		this.addCompostUpgrades(translationBuilder);
		this.addPlotPriceBreakdown(translationBuilder);

		// Config
		this.addCleanupConfig(translationBuilder);
		this.addDevConfig(translationBuilder);
		this.addFarmingConfig(translationBuilder);
		this.addHelpersConfig(translationBuilder);
		this.addMiningConfig(translationBuilder);
		this.addMiscConfig(translationBuilder);
		this.addDungeonConfig(translationBuilder);
	}

	private void addDungeonConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_DUNGEON, "Dungeon Config", "Various settings related to dungeons");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_USE_BACKEND,
				"Relay to backend",
				"Whether information should be exchanged with the backend or not");
		translationBuilder.add(CONFIG_DUNGEON_RENDER, "Render");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_SHOW_PLAYER_SKULLS,
				"Show player skulls",
				"Shows the player skull instead of the map marker.");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_SHOW_PLAYER_NAMES,
				"Show player names",
				"Shows the names of the players on the map.");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_RENDER_OVER_TEXT,
				"Player over room",
				"Renders the player name over the dungeon room text.");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_RENDER_KEEP_WITHER_DOOR,
				"Keep wither doors",
				"Prevents wither doors from changing to normal ones.");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_RENDER_SHOW_SECRETS,
				"Show secrets",
				"Shows information about the secrets in rooms.");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_RENDER_SHOW_PUZZLE_NAME,
				"Show puzzle name",
				"Shows the puzzle name (if known).");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_RENDER_ROOM_STATUS_AS_COLOR,
				"Show status color",
				"Shows the current room status as color.");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_RENDER_MAP_BACKGROUND,
				"Map background",
				"Renders a monochrome background behind the map.");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_RENDER_MAP_BACKGROUND_COLOR,
				"Background color",
				"The color to use for the map background");
	}

	private void addPlotPriceBreakdown(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(PLOT_PRICE_BREAKDOWN_PLOTS_MISSING, "Plots missing");
		translationBuilder.add(PLOT_PRICE_BREAKDOWN_PLOTS_OWNED, "Plots owned");
		translationBuilder.add(PLOT_PRICE_BREAKDOWN_MISSING, "Missing");
		translationBuilder.add(PLOT_PRICE_BREAKDOWN, "Breakdown");
		translationBuilder.add(PLOT_PRICE_BREAKDOWN_COMPOST_BREAKDOWN, "Compost Breakdown");
	}

	private void addHotmUtils(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(HOTM_UTILS_COST_NEXT_10, "Cost (10)");
		translationBuilder.add(HOTM_UTILS_COST_TOTAL, "Cost (Total)");
	}

	private void addCompostUpgrades(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(COMPOST_UPGRADE_MAX_TIER, "Max Tier");
		translationBuilder.add(COMPOST_UPGRADE_REMAINING_COST, "Remaining Upgrade Cost");
	}

	private void addItemStats(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(ITEM_STATS_VALUE_COINS, "Coin Value");
		translationBuilder.add(ITEM_STATS_VALUE_MOTES, "Motes Value");
		translationBuilder.add(ITEM_STATS_MUSEUM, "Museum");
		translationBuilder.add(ITEM_STATS_OBTAINED, "Obtained");
	}

	private void addMisc(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(CLOSE, "Close");
		translationBuilder.add(GO_BACK, "Go Back");
		translationBuilder.add(TO_RECIPE_BOOK, "To Recipe Book");
		translationBuilder.add(CLICK_TO_VIEW, "Click to view");
		translationBuilder.add(CLICK_TO_EDIT, "Click to edit");
		translationBuilder.add(CLICK_TO_SELECT, "Click to select");
		translationBuilder.add(LEFT_CLICK_TO_VIEW, "Left-click to view");
		translationBuilder.add(LEFT_CLICK_TO_SET, "Left-click to set");
		translationBuilder.add(RIGHT_CLICK_TO_EDIT, "Right-click to edit");
		translationBuilder.add(RANCHER_BOOTS_SAVE_GLOBAL, "Save Global");
		translationBuilder.add(RANCHER_BOOTS_RESET_TO_DEFAULT, "Reset to default");
		translationBuilder.add(RANCHER_BOOTS_FARMING_SPEEDS, "Farming Speeds");

		translationBuilder.add(PAGE, "Page");
		translationBuilder.add(PAGE_WITH_NUMBER, "Page %s");
		translationBuilder.add(PAGE_NEXT, "Next Page");
		translationBuilder.add(PAGE_PREVIOUS, "Previous Page");

		translationBuilder.add(ITEM_NOT_FOUND, "Can't find item %s");
		translationBuilder.add(NOT_FOUND, "Not Found");

		translationBuilder.add(SELECT_SLOT_ELIGIBLE, "Eligible for selection");
		translationBuilder.add(SELECT_SLOT_NOT_ELIGIBLE, "Not eligible for selection");

		translationBuilder.add(CRAFT_HELPER, "Set craft helper item");
		translationBuilder.add(CRAFT_HELPER_LINE_1, "Set the recipe as the selected");
		translationBuilder.add(CRAFT_HELPER_LINE_2, "craft helper item!");

		translationBuilder.add(
				BACKEND_WRONG_VERSION,
				"Your version of the mod is outdated, please download a newer version to use the backend!");
	}

	private void addForgeRecipeScreen(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(SCREEN_FORGE_RECIPE_OVERVIEW, "Forge Recipes");
		translationBuilder.add(SCREEN_FORGE_RECIPE_OVERVIEW_TITLE, "(%s/%s) Forge Recipes");
		translationBuilder.add(SCREEN_FORGE_RECIPE_OVERVIEW_VIEW_ALL, "View all of the Forge Recipes!");
		translationBuilder.add(SCREEN_FORGE_RECIPE_OVERVIEW_RECIPE, "Forge Recipe");
		translationBuilder.add(SCREEN_FORGE_RECIPE_OVERVIEW_BACK_TO_FORGE_RECIPES, "To Forge Recipes");
	}

	private void addItemSearchScreen(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(SCREEN_ITEM_SEARCH, "Item Search");
		translationBuilder.add(SCREEN_ITEM_SEARCH_HIGHLIGHT, "Highlighting Chests");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT, "Left-click to highlight all chests!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_TOTAL, "Total");
	}

	private void addCleanupConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_CLEANUP,
				"Cleanup",
				"Various cleanup settings, that either hide or modify what you see.");

		translationBuilder.add(CONFIG_CLEANUP_CATEGORIES_COOP, "Coop");

		translationBuilder.addConfig(CONFIG_CLEANUP_COOP_CLEANUP,
				"Collection tooltips",
				"Hides the names of coop members from the collection item.");

		translationBuilder.add(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_KEEP, "Keep");
		translationBuilder.add(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_EMPTY, "Empty");
		translationBuilder.add(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_ALL, "All");
		translationBuilder.add(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_OTHER, "Other");

		translationBuilder.add(CONFIG_CLEANUP_CATEGORIES_DUNGEONS, "Dungeons");

		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_WATCHER_MESSAGES,
				"Hide watcher messages",
				"Hides all watcher messages.");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_POTION_EFFECT_MESSAGE,
				"Hide potion messages",
				"Hides the paused effects message.");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_CLASS_MESSAGES,
				"Hide class messages",
				"Hides the class stat messages.");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_ULTIMATE_READY,
				"Hide ultimate ready",
				"hides the ultimate ready message.");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_BLESSING_MESSAGE,
				"Hide blessing messages",
				"Hides all blessing messages");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_SILVERFISH_MESSAGE,
				"Hide silverfish messages",
				"Hides the silverfish moving message.");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_DUNGEON_KEY_MESSAGE,
				"Hide key messages",
				"Hides the key pickup messages.");

		translationBuilder.add(CONFIG_CLEANUP_CATEGORIES_ITEMS, "Items");

		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_DUNGEON_STATS,
				"Remove dungeon stats",
				"Removes the dungeon stats from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_REFORGE_STATS,
				"Remove reforge stats",
				"Removes the reforge stats from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_HPB_STATS,
				"Remove hpb",
				"Removes the hot potato book stats from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_GEMSTONE_STATS,
				"Removes gemstone stats",
				"Remove the gemstone stats from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_GEAR_SCORE,
				"Remove gear score",
				"Removes the gear score from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_BLANK_LINE,
				"Remove blank lines",
				"Removes blank lines from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_FULL_SET_BONUS,
				"Remove full set bonus",
				"Removes the full set bonus from the item.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_GEMSTONE_LINE,
				"Remove gemstones",
				"Removes the gemstone line from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_ABILITY,
				"Remove abilities",
				"Removes abilities from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_PIECE_BONUS,
				"Remove piece bonus",
				"Remove the piece bonus from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_ENCHANTS, "Remove enchants", "Remove enchants from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_REFORGE, "Remove reforges", "Removes reforges from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_SOULBOUND,
				"Remove soulbound",
				"Removes the soulbound text from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_RUNES, "Remove runes", "Removes runes from items.");

		translationBuilder.add(CONFIG_CLEANUP_CATEGORIES_PETS, "Pets");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_MAX_LEVEL,
				"Remove max level",
				"Removes the max level and xp lines from pets.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_ACTIONS,
				"Remove actions",
				"Removes the left-click and right-click actions from the pet.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_HELD_ITEM,
				"Remove held item",
				"Removes the left-click and right-click actions from the pet.");
	}

	private void addDevConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_DEV, "Dev Config", "Development related config entries.");
		translationBuilder.addConfig(CONFIG_DEV_HIDE_CONSOLE_SPAM,
				"Remove console spam",
				"Removes spam from the console by canceling various logger invocations.");
		translationBuilder.add(CONFIG_DEV_REPO, "Data repository");
		translationBuilder.addConfig(CONFIG_DEV_DATA_REPO, "Data repo", "The github location of the data repo.");
		translationBuilder.addConfig(CONFIG_DEV_DATA_REPO_BRANCH, "Data repo branch", "The branch of the data repo.");
		translationBuilder.addConfig(CONFIG_DEV_BACKEND_RECONNECT, "Press button to ", "Reconnect to the backend.");
		translationBuilder.add(CONFIG_DEV_BACKEND_RECONNECT_VALUE, "Reconnect");
		translationBuilder.addConfig(
				CONFIG_DEV_BACKEND_CONNECT,
				"Connect to backend",
				"Whether the mod should connect to the backend or not.");
		translationBuilder.addConfig(CONFIG_DEV_BACKEND_SERVER, "Server Url", "Used to set the backend server url.");
		translationBuilder.addConfig(
				CONFIG_DEV_BACKEND_VERSION_SUFFIX,
				"Use version scheme",
				"Appends the current api version to the url.");
		translationBuilder.add(CONFIG_DEV_BACKEND, "Backend");
	}

	private void addFarmingConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_FARMING, "Farming Config", "Farming related settings.");
		translationBuilder.addConfig(CONFIG_FARMING_SHOW_PLOT_PRICE_BREAKDOWN,
				"Plot price breakdown",
				"Shows a breakdown of how much compost you need to unlock all plots.");
		translationBuilder.addConfig(CONFIG_FARMING_YAW_PITCH_DISPLAY,
				"Yaw/Pitch display",
				"Displays your yaw/pitch on the screen (in a non obnoxious way).");
		translationBuilder.add(CONFIG_FARMING_CATEGORIES_RANCHERS, "Rancher's Boots");
		translationBuilder.addConfig(CONFIG_FARMING_SHOW_RANCHER_SPEED,
				"Show rancher speed",
				"Shows the speed selected on ranchers boots as item stack size.");
		translationBuilder.addConfig(CONFIG_FARMING_SHOW_RANCHER_OPTIMAL_SPEED,
				"Show rancher overlay",
				"Show optimal speeds in the rancher's boots.");
		translationBuilder.add(CONFIG_FARMING_CATEGORIES_COMPOST, "Composter");
		translationBuilder.addConfig(CONFIG_FARMING_SHOW_COMPOST_PRICE_BREAKDOWN,
				"Compost upgrade price",
				"Shows the amount of items required to max an upgrade.");
		translationBuilder.addConfig(CONFIG_FARMING_COMPOST_SORT_ORDER, "Item sort", "How the items should be sorted" +
                                                                                     ".");
		translationBuilder.add(CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_ASCENDING, "Ascending");
		translationBuilder.add(CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_DESCENDING, "Descending");
		translationBuilder.add(CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_UNSORTED, "Unsorted");

		translationBuilder.add(CONFIG_FARMING_CATEGORIES_VISITOR, "Visitors");
		translationBuilder.addConfig(CONFIG_FARMING_VISITOR_MATERIAL_HELPER,
				"Show visitor materials",
				"Shows the amount of items a visitor needs down to the actual crop.");
		translationBuilder.add(CONFIG_FARMING_CATEGORIES_JACOBS, "Jacob / Contests");
		translationBuilder.addConfig(CONFIG_FARMING_HIGHLIGHT_UNCLAIMED_JACOB_CONTENTS,
				"Highlight unclaimed",
				"Highlight unclaimed jacob contests in his inventory.");
	}

	private void addHelpersConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_HELPERS,
				"Helpers",
				"Settings that help you with keeping track of certain things.");
		translationBuilder.addConfig(CONFIG_HELPERS_CRAFT_HELPER,
				"Craft Helper",
				"Shows the items required to craft something and your progress in the inventory.");
		translationBuilder.addConfig(
				CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS,
				"Craft Helper Location",
				"Edits the location of the craft helper.");
		translationBuilder.add(CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS_BUTTON, "Edit");
		translationBuilder.addConfig(CONFIG_HELPERS_ANVIL_HELPER,
				"Anvil Helper",
				"Highlights the same book in your inventory when combining them in an anvil.");
		translationBuilder.addConfig(CONFIG_HELPERS_CHEST_TRACKER,
				"Chest Tracker",
				"Allows for tracking of chests on private island.");
	}

	private void addMiningConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_MINING, "Mining", "All settings related to mining.");
		translationBuilder.addConfig(CONFIG_MINING_MODIFY_COMMISSIONS,
				"Modify commission items",
				"Visually changes the commission item to represent the stages a commission can be in.");

		translationBuilder.addConfig(CONFIG_MINING_PUZZLER_SOLVER,
				"Puzzler solver",
				"Highlight the correct block for the puzzler.");
		translationBuilder.add(CONFIG_MINING_CATEGORIES_HOTM, "HOTM");
		translationBuilder.addConfig(CONFIG_MINING_SHOW_HOTM_PERK_LEVEL_AS_STACK_SIZE,
				"Show perk as size",
				"Shows the hotm perk level as item stack size.");
		translationBuilder.addConfig(CONFIG_MINING_HIGHLIGHT_DISABLED_HOTM_PERKS,
				"Highlight disabled",
				"Change disabled perks to redstone.");
		translationBuilder.addConfig(CONFIG_MINING_SHOW_NEXT_10_COST,
				"Cost for next 10",
				"Shows the cost for the next 10 levels");
		translationBuilder.addConfig(CONFIG_MINING_SHOW_TOTAL_COST, "Total cost", "Shows the total cost.");
	}

	private void addMiscConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_MISC, "Misc Config", "Miscellaneous settings");
		translationBuilder.addConfig(CONFIG_MISC_ENABLE_SCROLL_TOOLTIPS, "Scrollable Tooltips", """
				Allows you to scroll through tooltips\r
				\r
				CTRL + Scroll -> move horizontal\r
				SHIFT + Scroll -> chop tooltips""");
		translationBuilder.addConfig(CONFIG_MISC_STORAGE_PREVIEW,
				"Storage Preview",
				"Shows a preview of the content in the storage.");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_PING, "Show Ping", "Shows the ping in the action bar");
		translationBuilder.add(CONFIG_MISC_CATEGORIES_ITEMS, "Items");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_ITEM_CREATION_DATE,
				"Creation date",
				"Shows the creation dates of items.");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_ITEM_DONATED_TO_MUSEUM,
				"Donated to museum",
				"Shows whether items are donated to the museum or not.");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_ITEM_NPC_VALUE, "NPC Value", "Show the npc value of items.");
		translationBuilder.add(CONFIG_MISC_CATEGORIES_RENDER, "Render");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_OWN_ARMOR, "Hide own armor", "Hides your own armor.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_OTHER_ARMOR, "Hide others armor", "Hides others armor.");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_DYE_ARMOR,
				"Show armor if dyed",
				"Shows the armor if a dye is applied to it.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_FIRE_ON_ENTITIES, "Hide fire", "Hide fire from entities.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_LIGHTNING_BOLT,
				"Hide lightning",
				"Hide the lightning bolt entity.");
		translationBuilder.add(CONFIG_MISC_CATEGORIES_RENDER_UI, "Render - UI");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_POTION_EFFECTS,
				"Hide inventory potions",
				"Hides potion effects from the inventory.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_HEALTH, "Hide health bar", "Hide health bar from ui.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_ARMOR, "Hide armor bar", "Hide armor bar from ui.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_FOOD, "Hide food bar", "Hide food bar from ui.");
		translationBuilder.add(CONFIG_MISC_CATEGORIES_RENDER_INVENTORY, "Render - Inventory");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_PET_LEVEL,
				"Show pet level",
				"Shows the pet level as stack size.");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_PET_RARITY_IN_LEVEL_TEXT,
				"Show rarity in level",
				"Shows the pet level in the color of the rarity");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_FORGE_RECIPE_STACK,
				"Show forge recipes",
				"Shows forge recipes in the recipe book");
	}
}