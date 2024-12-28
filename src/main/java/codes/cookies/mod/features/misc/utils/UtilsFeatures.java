package codes.cookies.mod.features.misc.utils;

import codes.cookies.mod.features.Loader;

/**
 * Utility class to load all utility features.
 */
@SuppressWarnings("MissingJavadoc")
public class UtilsFeatures {

	public static void load() {
		Loader.load("CraftHelper", codes.cookies.mod.features.crafthelper.CraftHelperManager::init);
		Loader.load("ModifiedRecipeScreen", ModifyRecipeScreen::new);
		Loader.load("StoragePreview", StoragePreview::new);
		Loader.load("AnvilHelper", AnvilHelper::new);
		Loader.load("ForgeRecipes", ForgeRecipes::new);
		Loader.load("StatsTracker", StatsTracker::init);
	}

}
