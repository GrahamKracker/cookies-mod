package codes.cookies.mod.features.crafthelper;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.features.crafthelper.ui.CraftHelperPanel;
import codes.cookies.mod.features.crafthelper.ui.CraftHelperPanelLine;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.accessors.InventoryScreenAccessor;

import lombok.Getter;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CraftHelperManager {
	private static int currentItemIndex = 0;
	public static int scrollDelta = -100;

	@Nullable
	public static CraftHelperItem getCurrentItem() {
		if (items.isEmpty()) {
			return null;
		}

		return items.get(currentItemIndex);
	}

	private static final ArrayList<CraftHelperItem> items = new ArrayList<>();

	@Getter
	private static CraftHelperLocation location;

	private static CraftHelperPanel panel;

	public static void init() {
		AtomicInteger ticksSinceLastUpdate = new AtomicInteger(0);
		location = ConfigManager.getConfig().helpersConfig.craftHelper.craftHelperLocation.getValue();
		ConfigManager.getConfig().helpersConfig.craftHelper.craftHelperLocation.withCallback((oldValue, newValue) -> location = newValue);
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof InventoryScreenAccessor inventoryScreenAccessor)) {
				return;
			}

			if (!SkyblockUtils.isCurrentlyInSkyblock()) {
				return;
			}

			if (!ConfigManager.getConfig().helpersConfig.craftHelper.craftHelper.getValue()) {
				return;
			}

			var currentItem = getCurrentItem();

			//if (currentItem == null) {
			pushNewCraftHelperItem(new CraftHelperItem(RepositoryItem.of("TITANIUM_DRILL_3"), 2)); //WAND_OF_RESTORATION
			currentItem = getCurrentItem();
			//}

			//if (panel == null) {
			{
				panel = new CraftHelperPanel(calculateWidth(inventoryScreenAccessor), currentItem);
			}
			/*} else {
				panel.init(currentItem);
			}*/

			screen.addDrawableChild(panel);

			ScreenEvents.beforeRender(screen).register((screen1, drawContext, i, i1, v) -> {
				panel.setWidth(calculateWidth(inventoryScreenAccessor));
				var position = getPosition(inventoryScreenAccessor, panel);
				panel.setX(position.x());
				panel.setY(position.y());
			});

			ScreenEvents.afterTick(screen).register((screen1) -> {
				if (ticksSinceLastUpdate.getAndIncrement() > 60) {
					panel.getLines().forEach(CraftHelperPanelLine::update);
					ticksSinceLastUpdate.set(0);
				}
			});

			ScreenEvents.remove(screen).register((screen1) -> {
				panel.children().clear();
			});
		});
	}

	private static int calculateRightEdge(InventoryScreenAccessor screen) {
		return screen.cookies$getBackgroundWidth() + screen.cookies$getX();
	}

	private static int calculateLeftEdge(InventoryScreenAccessor screen) {
		if (screen instanceof RecipeBookScreen<?> recipeBookScreen && recipeBookScreen.recipeBook.isOpen()) {
			return recipeBookScreen.recipeBook.getLeft() - 35; //magic number is the width of the tabs on the left TODO: make all the magic numbers final static fields
		}
		return (((Screen) screen).width - screen.cookies$getBackgroundWidth()) / 2;

	}

	private static int calculateWidth(InventoryScreenAccessor screen) {
		return switch (location) {
			case LEFT -> calculateLeftEdge(screen) - 3;
			case RIGHT -> MinecraftClient.getInstance().getWindow().getScaledWidth() - calculateRightEdge(screen) - 3;
		};
	}

	private static int calculateX(InventoryScreenAccessor screen) {
		return switch (location) {
			case LEFT -> 0;
			case RIGHT -> calculateRightEdge(screen);
		};
	}

	private static Vector2ic getPosition(InventoryScreenAccessor screen, CraftHelperPanel panel) {
		int x = calculateX(screen);
		int y = screen.cookies$getY() + screen.cookies$getBackgroundHeight() / 2;
		preventOverflow(MinecraftClient.getInstance().getWindow().getScaledHeight(), new Vector2i(x, y), panel.getHeight());
		return new Vector2i(x, y);
	}

	private static void preventOverflow(int screenHeight, Vector2i pos, int height) {
		int i = height + 3;
		if (pos.y + i > screenHeight) {
			pos.y = screenHeight - i;
		}
	}

	public static void pushNewCraftHelperItem(CraftHelperItem item) {
		items.addFirst(item);
	}

	public static void pushNewCraftHelperItem(RepositoryItem repositoryItemNotNull, int i) {
		items.addFirst(new CraftHelperItem(repositoryItemNotNull, i));
	}

	public static void popCraftHelperItem() {
		items.removeFirst();
	}

	public static void popCraftHelperItem(int i) {
		items.remove(i);
	}


	public static void close() {
	}
}
